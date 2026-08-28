package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class QaMessage implements Serializable {
    private Long msgId;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private String questionContent;
    private String replyContent;
    private Integer isFeatured;
    private String featuredTitle;
    private Date createdAt;

    public Long getMsgId() { return msgId; }
    public void setMsgId(Long msgId) { this.msgId = msgId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getQuestionContent() { return questionContent; }
    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Integer getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Integer isFeatured) { this.isFeatured = isFeatured; }

    public String getFeaturedTitle() { return featuredTitle; }
    public void setFeaturedTitle(String featuredTitle) { this.featuredTitle = featuredTitle; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
