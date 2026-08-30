package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.common.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
@RequestMapping("/api/item-demand")
public class ItemDemandController {

    private static final Logger log = LoggerFactory.getLogger(ItemDemandController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initDatabaseTable() {
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS item_demand (" +
                    "item_id VARCHAR(64) PRIMARY KEY COMMENT '物品ID'," +
                    "item_name VARCHAR(100) NOT NULL COMMENT '物品名称'," +
                    "dance_class_name VARCHAR(50) DEFAULT '全校/公共' COMMENT '适用年级'," +
                    "deadline VARCHAR(50) DEFAULT '' COMMENT '截止日期'," +
                    "expected_arrival_date VARCHAR(50) DEFAULT '' COMMENT '预计到货日期'," +
                    "arrival_status VARCHAR(50) DEFAULT '未到货' COMMENT '到货状态'," +
                    "size_summary_str TEXT COMMENT '需求个数汇总文本'," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;");

            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS item_demand_enrollment (" +
                    "enrollment_id VARCHAR(64) PRIMARY KEY COMMENT '登记ID'," +
                    "item_id VARCHAR(64) NOT NULL COMMENT '物品ID'," +
                    "parent_name VARCHAR(50) NOT NULL COMMENT '家长/填报人姓名'," +
                    "quantity INT NOT NULL DEFAULT 1 COMMENT '购买个数'," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "UNIQUE KEY uk_item_parent (item_id, parent_name)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;");

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_demand", Integer.class);
            if (count == null || count == 0) {
                String id1 = SnowflakeIdWorker.generateIdStr();
                jdbcTemplate.update("INSERT INTO item_demand (item_id, item_name, dance_class_name, deadline, expected_arrival_date, arrival_status, size_summary_str) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        id1, "双皮头芭蕾练功软鞋 (粉色)", "二年级", "2026-08-30", "2026-09-05", "未到货", "总个数: 3个 (张小雅家长 (1个); 李美涵家长 (2个))");

                jdbcTemplate.update("INSERT INTO item_demand_enrollment (enrollment_id, item_id, parent_name, quantity) VALUES (?, ?, ?, ?)",
                        SnowflakeIdWorker.generateIdStr(), id1, "张小雅家长", 1);
                jdbcTemplate.update("INSERT INTO item_demand_enrollment (enrollment_id, item_id, parent_name, quantity) VALUES (?, ?, ?, ?)",
                        SnowflakeIdWorker.generateIdStr(), id1, "李美涵家长", 2);

                String id2 = SnowflakeIdWorker.generateIdStr();
                jdbcTemplate.update("INSERT INTO item_demand_enrollment (enrollment_id, item_id, parent_name, quantity) VALUES (?, ?, ?, ?)",
                        SnowflakeIdWorker.generateIdStr(), id2, "王思琪家长", 1);
                jdbcTemplate.update("INSERT INTO item_demand (item_id, item_name, dance_class_name, deadline, expected_arrival_date, arrival_status, size_summary_str) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        id2, "高盘丸子头专业网发圈", "全校/公共", "2026-09-01", "2026-09-03", "已全到货", "总个数: 1个 (王思琪家长 (1个))");
            }
            log.info("[MySQL DB] Initialized item_demand & item_demand_enrollment tables successfully!");
        } catch (Exception e) {
            log.error("[MySQL DB] Error initializing item_demand tables: {}", e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getItemDemands(@RequestParam(required = false) String danceClassName) {
        log.info("[REST API GET /api/item-demand/list] Querying item demands for class: {}", danceClassName);
        String sql = "SELECT item_id, item_name, dance_class_name, deadline, expected_arrival_date, arrival_status, size_summary_str, created_at FROM item_demand ";
        List<Map<String, Object>> rows;
        if (danceClassName != null && !danceClassName.trim().isEmpty() && !"全校全部".equals(danceClassName)) {
            sql += "WHERE dance_class_name = ? OR dance_class_name = '全校/公共' OR dance_class_name = '全校全部' ORDER BY created_at DESC";
            rows = jdbcTemplate.queryForList(sql, danceClassName.trim());
        } else {
            sql += "ORDER BY created_at DESC";
            rows = jdbcTemplate.queryForList(sql);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            String itemId = String.valueOf(row.get("item_id"));
            item.put("itemId", itemId);
            item.put("itemName", row.get("item_name"));
            item.put("danceClassName", row.get("dance_class_name"));
            item.put("deadline", row.get("deadline"));
            item.put("expectedArrivalDate", row.get("expected_arrival_date"));
            item.put("arrivalStatus", row.get("arrival_status"));

            List<Map<String, Object>> enrollRows = jdbcTemplate.queryForList(
                    "SELECT parent_name, quantity FROM item_demand_enrollment WHERE item_id = ? ORDER BY created_at ASC", itemId);

            List<Map<String, Object>> enrollList = new ArrayList<>();
            int totalQty = 0;
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> er : enrollRows) {
                String pName = String.valueOf(er.get("parent_name"));
                int qty = Integer.parseInt(String.valueOf(er.get("quantity")));
                totalQty += qty;

                Map<String, Object> emMap = new HashMap<>();
                emMap.put("name", pName);
                emMap.put("count", qty);
                enrollList.add(emMap);

                if (sb.length() > 0) sb.append("; ");
                sb.append(pName).append(" (").append(qty).append("个)");
            }

            item.put("signedCount", enrollList.size());
            item.put("enrollList", enrollList);
            if (enrollList.size() > 0) {
                item.put("sizeSummaryStr", "需求总个数: " + totalQty + "个 (" + sb.toString() + ")");
            } else {
                item.put("sizeSummaryStr", row.get("size_summary_str") != null ? row.get("size_summary_str") : "暂无家长登记需求");
            }
            result.add(item);
        }
        return Result.success(result);
    }

    @PostMapping("/enroll")
    public Result<String> enrollItemDemand(@RequestBody Map<String, Object> payload) {
        String itemId = String.valueOf(payload.get("itemId"));
        String parentName = String.valueOf(payload.get("parentName"));
        Integer quantity = Integer.parseInt(String.valueOf(payload.getOrDefault("quantity", 1)));

        log.info("[REST API POST /api/item-demand/enroll] itemId={}, parentName={}, quantity={}", itemId, parentName, quantity);

        if (itemId == null || itemId.trim().isEmpty() || "null".equals(itemId)) {
            return Result.error("物品ID不可为空");
        }
        if (parentName == null || parentName.trim().isEmpty() || "null".equals(parentName)) {
            parentName = "热心家长";
        }
        if (quantity == null || quantity <= 0) quantity = 1;

        String enrollId = SnowflakeIdWorker.generateIdStr();
        jdbcTemplate.update("INSERT INTO item_demand_enrollment (enrollment_id, item_id, parent_name, quantity) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)",
                enrollId, itemId, parentName, quantity);

        List<Map<String, Object>> enrollRows = jdbcTemplate.queryForList(
                "SELECT parent_name, quantity FROM item_demand_enrollment WHERE item_id = ? ORDER BY created_at ASC", itemId);

        int totalQty = 0;
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> er : enrollRows) {
            String pName = String.valueOf(er.get("parent_name"));
            int qty = Integer.parseInt(String.valueOf(er.get("quantity")));
            totalQty += qty;
            if (sb.length() > 0) sb.append("; ");
            sb.append(pName).append(" (").append(qty).append("个)");
        }
        String summary = "需求总个数: " + totalQty + "个 (" + sb.toString() + ")";
        jdbcTemplate.update("UPDATE item_demand SET size_summary_str = ? WHERE item_id = ?", summary, itemId);

        return Result.success("选购需求成功登记保存至数据库！");
    }

    @PostMapping("/add")
    public Result<String> addCustomDemand(@RequestBody Map<String, Object> payload) {
        String itemName = String.valueOf(payload.getOrDefault("itemName", ""));
        String danceClassName = payload.containsKey("danceClassName") ? String.valueOf(payload.get("danceClassName")) : "全校/公共";
        String deadline = payload.containsKey("deadline") ? String.valueOf(payload.get("deadline")) : "";
        String expectedArrivalDate = payload.containsKey("expectedArrivalDate") ? String.valueOf(payload.get("expectedArrivalDate")) : "";

        String itemId = SnowflakeIdWorker.generateIdStr();
        jdbcTemplate.update("INSERT INTO item_demand (item_id, item_name, dance_class_name, deadline, expected_arrival_date, arrival_status, size_summary_str) VALUES (?, ?, ?, ?, ?, ?, ?)",
                itemId, itemName, danceClassName, deadline, expectedArrivalDate, "未到货", "暂无家长登记需求");
        log.info("[MySQL DB] Inserted new item_demand into MySQL: id={}, name={}, class={}", itemId, itemName, danceClassName);
        return Result.success("新增物品选购计划成功发布保存至数据库！");
    }

    @PostMapping("/update")
    public Result<String> updateDemand(@RequestBody Map<String, Object> payload) {
        String itemId = String.valueOf(payload.get("itemId"));
        String arrivalStatus = payload.containsKey("arrivalStatus") ? String.valueOf(payload.get("arrivalStatus")) : null;

        if (itemId != null && arrivalStatus != null) {
            jdbcTemplate.update("UPDATE item_demand SET arrival_status = ? WHERE item_id = ?", arrivalStatus, itemId);
        }
        return Result.success("到货状态成功更新！");
    }

    @GetMapping("/export")
    public Result<String> exportItemDemands() {
        List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT item_id, item_name, dance_class_name, deadline, arrival_status FROM item_demand ORDER BY created_at DESC");
        StringBuilder sb = new StringBuilder();
        sb.append("【劲松金帆舞团 - 全量物品选购明细汇总 (姓名与个数)】\n");
        sb.append("----------------------------------------\n");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String itemId = String.valueOf(item.get("item_id"));
            String itemName = String.valueOf(item.get("item_name"));
            String className = String.valueOf(item.get("dance_class_name"));
            String deadline = String.valueOf(item.get("deadline"));
            String status = String.valueOf(item.get("arrival_status"));

            sb.append("📦 [").append(i + 1).append("] 物品: ").append(itemName).append(" (").append(className).append(")\n");
            sb.append("   截止日期: ").append(deadline).append(" | 到货状态: ").append(status).append("\n");
            sb.append("   姓名与个数明细:\n");

            List<Map<String, Object>> enrollRows = jdbcTemplate.queryForList(
                    "SELECT parent_name, quantity FROM item_demand_enrollment WHERE item_id = ? ORDER BY created_at ASC", itemId);

            if (enrollRows.isEmpty()) {
                sb.append("     - 暂无登记数据\n");
            } else {
                for (Map<String, Object> er : enrollRows) {
                    sb.append("     - ").append(er.get("parent_name")).append(": ").append(er.get("quantity")).append("个\n");
                }
            }
            sb.append("----------------------------------------\n");
        }

        return Result.success(sb.toString());
    }
}
