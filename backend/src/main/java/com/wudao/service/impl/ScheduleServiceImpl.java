package com.wudao.service.impl;

import com.wudao.entity.Schedule;
import com.wudao.entity.LeaveMakeUp;
import com.wudao.entity.User;
import com.wudao.mapper.ScheduleMapper;
import com.wudao.mapper.LeaveMakeUpMapper;
import com.wudao.mapper.UserMapper;
import com.wudao.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private LeaveMakeUpMapper leaveMakeUpMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Schedule> getAllSchedules(String danceClassName) {
        log.info("[ScheduleService] Executing getAllSchedules() for class: {}", danceClassName);
        List<Schedule> list = scheduleMapper.selectAll(danceClassName);
        log.info("[ScheduleService] Successfully retrieved {} future schedule records", list != null ? list.size() : 0);
        return list;
    }

    @Override
    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        log.info("[ScheduleService] Executing createSchedule() for class: {}, course: {}", schedule != null ? schedule.getDanceClassName() : "NULL", schedule != null ? schedule.getCourseName() : "NULL");

        // 1. 参数非空校验与自动兜底
        if (schedule == null) {
            log.error("[ScheduleService] Schedule object is null!");
            throw new IllegalArgumentException("排课数据不可为空");
        }
        if (!StringUtils.hasText(schedule.getCourseName())) {
            throw new IllegalArgumentException("课程名称不可为空");
        }
        schedule.setDanceClassName(com.wudao.common.DanceClassEnum.getCodeByName(schedule.getDanceClassName()));
        if (!StringUtils.hasText(schedule.getDanceType())) {
            schedule.setDanceType("芭蕾舞");
        }
        if (!StringUtils.hasText(schedule.getClassroomName())) {
            schedule.setClassroomName("1号芭蕾专业排练厅");
        }
        if (!StringUtils.hasText(schedule.getClassDate())) {
            schedule.setClassDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
        if (!StringUtils.hasText(schedule.getStartTime())) {
            schedule.setStartTime("14:00");
        }
        if (!StringUtils.hasText(schedule.getEndTime())) {
            schedule.setEndTime("16:00");
        }

        // 2. 教师身份与自动兼容保护 (支持教师及管理员发布排课)
        String targetTeacherId = schedule.getTeacherId();
        if (!StringUtils.hasText(targetTeacherId)) {
            targetTeacherId = "2"; // 默认挂载在林依依老师名下
        }

        User teacher = userMapper.selectById(targetTeacherId);
        if (teacher != null && StringUtils.hasText(teacher.getRealName())) {
            schedule.setTeacherId(targetTeacherId);
            schedule.setTeacherName(teacher.getRealName());
        } else {
            // 若该 ID 对应用户未查询到，自动降级挂载至 2号教师 (林依依老师)
            schedule.setTeacherId("2");
            schedule.setTeacherName(StringUtils.hasText(schedule.getTeacherName()) ? schedule.getTeacherName() : "林依依老师");
        }

        // 3. 着装规范默认保护 (包含 skirtReq 裙子要求)
        if (!StringUtils.hasText(schedule.getTopsReq())) schedule.setTopsReq("标准专业连功服");
        if (!StringUtils.hasText(schedule.getBottomsReq())) schedule.setBottomsReq("舞蹈专用大袜/练功裤");
        if (!StringUtils.hasText(schedule.getSkirtReq())) schedule.setSkirtReq("粉色雪纺一片绑带短裙");
        if (!StringUtils.hasText(schedule.getShoesReq())) schedule.setShoesReq("双皮头软底舞蹈鞋");
        if (!StringUtils.hasText(schedule.getHairReq())) schedule.setHairReq("高盘头丸子头(配发网)");
        if (!StringUtils.hasText(schedule.getPropsReq())) schedule.setPropsReq("把杆砖与弹力带");

        if (schedule.getCapacity() == null || schedule.getCapacity() <= 0) {
            schedule.setCapacity(15);
        }
        schedule.setBookedCount(0);

        if (!StringUtils.hasText(schedule.getScheduleId())) {
            schedule.setScheduleId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }

        scheduleMapper.insert(schedule);
        log.info("[ScheduleService] Schedule created successfully, assigned ScheduleId: {}", schedule.getScheduleId());
        return schedule;
    }

    @Override
    @Transactional
    public LeaveMakeUp applyLeave(LeaveMakeUp leaveRecord) {
        log.info("[ScheduleService] Executing applyLeave()...");

        // 1. 参数校验
        if (leaveRecord == null) {
            throw new IllegalArgumentException("请假记录参数不可为空");
        }
        if (!StringUtils.hasText(leaveRecord.getStudentId())) {
            throw new IllegalArgumentException("学员ID不可为空");
        }
        if (!StringUtils.hasText(leaveRecord.getScheduleId())) {
            throw new IllegalArgumentException("排课ID不可为空");
        }

        // 2. 学员与课程存在性校验
        User student = userMapper.selectById(leaveRecord.getStudentId());
        if (student == null) {
            log.error("[ScheduleService] Leave failed: Student ID {} not found", leaveRecord.getStudentId());
            throw new IllegalArgumentException("申请学员不存在");
        }
        Schedule schedule = scheduleMapper.selectById(leaveRecord.getScheduleId());
        if (schedule == null) {
            log.error("[ScheduleService] Leave failed: Schedule ID {} not found", leaveRecord.getScheduleId());
            throw new IllegalArgumentException("申请请假的课程不存在");
        }

        // 3. 防重复请假校验
        List<LeaveMakeUp> history = leaveMakeUpMapper.selectByStudentId(leaveRecord.getStudentId());
        if (history != null) {
            for (LeaveMakeUp item : history) {
                if ("LEAVE".equals(item.getRecordType()) &&
                    "EFFECTIVE".equals(item.getStatus()) &&
                    schedule.getScheduleId().equals(item.getScheduleId())) {
                    log.warn("[ScheduleService] Duplicate leave request detected for student {} on schedule {}", student.getUserId(), schedule.getScheduleId());
                    throw new IllegalStateException("您已针对《" + schedule.getCourseName() + "》提交过请假，请勿重复申请");
                }
            }
        }

        leaveRecord.setStudentName(student.getRealName());
        leaveRecord.setCourseName(schedule.getCourseName());
        leaveRecord.setRecordType("LEAVE");
        leaveRecord.setStatus("EFFECTIVE");

        if (!StringUtils.hasText(leaveRecord.getRecordId())) {
            leaveRecord.setRecordId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }

        leaveMakeUpMapper.insert(leaveRecord);
        scheduleMapper.decrementBookedCount(schedule.getScheduleId());

        log.info("[ScheduleService] Zero-Approval Leave processed. RecordId: {}, Seat released on scheduleId: {}", leaveRecord.getRecordId(), schedule.getScheduleId());
        return leaveRecord;
    }

    @Override
    @Transactional
    public LeaveMakeUp applyMakeup(LeaveMakeUp makeupRecord) {
        log.info("[ScheduleService] Executing applyMakeup()...");

        // 1. 参数校验
        if (makeupRecord == null) {
            throw new IllegalArgumentException("补课预约参数不可为空");
        }
        if (!StringUtils.hasText(makeupRecord.getStudentId())) {
            throw new IllegalArgumentException("补课学员ID不可为空");
        }
        if (!StringUtils.hasText(makeupRecord.getScheduleId())) {
            throw new IllegalArgumentException("补课排课ID不可为空");
        }

        // 2. 学员与课程存在性校验
        User student = userMapper.selectById(makeupRecord.getStudentId());
        if (student == null) {
            throw new IllegalArgumentException("预约学员不存在");
        }
        Schedule schedule = scheduleMapper.selectById(makeupRecord.getScheduleId());
        if (schedule == null) {
            throw new IllegalArgumentException("预约补课的课程不存在");
        }

        // 3. 容量与学位校验
        if (schedule.getBookedCount() != null && schedule.getCapacity() != null) {
            if (schedule.getBookedCount() >= schedule.getCapacity()) {
                log.warn("[ScheduleService] Makeup failed: Schedule ID {} is fully booked ({}/{})", schedule.getScheduleId(), schedule.getBookedCount(), schedule.getCapacity());
                throw new IllegalStateException("该补课场次学位已满(" + schedule.getBookedCount() + "/" + schedule.getCapacity() + "人)，请选择其他时间段");
            }
        }

        // 4. 防重复预约校验
        List<LeaveMakeUp> history = leaveMakeUpMapper.selectByStudentId(makeupRecord.getStudentId());
        if (history != null) {
            for (LeaveMakeUp item : history) {
                if ("MAKE_UP".equals(item.getRecordType()) &&
                    "EFFECTIVE".equals(item.getStatus()) &&
                    schedule.getScheduleId().equals(item.getScheduleId())) {
                    throw new IllegalStateException("您已预约过《" + schedule.getCourseName() + "》的补课，无需重复预约");
                }
            }
        }

        makeupRecord.setStudentName(student.getRealName());
        makeupRecord.setCourseName(schedule.getCourseName());
        makeupRecord.setRecordType("MAKE_UP");
        makeupRecord.setStatus("EFFECTIVE");

        if (!StringUtils.hasText(makeupRecord.getRecordId())) {
            makeupRecord.setRecordId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }

        leaveMakeUpMapper.insert(makeupRecord);
        scheduleMapper.incrementBookedCount(schedule.getScheduleId());

        log.info("[ScheduleService] Zero-Approval Makeup Appointment processed. RecordId: {}, Booked count updated.", makeupRecord.getRecordId());
        return makeupRecord;
    }
}
