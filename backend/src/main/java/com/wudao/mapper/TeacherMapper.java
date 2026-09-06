package com.wudao.mapper;

import com.wudao.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TeacherMapper {
 List<Teacher> selectAllTeachers();
 Teacher selectTeacherByName(@Param("name") String name);
 int insertTeacher(Teacher teacher);
 int updateTeacher(Teacher teacher);
}
