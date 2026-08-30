package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class Schedule implements Serializable {
    private String scheduleId;
    private String courseName;
    private String danceType;
    private String teacherId;
    private String teacherName;
    private String classroomName;
    private String classDate;
    private String startTime;
    private String endTime;
    private String topsReq;
    private String bottomsReq;
    private String skirtReq;
    private String shoesReq;
    private String hairReq;
    private String propsReq;
    private String otherReq;
    private String remark;
    private String participantNames;
    private Integer capacity;
    private Integer bookedCount;
    private String danceClassName;
    private Date createdAt;

    public String getOtherReq() { return otherReq; }
    public void setOtherReq(String otherReq) { this.otherReq = otherReq; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getParticipantNames() { return participantNames; }
    public void setParticipantNames(String participantNames) { this.participantNames = participantNames; }

    public String getDanceClassName() { return danceClassName; }
    public void setDanceClassName(String danceClassName) { this.danceClassName = danceClassName; }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDanceType() { return danceType; }
    public void setDanceType(String danceType) { this.danceType = danceType; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }

    public String getClassDate() { return classDate; }
    public void setClassDate(String classDate) { this.classDate = classDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getTopsReq() { return topsReq; }
    public void setTopsReq(String topsReq) { this.topsReq = topsReq; }

    public String getBottomsReq() { return bottomsReq; }
    public void setBottomsReq(String bottomsReq) { this.bottomsReq = bottomsReq; }

    public String getSkirtReq() { return skirtReq; }
    public void setSkirtReq(String skirtReq) { this.skirtReq = skirtReq; }

    public String getShoesReq() { return shoesReq; }
    public void setShoesReq(String shoesReq) { this.shoesReq = shoesReq; }

    public String getHairReq() { return hairReq; }
    public void setHairReq(String hairReq) { this.hairReq = hairReq; }

    public String getPropsReq() { return propsReq; }
    public void setPropsReq(String propsReq) { this.propsReq = propsReq; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
