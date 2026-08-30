package com.wudao.controller;

import com.wudao.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping({"/api/thought", "/api/qa"})
public class ThoughtController {

    private final CopyOnWriteArrayList<Map<String, Object>> thoughtList = new CopyOnWriteArrayList<>();

    public ThoughtController() {
    }

    @GetMapping({"/list", "/thought-list"})
    public Result<List<Map<String, Object>>> getThoughtList(@RequestParam(required = false) String type) {
        if (type == null || type.isEmpty() || "ALL".equalsIgnoreCase(type)) {
            return Result.success(thoughtList);
        }
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> item : thoughtList) {
            if (type.equalsIgnoreCase(String.valueOf(item.get("type")))) {
                filtered.add(item);
            }
        }
        return Result.success(filtered);
    }

    @PostMapping({"/publish", "/submit-thought"})
    public Result<String> publishThought(@RequestBody Map<String, Object> payload) {
        String type = String.valueOf(payload.getOrDefault("type", "THOUGHT"));
        String content = String.valueOf(payload.getOrDefault("content", ""));
        String studentName = payload.containsKey("studentName") ? String.valueOf(payload.get("studentName")) : "";
        String roleType = String.valueOf(payload.getOrDefault("roleType", "STUDENT"));
        String danceClassName = String.valueOf(payload.getOrDefault("danceClassName", "二年级"));
        String targetTeacherName = String.valueOf(payload.getOrDefault("targetTeacherName", ""));

        if (content.trim().isEmpty()) {
            return Result.error("发布内容不可为空");
        }

        String nowStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());

        Map<String, Object> item = new HashMap<>();
        item.put("id", com.wudao.common.SnowflakeIdWorker.generateIdStr());
        item.put("studentName", studentName);
        item.put("roleType", roleType);
        item.put("type", type);
        item.put("danceClassName", danceClassName);
        item.put("targetTeacherName", targetTeacherName);
        item.put("title", "THOUGHT".equalsIgnoreCase(type) ? "💭 有感而发" : "💖 说说心里话");
        item.put("content", content.trim());
        item.put("likesCount", 0);
        item.put("createdAt", nowStr);

        thoughtList.add(0, item);
        String label = "THOUGHT".equalsIgnoreCase(type) ? "【有感而发】" : "【说说心里话】";
        return Result.success(label + " 已成功发布！");
    }

    @PostMapping("/like")
    public Result<String> likeThought(@RequestBody Map<String, Object> payload) {
        String id = String.valueOf(payload.get("id"));
        for (Map<String, Object> item : thoughtList) {
            if (id.equals(String.valueOf(item.get("id")))) {
                int count = Integer.parseInt(String.valueOf(item.getOrDefault("likesCount", 0)));
                item.put("likesCount", count + 1);
                break;
            }
        }
        return Result.success("点赞成功！");
    }
}
