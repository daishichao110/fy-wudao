package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class Mentorship implements Serializable {
    private String pairId;
    private String seniorStudentId;
    private String seniorStudentName;
    private String juniorStudentId;
    private String juniorStudentName;
    private String termName;
    private Integer starPoints;
    private Integer checkinCount;
    private Date createdAt;

    public String getPairId() { return pairId; }
    public void setPairId(String pairId) { this.pairId = pairId; }

    public String getSeniorStudentId() { return seniorStudentId; }
    public void setSeniorStudentId(String seniorStudentId) { this.seniorStudentId = seniorStudentId; }

    public String getSeniorStudentName() { return seniorStudentName; }
    public void setSeniorStudentName(String seniorStudentName) { this.seniorStudentName = seniorStudentName; }

    public String getJuniorStudentId() { return juniorStudentId; }
    public void setJuniorStudentId(String juniorStudentId) { this.juniorStudentId = juniorStudentId; }

    public String getJuniorStudentName() { return juniorStudentName; }
    public void setJuniorStudentName(String juniorStudentName) { this.juniorStudentName = juniorStudentName; }

    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }

    public Integer getStarPoints() { return starPoints; }
    public void setStarPoints(Integer starPoints) { this.starPoints = starPoints; }

    public Integer getCheckinCount() { return checkinCount; }
    public void setCheckinCount(Integer checkinCount) { this.checkinCount = checkinCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
