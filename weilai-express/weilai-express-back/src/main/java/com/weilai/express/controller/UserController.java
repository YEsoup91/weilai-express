package com.weilai.express.controller;

import com.weilai.express.dto.LoginDTO;
import com.weilai.express.dto.RegisterDTO;
import com.weilai.express.entity.User;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.Result;
import com.weilai.express.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }
}
