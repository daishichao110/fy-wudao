package com.wudao.mapper;

import com.wudao.entity.VolunteerTask;
import com.wudao.entity.VolunteerEnrollment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface VolunteerMapper {
    List<VolunteerTask> selectAllTasks(@Param("danceClassName") String danceClassName);
    VolunteerTask selectTaskById(@Param("taskId") Long taskId);
    int insertTask(VolunteerTask task);
    int incrementTaskEnrolledCount(@Param("taskId") Long taskId);
    int updateTaskStatusFull(@Param("taskId") Long taskId);
    int insertEnrollment(VolunteerEnrollment enrollment);
    VolunteerEnrollment selectEnrollment(@Param("taskId") Long taskId, @Param("userId") Long userId);
}
