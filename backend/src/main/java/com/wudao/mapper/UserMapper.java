package com.wudao.mapper;

import com.wudao.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    List<User> selectAll();
    User selectById(@Param("userId") String userId);
    User selectByRealName(@Param("realName") String realName);
    User selectByUsername(@Param("username") String username);
    User selectFirstByRole(@Param("roleType") String roleType);
    List<User> selectPendingUsers(@Param("danceClassName") String danceClassName);
    int updateStatus(@Param("userId") String userId, @Param("status") Integer status);
    int updatePoints(@Param("userId") String userId, @Param("points") Integer points);
    int insertUser(User user);
}
