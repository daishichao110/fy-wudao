package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.Purchase;
import com.wudao.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private static final Logger log = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/list")
    public Result<List<Purchase>> listPurchases() {
        log.info("[REST API GET /api/purchase/list] Querying cost disclosure purchase records");
        List<Purchase> purchases = purchaseService.getAllPurchases();
        return Result.success(purchases);
    }

    @PostMapping("/create")
    public Result<Purchase> createPurchase(@RequestBody Purchase purchase) {
        log.info("[REST API POST /api/purchase/create] Adding new purchase record: {}", purchase.getItemName());
        Purchase res = purchaseService.createPurchase(purchase);
        return Result.success("采购信息及公示已成功录入", res);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/create-dynamic")
    public Result<String> createDynamicPurchase(@RequestBody Map<String, Object> payload) {
        String title = String.valueOf(payload.getOrDefault("title", "家委集中采购动态需求"));
        Object itemsObj = payload.get("items");

        if (!(itemsObj instanceof List)) {
            return Result.error("请至少添加一项采购明细表格行");
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
        if (items.isEmpty()) {
            return Result.error("请至少添加一项采购明细表格行");
        }

        for (Map<String, Object> row : items) {
            String name = String.valueOf(row.getOrDefault("name", "未命名物品"));
            BigDecimal price = new BigDecimal(String.valueOf(row.getOrDefault("price", "0")));
            Integer count = Integer.valueOf(String.valueOf(row.getOrDefault("count", "1")));

            Purchase p = new Purchase();
            p.setItemName("【" + title + "】" + name);
            p.setCategory("家委采买");
            p.setQuantity(count);
            p.setUnitPrice(price);
            p.setTotalAmount(price.multiply(new BigDecimal(count)));
            p.setRemark("家委采购公示 / 预算数量: " + count);
            p.setProofUrl("/image/purchase_proof.jpg");
            p.setCreatedAt(new Date());
            purchaseService.createPurchase(p);
        }

        return Result.success("动态采购明细表格已成功发布公示！");
    }
}
