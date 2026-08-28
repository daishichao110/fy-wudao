package com.wudao.service.impl;

import com.wudao.entity.User;
import com.wudao.mapper.UserMapper;
import com.wudao.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        log.info("[UserService] Executing getAllUsers()...");
        return userMapper.selectAll();
    }

    @Override
    public User getUserById(Long userId) {
        log.info("[UserService] Executing getUserById() for userId: {}", userId);
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不合法");
        }
        return userMapper.selectById(userId);
    }

    @Override
    public List<User> getPendingUsers(String danceClassName) {
        log.info("[UserService] Querying pending users for class: {}", danceClassName);
        return userMapper.selectPendingUsers(danceClassName);
    }

    @Override
    @Transactional
    public User approveUser(Long userId, Integer status) {
        log.info("[UserService] Executing approveUser for userId: {}, newStatus: {}", userId, status);
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("操作用户ID不合法");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new IllegalArgumentException("审批状态参数不合法(1-通过 2-驳回)");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("目标审核用户不存在");
        }

        userMapper.updateStatus(userId, status);
        user.setStatus(status);
        log.info("[UserService] User approval finished for {}. Status updated to {}", user.getRealName(), status);
        return user;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        log.info("[UserService] Executing registerUser for realName: {}, roleType: {}", user != null ? user.getRealName() : "NULL", user != null ? user.getRoleType() : "NULL");
        if (user == null || !StringUtils.hasText(user.getRealName())) {
            throw new IllegalArgumentException("注册姓名不可为空");
        }
        if (!StringUtils.hasText(user.getRoleType())) {
            user.setRoleType("STUDENT");
        }

        // 规则：所有注册申请 (学员/家长、专业老师、家委干部) 均需管理员同意后方可登录！
        user.setStatus(0);
        user.setRemainingHours(20);
        user.setVolunteerPoints(0);

        if (!StringUtils.hasText(user.getUsername())) {
            user.setUsername("user_" + System.currentTimeMillis());
        }

        if (user.getUserId() == null || user.getUserId() <= 0) {
            user.setUserId(com.wudao.common.SnowflakeIdWorker.generateId());
        }

        userMapper.insertUser(user);
        log.info("[UserService] User application registered successfully. Assigned userId: {}, status: 0", user.getUserId());
        return user;
    }
}
