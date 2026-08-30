package com.wudao.controller;

import com.wudao.common.Result;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api", "/"})
public class HealthCheckController {

    @RequestMapping(value = {"/health", "/api/health"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Map<String, Object>> healthCheck() {
        String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("appName", "wudao-backend");
        data.put("timestamp", nowStr);
        return Result.success("服务运行正常", data);
    }
}
