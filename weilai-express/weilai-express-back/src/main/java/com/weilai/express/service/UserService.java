package com.weilai.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weilai.express.dto.LoginDTO;
import com.weilai.express.dto.RegisterDTO;
import com.weilai.express.entity.User;
import com.weilai.express.vo.Result;
import com.weilai.express.vo.UserVO;

public interface UserService extends IService<User> {

    Result<UserVO> login(LoginDTO loginDTO);

    Result<UserVO> register(RegisterDTO registerDTO);

    Result<UserVO> getUserInfo(Long userId);

    Result<Void> updateUserInfo(User user);
}
