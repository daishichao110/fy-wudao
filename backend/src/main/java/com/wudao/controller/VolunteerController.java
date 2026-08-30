package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.VolunteerTask;
import com.wudao.entity.VolunteerEnrollment;
import com.wudao.service.VolunteerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {

    private static final Logger log = LoggerFactory.getLogger(VolunteerController.class);

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private com.wudao.mapper.DutyScheduleMapper dutyScheduleMapper;

    @GetMapping("/duty/list")
    public Result<List<com.wudao.entity.DutySchedule>> listDutySchedules(@RequestParam(required = false) String danceClassName) {
        log.info("[REST API GET /api/volunteer/duty/list] Querying 7-day duty schedule for class: {}", danceClassName);
        List<com.wudao.entity.DutySchedule> list = dutyScheduleMapper.selectAllDuties(danceClassName);
        return Result.success(list);
    }

    @PostMapping("/duty/claim")
    public Result<com.wudao.entity.DutySchedule> claimDuty(@RequestBody com.wudao.entity.DutySchedule duty) {
        log.info("[REST API POST /api/volunteer/duty/claim] Claiming duty: date={}, name={}, class={}", duty.getDutyDate(), duty.getAssigneeName(), duty.getDanceClassName());
        if (duty.getDutyDate() == null || duty.getDutyDate().trim().isEmpty()) {
            return Result.error("轮值日期不可为空");
        }
        if (duty.getAssigneeName() == null || duty.getAssigneeName().trim().isEmpty()) {
            return Result.error("认领称谓不可为空");
        }
        if (!org.springframework.util.StringUtils.hasText(duty.getDutyId())) {
            duty.setDutyId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }
        if (!org.springframework.util.StringUtils.hasText(duty.getDanceClassName())) {
            duty.setDanceClassName("二年级");
        }
        duty.setStatus("SCHEDULED");
        dutyScheduleMapper.insertOrUpdateDuty(duty);
        return Result.success("7天看护轮值排班成功保存至 MySQL 数据库！", duty);
    }

    @GetMapping("/tasks")
    public Result<List<VolunteerTask>> listTasks(@RequestParam(required = false) String danceClassName) {
        log.info("[REST API GET /api/volunteer/tasks] Querying volunteer task groups for class: {}", danceClassName);
        List<VolunteerTask> tasks = volunteerService.getAllTasks(danceClassName);
        return Result.success(tasks);
    }

    @PostMapping("/createTask")
    public Result<VolunteerTask> createTask(@RequestBody VolunteerTask task) {
        log.info("[REST API POST /api/volunteer/createTask] Creating new volunteer task: {}", task.getTaskName());
        try {
            VolunteerTask res = volunteerService.createTask(task);
            return Result.success("协同任务发布成功！", res);
        } catch (Exception e) {
            log.error("Create volunteer task error: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/assignTask")
    public Result<VolunteerEnrollment> assignTask(@RequestBody VolunteerEnrollment enrollment) {
        log.info("[REST API POST /api/volunteer/assignTask] Assigning volunteer task: taskId={}, userId={}", enrollment.getTaskId(), enrollment.getUserId());
        try {
            VolunteerEnrollment res = volunteerService.assignTask(enrollment);
            return Result.success("协同任务成功指派给家长！", res);
        } catch (Exception e) {
            log.error("Assign volunteer task error: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/enroll")
    public Result<VolunteerEnrollment> enrollTask(@RequestBody VolunteerEnrollment enrollment) {
        log.info("[REST API POST /api/volunteer/enroll] Zero-Approval Volunteer Enrollment: taskId={}, userId={}", enrollment.getTaskId(), enrollment.getUserId());
        try {
            VolunteerEnrollment res = volunteerService.enrollTask(enrollment);
            return Result.success("志愿任务认领成功！无需审核", res);
        } catch (Exception e) {
            log.error("Volunteer enrollment error: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
