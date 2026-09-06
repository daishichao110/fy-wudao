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
 log.info("[REST API GET /api/teacher/list] Querying teacher roster from MySQL table sys_teacher...");
 List<Teacher> list = teacherMapper.selectAllTeachers();
 if (list != null) {
 for (Teacher teacher : list) {
 if (org.springframework.util.StringUtils.hasText(teacher.getAvatarUrl())) {
 teacher.setAvatarUrl(aliyunOssService.toFullUrl(teacher.getAvatarUrl()));
 }
 }
 }
 log.info("[REST API GET /api/teacher/list] Fetched {} teachers.", list.size());
 return Result.success("获取成功", list);
 }

 @PostMapping("/create")
 public Result<Teacher> createTeacher(@RequestBody Teacher teacher) {
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
 teacher.setAvatarUrl(aliyunOssService.toRelativePath(teacher.getAvatarUrl()));

 log.info("[REST API POST /api/teacher/create] Adding new teacher bio: Name={}, Title={}", teacher.getName(), teacher.getTitle());
 if (!org.springframework.util.StringUtils.hasText(teacher.getTeacherId())) {
 teacher.setTeacherId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
 }
 teacherMapper.insertTeacher(teacher);
 log.info("[REST API POST /api/teacher/create] Saved teacher ID: {}", teacher.getTeacherId());

 // 重新转换为全路径返回给前端
 teacher.setAvatarUrl(aliyunOssService.toFullUrl(teacher.getAvatarUrl()));
 return Result.success("教师档案添加成功！", teacher);
 }
}
