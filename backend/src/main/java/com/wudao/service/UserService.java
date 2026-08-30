package com.wudao.service;

import com.wudao.entity.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(String userId);
    List<User> getPendingUsers(String danceClassName);
    User approveUser(String userId, Integer status);
    User registerUser(User user);
}
