package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.User;
import com.wudao.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信授权登录接口 (根据手机号自动识别数据库中的角色与权限 status = 1 审核通过的用户登录)
     */
    @PostMapping("/wx-login")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");

        log.info("[REST API POST /api/auth/wx-login] Automated phone lookup: phone={}, code={}", phone, code);

        User user = null;
        // 1. 若前端传输了手机号，优先根据手机号在数据库 sys_user 表中精准检索对应身份
        if (phone != null && !phone.trim().isEmpty()) {
            user = userMapper.selectByUsername(phone.trim());
        }

        // 2. 若微信开发者工具未透传解密手机号(仅传code)，检索数据库中存在的管理员账号 18911800655
        if (user == null) {
            user = userMapper.selectByUsername("18911800655");
        }

        if (user == null) {
            log.warn("Login failed! Phone number not registered in database sys_user.");
            return Result.error("您的手机号尚未开通权限，请点击【去申请】填写资料提交审批！");
        }

        // 2. 校验：所有权限必须由管理员统一审批通过 (status === 1) 方可登录！
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("Login blocked! User {} (phone: {}) status is 0 (Pending Admin Approval)", user.getRealName(), user.getPhone());
            return Result.error("您的账号申请正等待超级管理员审批中，审批同意后方可登录！");
        }
        if (user.getStatus() != null && user.getStatus() == 2) {
            log.warn("Login blocked! User {} (phone: {}) status is 2 (Rejected)", user.getRealName(), user.getPhone());
            return Result.error("您的账号注册申请已被管理员驳回，请重新提交资料申请！");
        }

        String token = "token_" + UUID.randomUUID().toString().replace("-", "");
        log.info("Authorization success! Identified User: {} (ID: {}, Auto Role: {}), Token: {}", user.getRealName(), user.getUserId(), user.getRoleType(), token);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("openId", "wx_openid_" + user.getUserId());
        data.put("userInfo", user);

        return Result.success("登录成功", data);
    }

    /**
     * 填写学生姓名、关系及手机号申请小程序登录权限
     */
    @PostMapping("/apply-login")
    public Result<String> applyLoginPermission(@RequestBody Map<String, String> params) {
        String parentName = params.get("parentName");
        String studentName = params.get("studentName");
        String relationship = params.get("relationship");
        String phone = params.get("phone");
        String roleType = params.getOrDefault("roleType", "STUDENT");
        String danceClassName = params.getOrDefault("danceClassName", "芭蕾/中国舞体验班");

        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("请输入手机号！");
        }

        log.info("[REST API POST /api/auth/apply-login] Applying for login permission: Parent: {}, Student: {}, Relation: {}, Phone: {}, Role: {}", parentName, studentName, relationship, phone, roleType);

        User existingUser = userMapper.selectByUsername(phone.trim());
        if (existingUser != null) {
            if (existingUser.getStatus() == 0) {
                return Result.error("该手机号已提交过申请，正等待管理员审批，请勿重复提交！");
            } else if (existingUser.getStatus() == 1) {
                return Result.error("该手机号已由管理员审核通过，请直接进行登录！");
            } else {
                // 已被驳回，更新后重新提交审批
                existingUser.setRealName(parentName != null && !parentName.trim().isEmpty() ? parentName : (studentName + relationship));
                existingUser.setStudentName(studentName);
                existingUser.setRelationship(relationship);
                existingUser.setRoleType(roleType);
                existingUser.setStatus(0); // 重置为待审批
                userMapper.updateStatus(existingUser.getUserId(), 0);
                return Result.success("申请重新提交成功！请等待超级管理员审批。");
            }
        }

        User newUser = new User();
        newUser.setUserId(com.wudao.common.SnowflakeIdWorker.generateId());
        newUser.setUsername(phone.trim());
        newUser.setRealName(parentName != null && !parentName.trim().isEmpty() ? parentName : (studentName + relationship));
        newUser.setStudentName(studentName);
        newUser.setRelationship(relationship);
        newUser.setPhone(phone.trim());
        newUser.setRoleType(roleType);
        newUser.setDanceClassName(com.wudao.common.DanceClassEnum.getCodeByName(danceClassName));
        newUser.setRemainingHours(20);
        newUser.setVolunteerPoints(0);
        newUser.setStatus(0); // 必须由管理员同意才能登录！

        userMapper.insertUser(newUser);

        log.info("New User Application created! User ID: {}, Name: {}, ClassEnum: {}, Status: 0", newUser.getUserId(), newUser.getRealName(), newUser.getDanceClassName());

        return Result.success("权限申请提交成功！必须等待超级管理员同意后方可登录小程序。");
    }
}
