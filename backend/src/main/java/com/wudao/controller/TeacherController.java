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

    @Autowired
    private com.wudao.service.AliyunOssService aliyunOssService;

    @GetMapping("/list")
    public Result<List<Teacher>> getTeachers() {
        log.info("[REST API GET /api/teacher/list] 从数据库 sys_teacher 查询教师列表...");
        List<Teacher> list = teacherMapper.selectAllTeachers();
        if (list != null) {
            for (Teacher teacher : list) {
                String dbPath = teacher.getAvatarUrl();
                if (org.springframework.util.StringUtils.hasText(dbPath)) {
                    String fullUrl = aliyunOssService.toFullUrl(dbPath);
                    teacher.setAvatarUrl(fullUrl);
                    log.info("[DEBUG TEACHER LIST] 教师姓名: {}, 数据库保存Path: {}, 转换公网FullUrl: {}", teacher.getName(), dbPath, fullUrl);
                } else {
                    log.warn("[DEBUG TEACHER LIST] 教师姓名: {} 的 avatarUrl 为空", teacher.getName());
                }
            }
        }
        log.info("[REST API GET /api/teacher/list] 成功获取 {} 位教师记录.", list != null ? list.size() : 0);
        return Result.success("获取成功", list);
    }

    @PostMapping("/create")
    public Result<Teacher> createTeacher(@RequestBody Teacher teacher) {
        log.info("[REST API POST /api/teacher/create] 收到保存教师配置请求: teacherId={}, name={}, avatarUrl={}",
                teacher != null ? teacher.getTeacherId() : null,
                teacher != null ? teacher.getName() : null,
                teacher != null ? teacher.getAvatarUrl() : null);

        if (teacher == null) {
            return Result.error("请求参数不能为空！");
        }
        if (!org.springframework.util.StringUtils.hasText(teacher.getName())) {
            return Result.error("请输入教师姓名！");
        }
        if (!org.springframework.util.StringUtils.hasText(teacher.getAvatarUrl())) {
            return Result.error("请选择并上传教师肖像照片！");
        }

        // 提取相对路径
        String rawAvatar = teacher.getAvatarUrl();
        String relativePath = aliyunOssService.toRelativePath(rawAvatar);
        teacher.setAvatarUrl(relativePath);

        // 1. 优先根据 ID 检索数据库现存记录
        Teacher existingById = org.springframework.util.StringUtils.hasText(teacher.getTeacherId())
                ? teacherMapper.selectTeacherById(teacher.getTeacherId())
                : null;

        // 2. 其次根据姓名检索数据库现存记录
        Teacher existingByName = teacherMapper.selectTeacherByName(teacher.getName().trim());

        Teacher existing = existingById != null ? existingById : existingByName;

        if (existing != null) {
            // 已有记录，强制复用现有的 teacherId 执行 UPDATE 更新
            teacher.setTeacherId(existing.getTeacherId());
            log.info("[DEBUG TEACHER UPSERT] 匹配到现存记录 (ID={}, Name={}), 执行 UPDATE 更新覆盖...", existing.getTeacherId(), existing.getName());
            teacherMapper.updateTeacher(teacher);
        } else {
            // 无匹配记录，检查 ID 是否已被占用，如果已被占用或无 ID 则自动生成全新 Snowflake ID
            if (!org.springframework.util.StringUtils.hasText(teacher.getTeacherId()) 
                    || teacherMapper.selectTeacherById(teacher.getTeacherId()) != null) {
                teacher.setTeacherId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
            }
            log.info("[DEBUG TEACHER UPSERT] 无现存匹配记录, 分配新 ID={}, 执行 INSERT 插入...", teacher.getTeacherId());
            teacherMapper.insertTeacher(teacher);
        }

        // 转换为全路径返回给前端
        String fullUrl = aliyunOssService.toFullUrl(teacher.getAvatarUrl());
        teacher.setAvatarUrl(fullUrl);
        log.info("[DEBUG TEACHER UPSERT] 教师配置保存成功! 返回前端 FullUrl={}", fullUrl);

        return Result.success("教师档案添加成功！", teacher);
    }
}
