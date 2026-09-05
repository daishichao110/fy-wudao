package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.OperationLog;
import com.wudao.mapper.OperationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operation-log")
public class OperationLogController {

    private static final Logger log = LoggerFactory.getLogger(OperationLogController.class);

    @Autowired
    private OperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<List<OperationLog>> getLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String opType,
            @RequestParam(required = false, defaultValue = "100") Integer limit) {
        log.info("[REST API GET /api/operation-log/list] Querying operation logs: userId={}, opType={}, limit={}", userId, opType, limit);
        List<OperationLog> list = operationLogMapper.selectLogs(userId, opType, limit);
        return Result.success("获取日志成功", list);
    }
}
