package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.User;
import com.wudao.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> listUsers() {
        log.info("[REST API GET /api/user/list] Fetching all system users");
        List<User> users = userService.getAllUsers();
        log.info("[REST API GET /api/user/list] Response count: {}", users.size());
        return Result.success(users);
    }

    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable("id") String id) {
        log.info("[REST API GET /api/user/info/{}] Querying user info", id);
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @GetMapping("/pending-approvals")
    public Result<List<User>> getPendingApprovals(
            @RequestParam(value = "approverRole", required = false) String approverRole,
            @RequestParam(value = "danceClassName", required = false) String danceClassName) {
        log.info("[REST API GET /api/user/pending-approvals] Approver role: {}, class: {}", approverRole, danceClassName);
        List<User> list = userService.getPendingUsers(danceClassName);
        return Result.success(list);
    }

    @PostMapping("/approve")
    public Result<User> approveUser(@RequestParam("userId") String userId, @RequestParam("status") Integer status) {
        log.info("[REST API POST /api/user/approve] userId: {}, status: {}", userId, status);
        User user = userService.approveUser(userId, status);
        String actionStr = (status == 1) ? "审批通过" : "已驳回";
        return Result.success("用户 " + user.getRealName() + " " + actionStr, user);
    }

    @PostMapping("/register")
    public Result<User> registerUser(@RequestBody User user) {
        log.info("[REST API POST /api/user/register] Registering realName: {}", user != null ? user.getRealName() : "NULL");
        User result = userService.registerUser(user);
        if (result.getStatus() == 1) {
            return Result.success("注册成功！您的学员账号免审批直接生效。", result);
        } else {
            String roleTip = "TEACHER".equalsIgnoreCase(result.getRoleType()) ? "【管理员】" : "【专业老师/管理员】";
            return Result.success("注册申请已提交！正在等待" + roleTip + "审核开通。", result);
        }
    }
}
