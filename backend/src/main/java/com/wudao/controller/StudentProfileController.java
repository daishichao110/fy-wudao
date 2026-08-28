package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.StudentProfile;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/student-profile")
public class StudentProfileController {

    private final ConcurrentHashMap<Long, StudentProfile> profileStore = new ConcurrentHashMap<>();

    public StudentProfileController() {
        // 初始演示数据 (学员 李小桐)
        StudentProfile p = new StudentProfile();
        p.setProfileId(1L);
        p.setStudentId(6L);
        p.setStudentName("李小桐(新学员)");
        p.setGradeLevel("小学三年级");
        p.setChineseScore(95.5);
        p.setMathScore(98.0);
        p.setEnglishScore(94.0);
        p.setHeightCm(138.5);
        p.setWeightKg(31.2);
        p.setBustCm(68.0);
        p.setWaistCm(56.0);
        p.setHipCm(72.0);
        p.setShoeSize(34.0);
        p.setParentName("李妈妈");
        p.setParentPhone("13900000006");
        p.setUpdatedAt(new Date());
        profileStore.put(6L, p);
    }

    @GetMapping("/my")
    public Result<StudentProfile> getMyProfile(@RequestParam(required = false, defaultValue = "6") Long studentId) {
        StudentProfile p = profileStore.get(studentId);
        if (p == null) {
            p = new StudentProfile();
            p.setStudentId(studentId);
            p.setStudentName("未命名学员");
            p.setGradeLevel("小学三年级");
            p.setChineseScore(90.0);
            p.setMathScore(95.0);
            p.setEnglishScore(92.0);
            p.setHeightCm(138.0);
            p.setWeightKg(30.0);
            p.setParentName("家长");
            p.setParentPhone("13900000000");
            p.setUpdatedAt(new Date());
            profileStore.put(studentId, p);
        }
        return Result.success(p);
    }

    @PostMapping("/save")
    public Result<String> saveProfile(@RequestBody StudentProfile dto) {
        if (dto.getStudentId() == null) {
            dto.setStudentId(6L);
        }
        dto.setProfileId(System.currentTimeMillis());
        dto.setUpdatedAt(new Date());
        profileStore.put(dto.getStudentId(), dto);
        return Result.success("学员档案维护成功");
    }
}
