package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ItemDemand implements Serializable {
    private String itemId;
    private String itemName;
    private String danceClassName;
    private String deadline;
    private String expectedArrivalDate;
    private String arrivalStatus;
    private String sizeSummaryStr;
    private Integer signedCount;
    private List<ItemDemandEnrollment> enrollList;
    private Date createdAt;

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDanceClassName() { return danceClassName; }
    public void setDanceClassName(String danceClassName) { this.danceClassName = danceClassName; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getExpectedArrivalDate() { return expectedArrivalDate; }
    public void setExpectedArrivalDate(String expectedArrivalDate) { this.expectedArrivalDate = expectedArrivalDate; }

    public String getArrivalStatus() { return arrivalStatus; }
    public void setArrivalStatus(String arrivalStatus) { this.arrivalStatus = arrivalStatus; }

    public String getSizeSummaryStr() { return sizeSummaryStr; }
    public void setSizeSummaryStr(String sizeSummaryStr) { this.sizeSummaryStr = sizeSummaryStr; }

    public Integer getSignedCount() { return signedCount; }
    public void setSignedCount(Integer signedCount) { this.signedCount = signedCount; }

    public List<ItemDemandEnrollment> getEnrollList() { return enrollList; }
    public void setEnrollList(List<ItemDemandEnrollment> enrollList) { this.enrollList = enrollList; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
