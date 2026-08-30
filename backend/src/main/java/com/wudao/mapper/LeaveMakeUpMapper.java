package com.wudao.mapper;

import com.wudao.entity.LeaveMakeUp;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface LeaveMakeUpMapper {
    List<LeaveMakeUp> selectByStudentId(@Param("studentId") String studentId);
    int insert(LeaveMakeUp record);
}
