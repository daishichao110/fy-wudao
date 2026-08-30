package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.StudentProfile;
import com.wudao.entity.User;
import com.wudao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/student-profile")
public class StudentProfileController {

    @Autowired
    private UserMapper userMapper;

    private final ConcurrentHashMap<String, StudentProfile> profileStore = new ConcurrentHashMap<>();

    @GetMapping("/my")
    public Result<StudentProfile> getMyProfile(@RequestParam(required = false) String studentId) {
        if (!StringUtils.hasText(studentId)) {
            return Result.error("未传入学员ID");
        }

        StudentProfile p = profileStore.get(studentId);
        if (p == null) {
            User user = userMapper.selectById(studentId);
            p = new StudentProfile();
            p.setStudentId(studentId);
            p.setStudentName(user != null ? (StringUtils.hasText(user.getStudentName()) ? user.getStudentName() : user.getRealName()) : "学员档案");
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
    public Result<String> saveProfile(@RequestBody StudentProfile dto) {
        if (dto == null || !StringUtils.hasText(dto.getStudentId())) {
            return Result.error("学员ID不可为空");
        }
        if (!StringUtils.hasText(dto.getProfileId())) {
            dto.setProfileId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }
        dto.setUpdatedAt(new Date());
        profileStore.put(dto.getStudentId(), dto);
        return Result.success("学员档案维护成功");
    }

    @GetMapping("/scores")
    public Result<List<StudentProfile>> getScores() {
        return Result.success(new ArrayList<>(profileStore.values()));
    }
}
