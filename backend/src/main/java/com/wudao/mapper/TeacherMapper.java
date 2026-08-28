package com.wudao.mapper;

import com.wudao.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TeacherMapper {
    List<Teacher> selectAllTeachers();
    int insertTeacher(Teacher teacher);
}
