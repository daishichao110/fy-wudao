package com.wudao.service.impl;

import com.wudao.entity.Purchase;
import com.wudao.mapper.PurchaseMapper;
import com.wudao.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Override
    public List<Purchase> getAllPurchases() {
        log.info("[PurchaseService] Executing getAllPurchases()...");
        List<Purchase> list = purchaseMapper.selectAll();
        log.info("[PurchaseService] Fetched {} public purchase records", list != null ? list.size() : 0);
        return list;
    }

    @Override
    @Transactional
    public Purchase createPurchase(Purchase purchase) {
        log.info("[PurchaseService] Executing createPurchase()...");

        // 1. 基础对象与非空校验
        if (purchase == null) {
            throw new IllegalArgumentException("采购明细参数不可为空");
        }
        if (!StringUtils.hasText(purchase.getItemName())) {
            throw new IllegalArgumentException("采购品目名称不可为空");
        }
        if (!StringUtils.hasText(purchase.getCategory())) {
            throw new IllegalArgumentException("采购类别不可为空(道具/服装/设备等)");
        }

        // 2. 数量与单价合法性校验
        if (purchase.getUnitPrice() == null || purchase.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("采购单价必须大于0元");
        }
        if (purchase.getQuantity() == null || purchase.getQuantity() <= 0) {
            throw new IllegalArgumentException("采购数量必须为大于0的整数");
        }

        // 自动精确计算总价
        BigDecimal total = purchase.getUnitPrice().multiply(new BigDecimal(purchase.getQuantity()));
        purchase.setTotalAmount(total);

        // 发票凭证默认设置后端本地资源路径
        if (!StringUtils.hasText(purchase.getProofUrl())) {
            purchase.setProofUrl("/image/banner1.jpg");
        }

        if (!StringUtils.hasText(purchase.getPurchaseId())) {
            purchase.setPurchaseId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }

        purchaseMapper.insert(purchase);
        log.info("[PurchaseService] Public purchase record created successfully with ID: {}, Total Amount: ￥{}", purchase.getPurchaseId(), purchase.getTotalAmount());
        return purchase;
    }
}
