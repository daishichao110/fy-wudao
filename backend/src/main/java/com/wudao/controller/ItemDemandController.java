package com.wudao.controller;

import com.wudao.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/item-demand")
public class ItemDemandController {

    private final CopyOnWriteArrayList<Map<String, Object>> demandList = new CopyOnWriteArrayList<>();

    public ItemDemandController() {
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getItemDemands() {
        return Result.success(demandList);
    }

    @PostMapping("/add")
    public Result<String> addCustomDemand(@RequestBody Map<String, Object> payload) {
        String itemName = String.valueOf(payload.getOrDefault("itemName", "自定义物品"));
        Integer quantity = Integer.valueOf(String.valueOf(payload.getOrDefault("quantity", 1)));
        String spec = String.valueOf(payload.getOrDefault("spec", "自定义规格/化妆用品"));
        String studentName = payload.containsKey("studentName") ? String.valueOf(payload.get("studentName")) : "";

        Map<String, Object> item = new HashMap<>();
        item.put("itemId", com.wudao.common.SnowflakeIdWorker.generateIdStr());
        item.put("itemName", itemName);
        item.put("spec", spec);
        item.put("unitPrice", "￥0.00");
        item.put("needIt", true);
        item.put("quantity", quantity);
        item.put("studentName", studentName);
        demandList.add(item);
        return Result.success("新增物品需求成功！");
    }

    @PostMapping("/update")
    public Result<String> updateDemand(@RequestBody Map<String, Object> payload) {
        String itemId = String.valueOf(payload.get("itemId"));
        Boolean needIt = (Boolean) payload.getOrDefault("needIt", true);
        Integer quantity = Integer.valueOf(String.valueOf(payload.getOrDefault("quantity", 1)));
        String itemName = payload.containsKey("itemName") ? String.valueOf(payload.get("itemName")) : null;

        for (Map<String, Object> item : demandList) {
            if (itemId.equals(String.valueOf(item.get("itemId")))) {
                item.put("needIt", needIt);
                item.put("quantity", quantity);
                if (itemName != null && !itemName.trim().isEmpty()) {
                    item.put("itemName", itemName.trim());
                }
                break;
            }
        }
        return Result.success("物品需求修改保存成功！");
    }

    @GetMapping("/export")
    public Result<String> exportItemDemands() {
        StringBuilder csv = new StringBuilder();
        csv.append("物品ID,物品名称,规格要求,预估单价,学员姓名,是否需要,需求数量\n");
        for (Map<String, Object> item : demandList) {
            csv.append(item.get("itemId")).append(",")
               .append(item.get("itemName")).append(",")
               .append(item.get("spec")).append(",")
               .append(item.get("unitPrice")).append(",")
               .append(item.get("studentName")).append(",")
               .append(((Boolean) item.get("needIt")) ? "是" : "否").append(",")
               .append(item.get("quantity")).append("\n");
        }
        return Result.success(csv.toString());
    }
}
