package com.wudao.service.impl;

import com.wudao.common.DanceClassEnum;
import com.wudao.common.SnowflakeIdWorker;
import com.wudao.entity.ItemDemand;
import com.wudao.entity.ItemDemandEnrollment;
import com.wudao.mapper.ItemDemandMapper;
import com.wudao.service.ItemDemandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ItemDemandServiceImpl implements ItemDemandService {

    private static final Logger log = LoggerFactory.getLogger(ItemDemandServiceImpl.class);

    @Autowired
    private ItemDemandMapper itemDemandMapper;

    @Override
    public List<ItemDemand> getItemDemands(String danceClassName) {
        String queryCode = (StringUtils.hasText(danceClassName) && !"全校全部".equals(danceClassName)) 
                ? DanceClassEnum.getCodeByName(danceClassName) : "GRADE_ALL";

        log.info("[ItemDemandService] Querying item demands via MyBatis Mapper XML, queryCode: {}", queryCode);
        List<ItemDemand> list = itemDemandMapper.selectAll(queryCode);
        for (ItemDemand item : list) {
            List<ItemDemandEnrollment> enrollments = itemDemandMapper.selectEnrollmentsByItemId(item.getItemId());
            item.setEnrollList(enrollments);
            item.setSignedCount(enrollments != null ? enrollments.size() : 0);

            // 存数据库为英文 Code (如 GRADE_2)，转化为中文给前端展示
            item.setDanceClassName(DanceClassEnum.getNameByCode(item.getDanceClassName()));

            if (enrollments != null && !enrollments.isEmpty()) {
                int totalQty = 0;
                StringBuilder sb = new StringBuilder();
                for (ItemDemandEnrollment er : enrollments) {
                    totalQty += (er.getQuantity() != null ? er.getQuantity() : 1);
                    if (sb.length() > 0) sb.append("; ");
                    sb.append(er.getParentName()).append(" (").append(er.getQuantity()).append("个)");
                }
                item.setSizeSummaryStr("需求总个数: " + totalQty + "个 (" + sb.toString() + ")");
            } else if (!StringUtils.hasText(item.getSizeSummaryStr())) {
                item.setSizeSummaryStr("暂无家长登记需求");
            }
        }
        return list;
    }

    @Override
    @Transactional
    public String createItemDemand(ItemDemand demand) {
        if (demand == null || !StringUtils.hasText(demand.getItemName())) {
            throw new IllegalArgumentException("物品名称不可为空");
        }
        if (!StringUtils.hasText(demand.getItemId())) {
            demand.setItemId(SnowflakeIdWorker.generateIdStr());
        }
        
        // 数据库落库强制为非中文 ENUM CODE
        String code = DanceClassEnum.getCodeByName(demand.getDanceClassName());
        demand.setDanceClassName(code);

        if (!StringUtils.hasText(demand.getArrivalStatus())) {
            demand.setArrivalStatus("未到货");
        }
        demand.setSizeSummaryStr("暂无家长登记需求");

        itemDemandMapper.insertDemand(demand);
        log.info("[ItemDemandService] Inserted new item_demand via MyBatis Mapper XML: itemId={}, code={}", demand.getItemId(), code);
        return "新增物品选购计划成功！";
    }

    @Override
    @Transactional
    public String updateItemDemand(ItemDemand demand) {
        if (demand == null || !StringUtils.hasText(demand.getItemId())) {
            throw new IllegalArgumentException("物品ID不可为空");
        }
        if (StringUtils.hasText(demand.getDanceClassName())) {
            demand.setDanceClassName(DanceClassEnum.getCodeByName(demand.getDanceClassName()));
        }
        itemDemandMapper.updateDemand(demand);
        return "物品选购到货状态更新成功！";
    }

    @Override
    @Transactional
    public String enrollItemDemand(String itemId, String parentName, Integer quantity) {
        if (!StringUtils.hasText(itemId) || "null".equals(itemId)) {
            throw new IllegalArgumentException("物品ID不可为空");
        }
        if (!StringUtils.hasText(parentName) || "null".equals(parentName)) {
            throw new IllegalArgumentException("填报家长姓名不可为空");
        }
        if (quantity == null || quantity <= 0) quantity = 1;

        ItemDemandEnrollment enrollment = new ItemDemandEnrollment();
        enrollment.setEnrollmentId(SnowflakeIdWorker.generateIdStr());
        enrollment.setItemId(itemId);
        enrollment.setParentName(parentName);
        enrollment.setQuantity(quantity);

        itemDemandMapper.upsertEnrollment(enrollment);

        List<ItemDemandEnrollment> enrollments = itemDemandMapper.selectEnrollmentsByItemId(itemId);
        int totalQty = 0;
        StringBuilder sb = new StringBuilder();
        for (ItemDemandEnrollment er : enrollments) {
            totalQty += (er.getQuantity() != null ? er.getQuantity() : 1);
            if (sb.length() > 0) sb.append("; ");
            sb.append(er.getParentName()).append(" (").append(er.getQuantity()).append("个)");
        }
        String summary = "需求总个数: " + totalQty + "个 (" + sb.toString() + ")";
        itemDemandMapper.updateSizeSummaryStr(itemId, summary);

        log.info("[ItemDemandService] Upserted item_demand_enrollment via MyBatis Mapper XML: itemId={}, parent={}, qty={}", itemId, parentName, quantity);
        return "选购需求成功登记！";
    }

    @Override
    public String exportItemDemands() {
        List<ItemDemand> items = itemDemandMapper.selectAll(null);
        StringBuilder sb = new StringBuilder();
        sb.append("【劲松金帆舞团 - 全量物品选购明细汇总 (姓名与个数)】\n");
        sb.append("----------------------------------------\n");

        if (items == null || items.isEmpty()) {
            sb.append("暂无选购计划与登记数据\n");
            return sb.toString();
        }

        for (int i = 0; i < items.size(); i++) {
            ItemDemand item = items.get(i);
            String displayName = DanceClassEnum.getNameByCode(item.getDanceClassName());
            sb.append("📦 [").append(i + 1).append("] 物品: ").append(item.getItemName()).append(" (").append(displayName).append(")\n");
            sb.append("   截止日期: ").append(item.getDeadline()).append(" | 到货状态: ").append(item.getArrivalStatus()).append("\n");
            sb.append("   姓名与个数明细:\n");

            List<ItemDemandEnrollment> enrollments = itemDemandMapper.selectEnrollmentsByItemId(item.getItemId());
            if (enrollments == null || enrollments.isEmpty()) {
                sb.append("     - 暂无登记数据\n");
            } else {
                for (ItemDemandEnrollment er : enrollments) {
                    sb.append("     - ").append(er.getParentName()).append(": ").append(er.getQuantity()).append("个\n");
                }
            }
            sb.append("----------------------------------------\n");
        }

        return sb.toString();
    }
}
