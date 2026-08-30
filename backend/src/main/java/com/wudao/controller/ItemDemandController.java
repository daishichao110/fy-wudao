package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.ItemDemand;
import com.wudao.service.ItemDemandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/item-demand")
public class ItemDemandController {

    private static final Logger log = LoggerFactory.getLogger(ItemDemandController.class);

    @Autowired
    private ItemDemandService itemDemandService;

    @GetMapping("/list")
    public Result<List<ItemDemand>> getItemDemands(@RequestParam(required = false) String danceClassName) {
        log.info("[REST API GET /api/item-demand/list] Querying item demands for class: {}", danceClassName);
        List<ItemDemand> list = itemDemandService.getItemDemands(danceClassName);
        return Result.success(list);
    }

    @PostMapping("/enroll")
    public Result<String> enrollItemDemand(@RequestBody Map<String, Object> payload) {
        String itemId = String.valueOf(payload.get("itemId"));
        String parentName = String.valueOf(payload.get("parentName"));
        Integer quantity = Integer.parseInt(String.valueOf(payload.getOrDefault("quantity", 1)));

        log.info("[REST API POST /api/item-demand/enroll] itemId={}, parentName={}, quantity={}", itemId, parentName, quantity);
        String msg = itemDemandService.enrollItemDemand(itemId, parentName, quantity);
        return Result.success(msg);
    }

    @PostMapping("/add")
    public Result<String> addCustomDemand(@RequestBody ItemDemand demand) {
        log.info("[REST API POST /api/item-demand/add] Adding new item demand via MyBatis Mapper XML: {}", demand.getItemName());
        String msg = itemDemandService.createItemDemand(demand);
        return Result.success(msg);
    }

    @PostMapping("/update")
    public Result<String> updateDemand(@RequestBody ItemDemand demand) {
        log.info("[REST API POST /api/item-demand/update] Updating item demand via MyBatis Mapper XML: {}", demand.getItemId());
        String msg = itemDemandService.updateItemDemand(demand);
        return Result.success(msg);
    }

    @GetMapping("/export")
    public Result<String> exportItemDemands() {
        log.info("[REST API GET /api/item-demand/export] Exporting item demands via MyBatis Mapper XML");
        String text = itemDemandService.exportItemDemands();
        return Result.success(text);
    }
}
