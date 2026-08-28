package com.wudao.mapper;

import com.wudao.entity.DutySchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DutyScheduleMapper {
    List<DutySchedule> selectAllDuties(@Param("danceClassName") String danceClassName);
    DutySchedule selectByDate(@Param("dutyDate") String dutyDate, @Param("danceClassName") String danceClassName);
    int insertOrUpdateDuty(DutySchedule duty);
}
