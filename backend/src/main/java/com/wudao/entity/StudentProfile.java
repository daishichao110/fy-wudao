package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class StudentProfile implements Serializable {
    private Long profileId;
    private Long studentId;
    private String studentName;
    private String gradeLevel;
    private Double chineseScore;
    private Double mathScore;
    private Double englishScore;
    private Double heightCm;
    private Double weightKg;
    private Double bustCm;
    private Double waistCm;
    private Double hipCm;
    private Double shoeSize;
    private String parentName;
    private String parentPhone;
    private Date updatedAt;

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }

    public Double getChineseScore() { return chineseScore; }
    public void setChineseScore(Double chineseScore) { this.chineseScore = chineseScore; }

    public Double getMathScore() { return mathScore; }
    public void setMathScore(Double mathScore) { this.mathScore = mathScore; }

    public Double getEnglishScore() { return englishScore; }
    public void setEnglishScore(Double englishScore) { this.englishScore = englishScore; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getBustCm() { return bustCm; }
    public void setBustCm(Double bustCm) { this.bustCm = bustCm; }

    public Double getWaistCm() { return waistCm; }
    public void setWaistCm(Double waistCm) { this.waistCm = waistCm; }

    public Double getHipCm() { return hipCm; }
    public void setHipCm(Double hipCm) { this.hipCm = hipCm; }

    public Double getShoeSize() { return shoeSize; }
    public void setShoeSize(Double shoeSize) { this.shoeSize = shoeSize; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
