package com.wudao.controller;

import com.wudao.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api", "/"})
public class MockController {

    private static final Logger log = LoggerFactory.getLogger(MockController.class);

    /**
     * 🟢 部署检验 Mock 接口 (用于检验服务器部署与网络连通性是否成功)
     * 支持 GET / POST 请求路径:
     * - http://localhost:8080/api/mock
     * - http://localhost:8080/mock
     * - http://localhost:8080/api/health
     * - http://localhost:8080/health
     */
    @RequestMapping(value = {"/mock", "/health", "/api/mock", "/api/health"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Map<String, Object>> mockDeploymentCheck() {
        String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        log.info("🟢 [MOCK HEALTH CHECK] 接收到部署检验请求！wudao-backend 后端服务运行正常，当前时间: {}", nowStr);

        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("appName", "劲松金帆舞团教务数字化管理系统");
        data.put("timestamp", nowStr);
        data.put("message", "🟢 恭喜！后端 Spring Boot 服务已成功部署并流畅运行！");

        return Result.success("服务连通性校验成功！", data);
    }
}
