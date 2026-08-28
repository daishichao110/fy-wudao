package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class VolunteerTask implements Serializable {
    private Long taskId;
    private String activityName;
    private String groupType;
    private String taskName;
    private String taskDate;
    private String serviceTime;
    private String location;
    private Integer quotaCount;
    private Integer enrolledCount;
    private String status;
    private String description;
    private String danceClassName;
    private Date createdAt;

    public String getDanceClassName() { return danceClassName; }
    public void setDanceClassName(String danceClassName) { this.danceClassName = danceClassName; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getTaskDate() { return taskDate; }
    public void setTaskDate(String taskDate) { this.taskDate = taskDate; }

    public String getServiceTime() { return serviceTime; }
    public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getQuotaCount() { return quotaCount; }
    public void setQuotaCount(Integer quotaCount) { this.quotaCount = quotaCount; }

    public Integer getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(Integer enrolledCount) { this.enrolledCount = enrolledCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
