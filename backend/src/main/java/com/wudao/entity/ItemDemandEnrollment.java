package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class ItemDemandEnrollment implements Serializable {
    private String enrollmentId;
    private String itemId;
    private String parentName;
    private Integer quantity;
    private Date createdAt;

    // 前端兼容别名
    public String getName() { return parentName; }
    public Integer getCount() { return quantity; }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
