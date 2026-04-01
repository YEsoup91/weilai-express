package com.weilai.express.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.weilai.express.entity.User;
import com.weilai.express.enums.RoleEnum;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "调试工具")
@RestController
@RequestMapping("/debug")
public class DebugController {

    private final UserService userService;

    public DebugController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/password")
    @Operation(summary = "生成密码哈希")
    public Result<String> generatePassword(@RequestParam String password) {
        String hash = BCrypt.hashpw(password);
        System.out.println("密码：" + password);
        System.out.println("哈希：" + hash);
        return Result.success(hash);
    }

    @PostMapping("/check-password")
    @Operation(summary = "验证密码")
    public Result<Boolean> checkPassword(
            @RequestParam String password,
            @RequestParam String hash) {
        boolean match = BCrypt.checkpw(password, hash);
        System.out.println("密码：" + password);
        System.out.println("哈希：" + hash);
        System.out.println("匹配：" + match);
        return Result.success(match);
    }

    @PostMapping("/reset-user-password")
    @Operation(summary = "重置用户密码")
    public Result<Void> resetUserPassword(
            @RequestParam String username,
            @RequestParam String newPassword) {
        User user = userService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        String hashedPassword = BCrypt.hashpw(newPassword);
        user.setPassword(hashedPassword);
        userService.updateById(user);

        System.out.println("已重置用户 " + username + " 的密码");
        System.out.println("新密码哈希：" + hashedPassword);

        return Result.success();
    }
}
