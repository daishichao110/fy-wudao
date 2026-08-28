package com.wudao.service;

import com.wudao.entity.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long userId);
    List<User> getPendingUsers(String danceClassName);
    User approveUser(Long userId, Integer status);
    User registerUser(User user);
}
