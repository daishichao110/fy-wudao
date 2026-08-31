package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private String userId;
    private String username;
    private String openId;
    private String realName;
    private String studentName;
    private String relationship;
    private String phone;
    private String avatarUrl;
    private String roleType;
    private String danceClassName;
    private Integer enrollmentYear;
    private Integer remainingHours;
    private Integer volunteerPoints;
    private Integer status;
    private Date createdAt;

    public Integer getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(Integer enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getDanceClassName() { return danceClassName; }
    public void setDanceClassName(String danceClassName) { this.danceClassName = danceClassName; }

    public Integer getRemainingHours() { return remainingHours; }
    public void setRemainingHours(Integer remainingHours) { this.remainingHours = remainingHours; }

    public Integer getVolunteerPoints() { return volunteerPoints; }
    public void setVolunteerPoints(Integer volunteerPoints) { this.volunteerPoints = volunteerPoints; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
