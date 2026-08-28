package com.wudao.service;

import com.wudao.entity.Schedule;
import com.wudao.entity.LeaveMakeUp;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getAllSchedules(String danceClassName);
    Schedule createSchedule(Schedule schedule);
    LeaveMakeUp applyLeave(LeaveMakeUp leaveRecord);
    LeaveMakeUp applyMakeup(LeaveMakeUp makeupRecord);
}
