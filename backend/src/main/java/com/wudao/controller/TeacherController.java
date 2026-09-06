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
        log.info("[REST API POST /api/teacher/create] 收到设置教师信息请求, 原始Name={}, 原始AvatarUrl={}", 
                teacher != null ? teacher.getName() : null, 
                teacher != null ? teacher.getAvatarUrl() : null);

        if (teacher == null) {
            return Result.error("请求参数不能为空！");
        }
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            return Result.error("请输入教师姓名！");
        }
        if (teacher.getAvatarUrl() == null || teacher.getAvatarUrl().trim().isEmpty()) {
            return Result.error("请选择并上传教师肖像照片！");
        }
        if (teacher.getTitle() == null || teacher.getTitle().trim().isEmpty()) {
            return Result.error("请输入教师职称头衔！");
        }
        if (teacher.getDanceType() == null || teacher.getDanceType().trim().isEmpty()) {
            return Result.error("请输入教师擅长舞种！");
        }

        // 提取相对路径写入数据库，拒绝硬编码默认图
        String rawAvatar = teacher.getAvatarUrl();
        String relativePath = aliyunOssService.toRelativePath(rawAvatar);
        teacher.setAvatarUrl(relativePath);
        log.info("[DEBUG TEACHER CREATE] 相对路径提取结果: 原始Url={} -> 提取Key={}", rawAvatar, relativePath);

        // 检查数据库是否存在同名教师（存在则更新，不存在则插入）
        Teacher existing = teacherMapper.selectTeacherByName(teacher.getName().trim());
        if (existing != null) {
            log.info("[DEBUG TEACHER CREATE] 数据库已存在同名教师记录, 执行 UPDATE 更新操作. ExistingID={}, Name={}", existing.getTeacherId(), existing.getName());
            teacher.setTeacherId(existing.getTeacherId());
            teacherMapper.updateTeacher(teacher);
        } else {
            if (!org.springframework.util.StringUtils.hasText(teacher.getTeacherId())) {
                teacher.setTeacherId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
            }
            log.info("[DEBUG TEACHER CREATE] 新建教师档案, 执行 INSERT 插入操作. NewID={}, Name={}", teacher.getTeacherId(), teacher.getName());
            teacherMapper.insertTeacher(teacher);
        }

        // 重新转换为全路径返回给前端
        String returnFullUrl = aliyunOssService.toFullUrl(teacher.getAvatarUrl());
        teacher.setAvatarUrl(returnFullUrl);
        log.info("[DEBUG TEACHER CREATE] 教师档案保存/更新完毕! 返回前端 FullUrl={}", returnFullUrl);

        return Result.success("教师档案添加成功！", teacher);
    }
}
