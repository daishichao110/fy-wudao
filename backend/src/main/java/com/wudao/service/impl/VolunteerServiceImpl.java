package com.wudao.service.impl;

import com.wudao.entity.VolunteerTask;
import com.wudao.entity.VolunteerEnrollment;
import com.wudao.entity.User;
import com.wudao.mapper.VolunteerMapper;
import com.wudao.mapper.UserMapper;
import com.wudao.service.VolunteerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private static final Logger log = LoggerFactory.getLogger(VolunteerServiceImpl.class);

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<VolunteerTask> getAllTasks(String danceClassName) {
        log.info("[VolunteerService] Executing getAllTasks() for class: {}", danceClassName);
        List<VolunteerTask> tasks = volunteerMapper.selectAllTasks(danceClassName);
        log.info("[VolunteerService] Fetched {} volunteer task groups", tasks != null ? tasks.size() : 0);
        return tasks;
    }

    @Override
    @Transactional
    public VolunteerTask createTask(VolunteerTask task) {
        log.info("[VolunteerService] Creating new volunteer task: {}, class: {}", task.getTaskName(), task.getDanceClassName());
        if (task == null || task.getTaskName() == null || task.getTaskName().trim().isEmpty()) {
            throw new IllegalArgumentException("任务名称不可为空");
        }
        if (task.getQuotaCount() == null || task.getQuotaCount() <= 0) {
            task.setQuotaCount(4);
        }
        if (!org.springframework.util.StringUtils.hasText(task.getDanceClassName())) {
            task.setDanceClassName("二年级");
        }
        if (task.getTaskId() == null || task.getTaskId() <= 0) {
            task.setTaskId(com.wudao.common.SnowflakeIdWorker.generateId());
        }
        volunteerMapper.insertTask(task);
        return task;
    }

    @Override
    @Transactional
    public VolunteerEnrollment assignTask(VolunteerEnrollment enrollment) {
        log.info("[VolunteerService] Executing Task Assignment: taskId={}, userId={}", enrollment.getTaskId(), enrollment.getUserId());
        if (enrollment == null || enrollment.getTaskId() == null || enrollment.getUserId() == null) {
            throw new IllegalArgumentException("指派任务参数不可为空");
        }

        VolunteerTask task = volunteerMapper.selectTaskById(enrollment.getTaskId());
        if (task == null) {
            throw new IllegalArgumentException("指定任务不存在");
        }

        User user = userMapper.selectById(enrollment.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("指定指派的家长不存在");
        }

        String rel = (user.getRelationship() != null && !user.getRelationship().isEmpty()) ? user.getRelationship() : "家长";
        String parentDisplayName = (user.getStudentName() != null && !user.getStudentName().isEmpty()) ? (user.getStudentName() + "的" + rel) : user.getRealName();
        enrollment.setUserName(parentDisplayName);
        enrollment.setStatus("ASSIGNED");
        if (enrollment.getEnrollmentId() == null || enrollment.getEnrollmentId() <= 0) {
            enrollment.setEnrollmentId(com.wudao.common.SnowflakeIdWorker.generateId());
        }

        volunteerMapper.insertEnrollment(enrollment);
        volunteerMapper.incrementTaskEnrolledCount(enrollment.getTaskId());
        volunteerMapper.updateTaskStatusFull(enrollment.getTaskId());

        return enrollment;
    }

    @Override
    @Transactional
    public VolunteerEnrollment enrollTask(VolunteerEnrollment enrollment) {
        log.info("[VolunteerService] Executing Zero-Approval Volunteer Enrollment...");

        if (enrollment == null) {
            throw new IllegalArgumentException("认领申请参数不可为空");
        }
        if (enrollment.getTaskId() == null || enrollment.getTaskId() <= 0) {
            throw new IllegalArgumentException("志愿任务ID不合法");
        }
        if (enrollment.getUserId() == null || enrollment.getUserId() <= 0) {
            throw new IllegalArgumentException("家委/家长用户ID不合法");
        }

        User user = userMapper.selectById(enrollment.getUserId());
        if (user == null) {
            log.error("[VolunteerService] Enrollment failed: User ID {} not found", enrollment.getUserId());
            throw new IllegalArgumentException("申请认领的家委用户不存在(ID: " + enrollment.getUserId() + ")");
        }
        String userRel = (user.getRelationship() != null && !user.getRelationship().isEmpty()) ? user.getRelationship() : "家长";
        String userParentDisplayName = (user.getStudentName() != null && !user.getStudentName().isEmpty()) ? (user.getStudentName() + "的" + userRel) : user.getRealName();
        enrollment.setUserName(userParentDisplayName);

        VolunteerTask task = volunteerMapper.selectTaskById(enrollment.getTaskId());
        if (task == null) {
            log.error("[VolunteerService] Enrollment failed: Task ID {} not found", enrollment.getTaskId());
            throw new IllegalArgumentException("指定志愿任务不存在(ID: " + enrollment.getTaskId() + ")");
        }

        if ("FULL".equals(task.getStatus()) || (task.getEnrolledCount() != null && task.getQuotaCount() != null && task.getEnrolledCount() >= task.getQuotaCount())) {
            log.warn("[VolunteerService] Enrollment failed: Task ID {} quota is full ({}/{})", task.getTaskId(), task.getEnrolledCount(), task.getQuotaCount());
            throw new IllegalStateException("【名额已满】《" + task.getTaskName() + "》名额已招满(" + task.getEnrolledCount() + "/" + task.getQuotaCount() + "人)，请选择其他组别");
        }

        VolunteerEnrollment existing = volunteerMapper.selectEnrollment(enrollment.getTaskId(), enrollment.getUserId());
        if (existing != null) {
            log.warn("[VolunteerService] Duplicate volunteer enrollment: userId {} already enrolled task {}", user.getUserId(), task.getTaskId());
            throw new IllegalStateException("您已成功认领过《" + task.getTaskName() + "》，无需重复认领");
        }

        enrollment.setStatus("COMPLETED");
        if (enrollment.getEnrollmentId() == null || enrollment.getEnrollmentId() <= 0) {
            enrollment.setEnrollmentId(com.wudao.common.SnowflakeIdWorker.generateId());
        }
        volunteerMapper.insertEnrollment(enrollment);
        volunteerMapper.incrementTaskEnrolledCount(enrollment.getTaskId());
        volunteerMapper.updateTaskStatusFull(enrollment.getTaskId());

        userMapper.updatePoints(user.getUserId(), 15);
        log.info("[VolunteerService] Volunteer enrollment completed. Granted 15 volunteer points to user ID: {}", user.getUserId());

        return enrollment;
    }
}
