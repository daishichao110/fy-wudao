package com.wudao.entity;

import java.io.Serializable;

public class DutySchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dutyId;
    private String dutyDate;
    private String assigneeName;
    private String userId;
    private String danceClassName;
    private String status;
    private String createdAt;

    public String getDutyId() { return dutyId; }
    public void setDutyId(String dutyId) { this.dutyId = dutyId; }

    public String getDutyDate() { return dutyDate; }
    public void setDutyDate(String dutyDate) { this.dutyDate = dutyDate; }

    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDanceClassName() { return danceClassName; }
    public void setDanceClassName(String danceClassName) { this.danceClassName = danceClassName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
