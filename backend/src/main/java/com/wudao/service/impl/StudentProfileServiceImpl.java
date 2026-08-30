package com.wudao.service.impl;

import com.wudao.common.SnowflakeIdWorker;
import com.wudao.entity.StudentProfile;
import com.wudao.mapper.StudentProfileMapper;
import com.wudao.service.StudentProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    private static final Logger log = LoggerFactory.getLogger(StudentProfileServiceImpl.class);

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Override
    public StudentProfile getProfileByStudentId(String studentId) {
        if (!StringUtils.hasText(studentId)) return null;
        return studentProfileMapper.selectByStudentId(studentId);
    }

    @Override
    public List<StudentProfile> getAllProfiles(String gradeLevel) {
        return studentProfileMapper.selectAllProfiles(gradeLevel);
    }

    @Override
    @Transactional
    public StudentProfile saveOrUpdateProfile(StudentProfile profile) {
        if (profile == null) throw new IllegalArgumentException("档案信息不可为空");
        if (!StringUtils.hasText(profile.getStudentName())) throw new IllegalArgumentException("学员姓名不可为空");

        log.info("[StudentProfileService] Saving/Updating profile for student: {}, studentId: {}", profile.getStudentName(), profile.getStudentId());

        // 校验该学员是否存在已有的唯一档案 (按 studentId 或 studentName 查重)
        StudentProfile existing = null;
        if (StringUtils.hasText(profile.getStudentId())) {
            existing = studentProfileMapper.selectByStudentId(profile.getStudentId());
        }
        if (existing == null && StringUtils.hasText(profile.getStudentName())) {
            existing = studentProfileMapper.selectByStudentName(profile.getStudentName());
        }

        if (existing != null) {
            // 已存在该学员记录：一律执行 UPDATE 更新，绝不产生重复行数据！
            profile.setProfileId(existing.getProfileId());
            if (!StringUtils.hasText(profile.getStudentId())) {
                profile.setStudentId(existing.getStudentId());
            }
            studentProfileMapper.updateProfile(profile);
            log.info("[StudentProfileService] Updated existing profile ID: {} for student: {}", existing.getProfileId(), profile.getStudentName());
        } else {
            // 尚无记录：创建新档案
            if (!StringUtils.hasText(profile.getProfileId())) {
                profile.setProfileId(SnowflakeIdWorker.generateIdStr());
            }
            if (!StringUtils.hasText(profile.getStudentId())) {
                profile.setStudentId(SnowflakeIdWorker.generateIdStr());
            }
            studentProfileMapper.insertProfile(profile);
            log.info("[StudentProfileService] Inserted new profile ID: {} for student: {}", profile.getProfileId(), profile.getStudentName());
        }

        profile.setUpdatedAt(new Date());
        return profile;
    }
}
