package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class WorkGroup implements Serializable {
    private String groupId;
    private String groupName;
    private String icon;
    private String leaderUserId;
    private String memberUserIds;
    private String leaderName;
    private String memberNames;
    private String dutyDesc;
    private String danceClassName;
    private Integer sortOrder;
    private Date createdAt;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDanceClassName() { return danceClassName; }
    public void setDanceClassName(String danceClassName) { this.danceClassName = danceClassName; }

    public String getLeaderUserId() { return leaderUserId; }
    public void setLeaderUserId(String leaderUserId) { this.leaderUserId = leaderUserId; }

    public String getMemberUserIds() { return memberUserIds; }
    public void setMemberUserIds(String memberUserIds) { this.memberUserIds = memberUserIds; }

    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public String getMemberNames() { return memberNames; }
    public void setMemberNames(String memberNames) { this.memberNames = memberNames; }

    public String getDutyDesc() { return dutyDesc; }
    public void setDutyDesc(String dutyDesc) { this.dutyDesc = dutyDesc; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
