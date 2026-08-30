package com.wudao.mapper;

import com.wudao.entity.StudentProfile;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StudentProfileMapper {
    StudentProfile selectByStudentId(@Param("studentId") String studentId);
    StudentProfile selectByStudentName(@Param("studentName") String studentName);
    List<StudentProfile> selectAllProfiles(@Param("gradeLevel") String gradeLevel);
    int insertProfile(StudentProfile profile);
    int updateProfile(StudentProfile profile);
}
