package com.wudao.mapper;

import com.wudao.entity.VolunteerTask;
import com.wudao.entity.VolunteerEnrollment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface VolunteerMapper {
    List<VolunteerTask> selectAllTasks(@Param("danceClassName") String danceClassName);
    VolunteerTask selectTaskById(@Param("taskId") String taskId);
    int insertTask(VolunteerTask task);
    int incrementTaskEnrolledCount(@Param("taskId") String taskId);
    int updateTaskStatusFull(@Param("taskId") String taskId);
    int insertEnrollment(VolunteerEnrollment enrollment);
    VolunteerEnrollment selectEnrollment(@Param("taskId") String taskId, @Param("userId") String userId);
}
