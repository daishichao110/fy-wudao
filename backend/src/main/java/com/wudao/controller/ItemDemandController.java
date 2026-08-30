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
        Map<String, Object> item1 = new HashMap<>();
        item1.put("itemId", 101L);
        item1.put("itemName", "舞台防水遮瑕粉饼");
        item1.put("spec", "舞台高定防汗持久型");
        item1.put("unitPrice", "￥45.00");
        item1.put("needIt", true);
        item1.put("quantity", 2);
        item1.put("studentName", "李小桐");
        demandList.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("itemId", 102L);
        item2.put("itemName", "舞台演出哑光红口红");
        item2.put("spec", "复古正红 / 显白不脱色");
        item2.put("unitPrice", "￥38.00");
        item2.put("needIt", true);
        item2.put("quantity", 1);
        item2.put("studentName", "李小桐");
        demandList.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("itemId", 103L);
        item3.put("itemName", "舞台定型发胶与隐形发网");
        item3.put("spec", "强力定型发胶 + 黑色发网3个");
        item3.put("unitPrice", "￥25.00");
        item3.put("needIt", true);
        item3.put("quantity", 1);
        item3.put("studentName", "李小桐");
        demandList.add(item3);
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
        String studentName = String.valueOf(payload.getOrDefault("studentName", "李小桐"));

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
