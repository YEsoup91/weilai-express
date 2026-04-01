package com.weilai.express.controller;

import com.weilai.express.dto.LoginDTO;
import com.weilai.express.dto.RegisterDTO;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.Result;
import com.weilai.express.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Result<UserVO> result = userService.login(loginDTO);
        
        // 生成简单的 token（实际项目应该用 JWT）
        String token = UUID.randomUUID().toString().replace("-", "");
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userInfo", result.getData());
        
        return Result.success(response);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<UserVO> getUserInfo(@RequestParam Long userId) {
        return userService.getUserInfo(userId);
    }
}
