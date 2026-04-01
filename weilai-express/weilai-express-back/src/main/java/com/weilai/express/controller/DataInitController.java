package com.weilai.express.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.weilai.express.entity.User;
import com.weilai.express.enums.RoleEnum;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "数据初始化")
@RestController
@RequestMapping("/init")
public class DataInitController {

    private final UserService userService;

    public DataInitController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @Operation(summary = "初始化测试用户")
    public Result<String> initTestUsers() {
        createTestUser("user001", "测试用户", "13800138001", RoleEnum.USER);
        createTestUser("merchant001", "美味餐厅", "13800138002", RoleEnum.MERCHANT);
        createTestUser("rider001", "快递小哥", "13800138003", RoleEnum.RIDER);
        createTestUser("admin001", "管理员", "13800138004", RoleEnum.ADMIN);

        String message = "✅ 测试用户初始化完成！\n\n" +
                "账号列表：\n" +
                "user001 / 123456 (普通用户)\n" +
                "merchant001 / 123456 (商家)\n" +
                "rider001 / 123456 (骑手)\n" +
                "admin001 / 123456 (管理员)";
        
        return Result.success(message);
    }

    private void createTestUser(String username, String nickname, String phone, RoleEnum role) {
        // 检查是否已存在
        User existUser = userService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        if (existUser != null) {
            // 如果已存在，更新密码
            existUser.setPassword(BCrypt.hashpw("123456"));
            userService.updateById(existUser);
            System.out.println("✅ 已更新用户：" + username);
        } else {
            // 创建新用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(BCrypt.hashpw("123456"));
            user.setNickname(nickname);
            user.setPhone(phone);
            user.setEmail(username + "@test.com");
            user.setRoleId(role.getCode().longValue());
            user.setStatus(1);
            userService.save(user);
            System.out.println("✅ 已创建用户：" + username);
        }
    }
}
