package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.Schedule;
import com.wudao.entity.LeaveMakeUp;
import com.wudao.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/list")
    public Result<List<Schedule>> listSchedules(@RequestParam(required = false) String danceClassName) {
        log.info("[REST API GET /api/schedule/list] Querying schedules for class: {}", danceClassName);
        List<Schedule> schedules = scheduleService.getAllSchedules(danceClassName);
        log.info("[REST API GET /api/schedule/list] Returned {} schedules", schedules.size());
        return Result.success(schedules);
    }

    @PostMapping("/create")
    public Result<Schedule> createSchedule(@RequestBody Schedule schedule) {
        log.info("[REST API POST /api/schedule/create] Teacher publishing new schedule for course: {}", schedule.getCourseName());
        Schedule created = scheduleService.createSchedule(schedule);
        return Result.success("排课及着装规范发布成功", created);
    }

    @PostMapping("/leave")
    public Result<LeaveMakeUp> applyLeave(@RequestBody LeaveMakeUp leaveRecord) {
        log.info("[REST API POST /api/schedule/leave] Zero-Approval Instant Leave: studentId={}, scheduleId={}", leaveRecord.getStudentId(), leaveRecord.getScheduleId());
        LeaveMakeUp res = scheduleService.applyLeave(leaveRecord);
        return Result.success("一键请假成功，额度已实时退回", res);
    }

    @PostMapping("/makeup")
    public Result<LeaveMakeUp> applyMakeup(@RequestBody LeaveMakeUp makeupRecord) {
        log.info("[REST API POST /api/schedule/makeup] Zero-Approval Makeup Appointment: studentId={}, scheduleId={}", makeupRecord.getStudentId(), makeupRecord.getScheduleId());
        LeaveMakeUp res = scheduleService.applyMakeup(makeupRecord);
        return Result.success("预约补课成功，已实时核销补课额度", res);
    }
}
