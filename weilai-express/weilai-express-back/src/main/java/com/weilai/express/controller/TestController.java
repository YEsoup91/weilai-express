package com.weilai.express.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.weilai.express.entity.User;
import com.weilai.express.enums.RoleEnum;
import com.weilai.express.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "测试数据管理")
@RestController
@RequestMapping("/test")
public class TestController {

    private final UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/init-users")
    @Operation(summary = "初始化测试用户")
    public String initTestUsers() {
        // 创建测试用户的密码
        String password = BCrypt.hashpw("123456");

        // 普通用户
        User user1 = new User();
        user1.setUsername("user001");
        user1.setPassword(password);
        user1.setNickname("测试用户");
        user1.setPhone("13800138001");
        user1.setEmail("user@test.com");
        user1.setRoleId(RoleEnum.USER.getCode().longValue());
        user1.setStatus(1);
        saveUserIfNotExists(user1);

        // 商家用户
        User user2 = new User();
        user2.setUsername("merchant001");
        user2.setPassword(password);
        user2.setNickname("美味餐厅老板");
        user2.setPhone("13800138002");
        user2.setEmail("merchant@test.com");
        user2.setRoleId(RoleEnum.MERCHANT.getCode().longValue());
        user2.setStatus(1);
        saveUserIfNotExists(user2);

        // 骑手用户
        User user3 = new User();
        user3.setUsername("rider001");
        user3.setPassword(password);
        user3.setNickname("快递小哥");
        user3.setPhone("13800138003");
        user3.setEmail("rider@test.com");
        user3.setRoleId(RoleEnum.RIDER.getCode().longValue());
        user3.setStatus(1);
        saveUserIfNotExists(user3);

        // 管理员
        User user4 = new User();
        user4.setUsername("admin001");
        user4.setPassword(password);
        user4.setNickname("系统管理员");
        user4.setPhone("13800138004");
        user4.setEmail("admin@test.com");
        user4.setRoleId(RoleEnum.ADMIN.getCode().longValue());
        user4.setStatus(1);
        saveUserIfNotExists(user4);

        return "测试用户初始化完成！\n\n" +
                "账号列表：\n" +
                "1. user001 / 123456 (普通用户)\n" +
                "2. merchant001 / 123456 (商家)\n" +
                "3. rider001 / 123456 (骑手)\n" +
                "4. admin001 / 123456 (管理员)";
    }

    private void saveUserIfNotExists(User user) {
        User existUser = userService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername())
        );
        if (existUser == null) {
            userService.save(user);
        }
    }
}
