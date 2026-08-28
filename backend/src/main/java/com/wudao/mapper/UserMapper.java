package com.wudao.mapper;

import com.wudao.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    List<User> selectAll();
    User selectById(@Param("userId") Long userId);
    User selectByUsername(@Param("username") String username);
    User selectFirstByRole(@Param("roleType") String roleType);
    List<User> selectPendingUsers(@Param("danceClassName") String danceClassName);
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);
    int updatePoints(@Param("userId") Long userId, @Param("points") Integer points);
    int insertUser(User user);
}
