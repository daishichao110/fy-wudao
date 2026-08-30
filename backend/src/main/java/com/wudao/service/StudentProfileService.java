package com.wudao.service;

import com.wudao.entity.StudentProfile;
import java.util.List;

public interface StudentProfileService {
    StudentProfile getProfileByStudentId(String studentId);
    List<StudentProfile> getAllProfiles(String gradeLevel);
    StudentProfile saveOrUpdateProfile(StudentProfile profile);
}
