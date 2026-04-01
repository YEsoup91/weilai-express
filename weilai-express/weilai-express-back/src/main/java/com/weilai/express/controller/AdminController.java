package com.weilai.express.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weilai.express.entity.Role;
import com.weilai.express.entity.User;
import com.weilai.express.service.RoleService;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理员后台")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    @Operation(summary = "获取用户列表")
    public Result<List<User>> getUserList(@RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or()
                   .like(User::getNickname, keyword);
        }
        
        List<User> userList = userService.list(wrapper);
        return Result.success(userList);
    }

    @GetMapping("/roles")
    @Operation(summary = "获取角色列表")
    public Result<List<Role>> getRoleList() {
        List<Role> roleList = roleService.list();
        return Result.success(roleList);
    }

    @PostMapping("/role")
    @Operation(summary = "分配角色")
    public Result<Void> assignRole(@RequestBody Map<String, Object> data) {
        Long userId = ((Number) data.get("userId")).longValue();
        Long roleId = ((Number) data.get("roleId")).longValue();
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        
        Role role = roleService.getById(roleId);
        if (role == null) {
            return Result.error(404, "角色不存在");
        }
        
        user.setRoleId(roleId);
        userService.updateById(user);
        
        return Result.success();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取统计数据")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("merchantCount", userService.count(new LambdaQueryWrapper<User>().eq(User::getRoleId, 2)));
        stats.put("riderCount", userService.count(new LambdaQueryWrapper<User>().eq(User::getRoleId, 3)));
        stats.put("orderCount", 0); // TODO: 实现订单统计
        
        return Result.success(stats);
    }
}
