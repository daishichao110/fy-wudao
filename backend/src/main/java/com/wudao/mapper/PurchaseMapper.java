package com.wudao.mapper;

import com.wudao.entity.Purchase;
import java.util.List;

public interface PurchaseMapper {
    List<Purchase> selectAll();
    int insert(Purchase purchase);
}
