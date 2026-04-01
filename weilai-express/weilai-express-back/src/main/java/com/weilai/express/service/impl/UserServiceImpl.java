package com.weilai.express.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weilai.express.dto.LoginDTO;
import com.weilai.express.dto.RegisterDTO;
import com.weilai.express.entity.Role;
import com.weilai.express.entity.User;
import com.weilai.express.enums.RoleEnum;
import com.weilai.express.exception.BusinessException;
import com.weilai.express.mapper.UserMapper;
import com.weilai.express.service.RoleService;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.Result;
import com.weilai.express.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RoleService roleService;

    public UserServiceImpl(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public Result<UserVO> login(LoginDTO loginDTO) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 使用 BCrypt 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        Role role = roleService.getById(user.getRoleId());
        if (role != null) {
            userVO.setRoleName(role.getRoleName());
            userVO.setRoleCode(role.getRoleCode());
        }
        
        return Result.success(userVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<UserVO> register(RegisterDTO registerDTO) {
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerDTO.getUsername()));
        
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setRoleId(RoleEnum.USER.getCode().longValue());
        user.setStatus(1);
        
        this.save(user);
        
        return getUserInfo(user.getId());
    }

    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        Role role = roleService.getById(user.getRoleId());
        if (role != null) {
            userVO.setRoleName(role.getRoleName());
        }
        
        return Result.success(userVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateUserInfo(User user) {
        this.updateById(user);
        return Result.success();
    }
}
