package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.StudentProfile;
import com.wudao.entity.User;
import com.wudao.mapper.UserMapper;
import com.wudao.service.StudentProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/student-profile")
public class StudentProfileController {

    private static final Logger log = LoggerFactory.getLogger(StudentProfileController.class);

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/my")
    public Result<StudentProfile> getMyProfile(@RequestParam(required = false) String studentId) {
        log.info("[REST API GET /api/student-profile/my] Fetching profile for studentId: {}", studentId);
        StudentProfile p = studentProfileService.getProfileByStudentId(studentId);
        if (p == null && StringUtils.hasText(studentId)) {
            User user = userMapper.selectById(studentId);
            p = new StudentProfile();
            p.setStudentId(studentId);
            p.setStudentName(user != null ? (StringUtils.hasText(user.getStudentName()) ? user.getStudentName() : user.getRealName()) : "");
            p.setGradeLevel(user != null && StringUtils.hasText(user.getDanceClassName()) ? user.getDanceClassName() : "二年级");
            p.setChineseScore(0.0);
            p.setMathScore(0.0);
            p.setEnglishScore(0.0);
            p.setHeightCm(0.0);
            p.setWeightKg(0.0);
            p.setBustCm(0.0);
            p.setWaistCm(0.0);
            p.setHipCm(0.0);
            p.setShoeSize(0.0);
            p.setParentName(user != null ? user.getRealName() : "");
            p.setParentPhone(user != null ? user.getPhone() : "");
            p.setUpdatedAt(new Date());
        }
        return Result.success(p);
    }

    @PostMapping("/save")
    public Result<StudentProfile> saveProfile(@RequestBody StudentProfile dto) {
        log.info("[REST API POST /api/student-profile/save] Saving student profile in MySQL: studentName={}, studentId={}", dto != null ? dto.getStudentName() : null, dto != null ? dto.getStudentId() : null);
        StudentProfile saved = studentProfileService.saveOrUpdateProfile(dto);
        return Result.success("学员档案与成绩信息成功保存！", saved);
    }

    @GetMapping({"/scores", "/list"})
    public Result<List<StudentProfile>> getScores(@RequestParam(required = false) String gradeLevel) {
        log.info("[REST API GET /api/student-profile/scores] Querying student profiles list for gradeLevel: {}", gradeLevel);
        List<StudentProfile> list = studentProfileService.getAllProfiles(gradeLevel);
        return Result.success(list);
    }
}
