package com.wudao.entity;

import java.io.Serializable;
import java.util.Date;

public class OperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String logId;
    private String userId;
    private String userName;
    private String apiPath;
    private String apiName;
    private String opType;
    private Date opTime;

    public OperationLog() {
    }

    public OperationLog(String logId, String userId, String userName, String apiPath, String apiName, String opType, Date opTime) {
        this.logId = logId;
        this.userId = userId;
        this.userName = userName;
        this.apiPath = apiPath;
        this.apiName = apiName;
        this.opType = opType;
        this.opTime = opTime;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }
}
