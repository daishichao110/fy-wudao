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
        Map<String, Object> t1 = new HashMap<>();
        t1.put("id", 1L);
        t1.put("studentName", "李小桐(家长)");
        t1.put("roleType", "STUDENT");
        t1.put("type", "THOUGHT"); // 有感而发 (全员可见)
        t1.put("title", "💭 有感而发");
        t1.put("content", "今天陪孩子练习芭蕾基训的擦地和软开度，看到孩子从刚开始压腿哭鼻子到现在能坚持做完一组动作，作为家长心里非常感动！感谢教务处和老师们的悉心指导，孩子的体态和自信都有了很大提升！");
        t1.put("likesCount", 8);
        t1.put("createdAt", "2026-08-23 16:30");
        thoughtList.add(t1);

        Map<String, Object> t2 = new HashMap<>();
        t2.put("id", 2L);
        t2.put("studentName", "李小桐(家长)");
        t2.put("targetTeacherName", "林依依老师(芭蕾首席导师)");
        t2.put("roleType", "STUDENT");
        t2.put("type", "HEART"); // 说说心里话 (定向私密)
        t2.put("title", "💖 说说心里话");
        t2.put("content", "林老师您好！孩子最近回家后总提到上课时您对她的表扬，学习舞蹈的积极性高了很多。想私下向您请教一下，在家练习左脚抱腿时需要注意什么细节呢？感谢林老师！");
        t2.put("likesCount", 0);
        t2.put("createdAt", "2026-08-23 17:15");
        thoughtList.add(t2);
    }

    @GetMapping({"/list", "/thought-list"})
    public Result<List<Map<String, Object>>> getThoughtList(@RequestParam(required = false) String type) {
        if (type == null || type.isEmpty()) {
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
        String studentName = String.valueOf(payload.getOrDefault("studentName", "李小桐(家长)"));
        String roleType = String.valueOf(payload.getOrDefault("roleType", "STUDENT"));
        String targetTeacherName = String.valueOf(payload.getOrDefault("targetTeacherName", ""));

        if (content.trim().isEmpty()) {
            return Result.error("发布内容不可为空");
        }

        Map<String, Object> item = new HashMap<>();
        item.put("id", System.currentTimeMillis());
        item.put("studentName", studentName);
        item.put("roleType", roleType);
        item.put("type", type);
        item.put("targetTeacherName", targetTeacherName);
        item.put("title", "THOUGHT".equalsIgnoreCase(type) ? "💭 有感而发" : "💖 说说心里话");
        item.put("content", content.trim());
        item.put("likesCount", 0);
        item.put("createdAt", "2026-08-23 20:50");

        thoughtList.add(0, item);
        String label = "THOUGHT".equalsIgnoreCase(type) ? "【有感而发】" : "【说说心里话】";
        return Result.success(label + " 已成功发布！");
    }

    @PostMapping("/like")
    public Result<String> likeThought(@RequestBody Map<String, Object> payload) {
        Long id = Long.valueOf(String.valueOf(payload.get("id")));
        for (Map<String, Object> item : thoughtList) {
            if (id.equals(item.get("id"))) {
                int count = Integer.parseInt(String.valueOf(item.getOrDefault("likesCount", 0)));
                item.put("likesCount", count + 1);
                break;
            }
        }
        return Result.success("点赞成功！");
    }
}
