package com.wudao.interceptor;

import com.wudao.common.SnowflakeIdWorker;
import com.wudao.entity.OperationLog;
import com.wudao.mapper.OperationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(OperationLogInterceptor.class);

    @Autowired
    private OperationLogMapper operationLogMapper;

    private static final Map<String, String> API_NAME_MAP = new HashMap<>();

    static {
        API_NAME_MAP.put("/api/auth/wx-login", "微信小程序授权登录");
        API_NAME_MAP.put("/api/auth/apply-login", "提交账户开通申请");
        API_NAME_MAP.put("/api/schedule/list", "查看教务课程排期表");
        API_NAME_MAP.put("/api/schedule/create", "发布教务课程排期");
        API_NAME_MAP.put("/api/leave-makeup/apply-leave", "提交学员请假核销");
        API_NAME_MAP.put("/api/leave-makeup/apply-makeup", "提交学员补课核销");
        API_NAME_MAP.put("/api/volunteer/tasks", "查看大型活动招募任务");
        API_NAME_MAP.put("/api/volunteer/createTask", "发布大型活动招募任务");
        API_NAME_MAP.put("/api/volunteer/assignTask", "指派大型活动招募岗位");
        API_NAME_MAP.put("/api/volunteer/enroll", "报名大型活动招募任务");
        API_NAME_MAP.put("/api/volunteer/duty/list", "查看7天家委轮值看护排班");
        API_NAME_MAP.put("/api/volunteer/duty/claim", "报名7天家委轮值看护");
        API_NAME_MAP.put("/api/item-demand/list", "查看集中采购与选购计划");
        API_NAME_MAP.put("/api/item-demand/add", "发布物品选购计划");
        API_NAME_MAP.put("/api/item-demand/update", "更新物品到货状态");
        API_NAME_MAP.put("/api/item-demand/enroll", "登记选购物品需求数量");
        API_NAME_MAP.put("/api/item-demand/export", "导出采购选购全量明细");
        API_NAME_MAP.put("/api/teacher/list", "查看名师团队列表");
        API_NAME_MAP.put("/api/teacher/create", "配置保存名师档案");
        API_NAME_MAP.put("/api/banner/list", "查看活动展播Banner");
        API_NAME_MAP.put("/api/banner/publish", "发布大型活动展播");
        API_NAME_MAP.put("/api/thought/list", "查看随感与说说心里话");
        API_NAME_MAP.put("/api/thought/publish", "发布随感与说说心里话");
        API_NAME_MAP.put("/api/qa/list", "查看私信与精选问答");
        API_NAME_MAP.put("/api/qa/submit-thought", "提交私信与问答");
        API_NAME_MAP.put("/api/user/pending-approvals", "查看待审批用户列表");
        API_NAME_MAP.put("/api/user/approve", "审批开通系统用户账号");
        API_NAME_MAP.put("/api/student-profile/scores", "查看学员综合档案与成绩");
        API_NAME_MAP.put("/api/student-profile/my", "查看个人学员综合档案");
        API_NAME_MAP.put("/api/student-profile/save", "保存学员综合档案成绩");
        API_NAME_MAP.put("/api/work-group/list", "查看家委工作小组");
        API_NAME_MAP.put("/api/work-group/save", "保存家委工作小组配置");
        API_NAME_MAP.put("/api/work-group/delete", "删除家委工作小组");
        API_NAME_MAP.put("/api/notice/list", "查看官方通知公告");
        API_NAME_MAP.put("/api/notice/create", "发布官方通知公告");
        API_NAME_MAP.put("/api/operation-log/list", "查看系统操作与浏览日志");
        API_NAME_MAP.put("/api/upload/image", "上传图片至阿里云OSS");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            String uri = request.getRequestURI();
            if (uri == null || uri.startsWith("/image") || uri.startsWith("/static") || uri.contains("favicon") || uri.contains("/error")) {
                return;
            }

            String method = request.getMethod();
            String opType = determineOpType(method, uri);
            String apiName = determineApiName(uri, method);

            String userId = request.getHeader("X-User-Id");
            if (userId == null || userId.trim().isEmpty()) {
                userId = request.getParameter("userId");
            }
            if (userId == null || userId.trim().isEmpty()) {
                userId = "1787400000000000001";
            }

            String userName = request.getHeader("X-User-Name");
            if (userName == null || userName.trim().isEmpty()) {
                userName = request.getParameter("userName");
            }
            if (userName == null || userName.trim().isEmpty()) {
                userName = request.getParameter("parentName");
            }
            if (userName == null || userName.trim().isEmpty()) {
                userName = request.getParameter("realName");
            }
            if (userName == null || userName.trim().isEmpty()) {
                userName = "1787400000000000001".equals(userId) ? "系统管理员" : "热心用户";
            }

            OperationLog logEntity = new OperationLog();
            logEntity.setLogId(SnowflakeIdWorker.generateIdStr());
            logEntity.setUserId(userId);
            logEntity.setUserName(userName);
            logEntity.setApiPath(uri);
            logEntity.setApiName(apiName);
            logEntity.setOpType(opType);
            logEntity.setOpTime(new Date());

            operationLogMapper.insertLog(logEntity);
            log.info("[OperationLog] Logged request: user={}({}), type={}, api={}({})", userName, userId, opType, apiName, uri);
        } catch (Exception e) {
            log.error("[OperationLog] Error recording operation log", e);
        }
    }

    private String determineOpType(String method, String uri) {
        if (uri.contains("/login")) {
            return "LOGIN";
        }
        if ("GET".equalsIgnoreCase(method)) {
            return "VIEW";
        } else if ("DELETE".equalsIgnoreCase(method) || uri.contains("/delete")) {
            return "DELETE";
        } else if (uri.contains("/update") || uri.contains("/approve") || uri.contains("/assign")) {
            return "UPDATE";
        } else {
            return "CREATE";
        }
    }

    private String determineApiName(String uri, String method) {
        if (API_NAME_MAP.containsKey(uri)) {
            return API_NAME_MAP.get(uri);
        }
        for (Map.Entry<String, String> entry : API_NAME_MAP.entrySet()) {
            if (uri.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        if ("GET".equalsIgnoreCase(method)) {
            return "查看系统记录 (" + uri + ")";
        }
        return "操作系统接口 (" + uri + ")";
    }
}
