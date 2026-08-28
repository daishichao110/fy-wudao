package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class Mentorship implements Serializable {
    private Long pairId;
    private Long seniorStudentId;
    private String seniorStudentName;
    private Long juniorStudentId;
    private String juniorStudentName;
    private String termName;
    private Integer starPoints;
    private Integer checkinCount;
    private Date createdAt;

    public Long getPairId() { return pairId; }
    public void setPairId(Long pairId) { this.pairId = pairId; }

    public Long getSeniorStudentId() { return seniorStudentId; }
    public void setSeniorStudentId(Long seniorStudentId) { this.seniorStudentId = seniorStudentId; }

    public String getSeniorStudentName() { return seniorStudentName; }
    public void setSeniorStudentName(String seniorStudentName) { this.seniorStudentName = seniorStudentName; }

    public Long getJuniorStudentId() { return juniorStudentId; }
    public void setJuniorStudentId(Long juniorStudentId) { this.juniorStudentId = juniorStudentId; }

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
