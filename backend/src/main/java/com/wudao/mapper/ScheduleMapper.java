package com.wudao.mapper;

import com.wudao.entity.Schedule;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ScheduleMapper {
    List<Schedule> selectAll(@Param("danceClassName") String danceClassName);
    Schedule selectById(@Param("scheduleId") String scheduleId);
    int insert(Schedule schedule);
    int incrementBookedCount(@Param("scheduleId") String scheduleId);
    int decrementBookedCount(@Param("scheduleId") String scheduleId);
}
