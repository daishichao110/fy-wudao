package com.wudao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BodyMetric implements Serializable {
    private String metricId;
    private String studentId;
    private String studentName;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bustCm;
    private BigDecimal waistCm;
    private BigDecimal hipCm;
    private BigDecimal torsoLengthCm;
    private BigDecimal shoeSize;
    private String measuredDate;
    private Date createdAt;

    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public BigDecimal getBustCm() { return bustCm; }
    public void setBustCm(BigDecimal bustCm) { this.bustCm = bustCm; }

    public BigDecimal getWaistCm() { return waistCm; }
    public void setWaistCm(BigDecimal waistCm) { this.waistCm = waistCm; }

    public BigDecimal getHipCm() { return hipCm; }
    public void setHipCm(BigDecimal hipCm) { this.hipCm = hipCm; }

    public BigDecimal getTorsoLengthCm() { return torsoLengthCm; }
    public void setTorsoLengthCm(BigDecimal torsoLengthCm) { this.torsoLengthCm = torsoLengthCm; }

    public BigDecimal getShoeSize() { return shoeSize; }
    public void setShoeSize(BigDecimal shoeSize) { this.shoeSize = shoeSize; }

    public String getMeasuredDate() { return measuredDate; }
    public void setMeasuredDate(String measuredDate) { this.measuredDate = measuredDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
