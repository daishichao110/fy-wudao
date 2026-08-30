package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.User;
import com.wudao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/export")
public class StudentExportController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/students")
    public Result<String> exportStudents() {
        StringBuilder csv = new StringBuilder();
        csv.append("学员ID,账号名,真实姓名,关联学生,与学生关系,手机号,身份角色,所在班级\n");
        List<User> list = userMapper.selectAllUsers();
        if (list != null) {
            for (User user : list) {
                csv.append(user.getUserId()).append(",")
                   .append(user.getUsername() != null ? user.getUsername() : "").append(",")
                   .append(user.getRealName() != null ? user.getRealName() : "").append(",")
                   .append(user.getStudentName() != null ? user.getStudentName() : "").append(",")
                   .append(user.getRelationship() != null ? user.getRelationship() : "").append(",")
                   .append(user.getPhone() != null ? user.getPhone() : "").append(",")
                   .append(user.getRoleType() != null ? user.getRoleType() : "").append(",")
                   .append(user.getDanceClassName() != null ? user.getDanceClassName() : "").append("\n");
            }
        }
        return Result.success(csv.toString());
    }
}
