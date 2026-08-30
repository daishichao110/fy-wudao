package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.Teacher;
import com.wudao.mapper.TeacherMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private static final Logger log = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/list")
    public Result<List<Teacher>> getTeachers() {
        log.info("[REST API GET /api/teacher/list] Querying teacher roster from MySQL table sys_teacher...");
        List<Teacher> list = teacherMapper.selectAllTeachers();
        log.info("[REST API GET /api/teacher/list] Fetched {} teachers.", list.size());
        return Result.success("获取成功", list);
    }

    @PostMapping("/create")
    public Result<Teacher> createTeacher(@RequestBody Teacher teacher) {
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            return Result.error("请输入教师姓名！");
        }

        if (teacher.getTitle() == null || teacher.getTitle().trim().isEmpty()) {
            teacher.setTitle("专业舞蹈导师");
        }

        if (teacher.getDanceType() == null || teacher.getDanceType().trim().isEmpty()) {
            teacher.setDanceType("芭蕾舞/中国舞");
        }

        if (teacher.getAvatarUrl() == null || teacher.getAvatarUrl().trim().isEmpty()) {
            teacher.setAvatarUrl("/image/teacher1.jpg");
        }

        log.info("[REST API POST /api/teacher/create] Adding new teacher bio: Name={}, Title={}", teacher.getName(), teacher.getTitle());
        if (teacher.getTeacherId() == null || teacher.getTeacherId() <= 0) {
            teacher.setTeacherId(com.wudao.common.SnowflakeIdWorker.generateId());
        }
        teacherMapper.insertTeacher(teacher);
        log.info("[REST API POST /api/teacher/create] Saved teacher ID: {}", teacher.getTeacherId());

        return Result.success("教师档案添加成功！", teacher);
    }
}
