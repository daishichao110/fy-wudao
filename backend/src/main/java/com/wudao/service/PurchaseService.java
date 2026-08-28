package com.wudao.service;

import com.wudao.entity.Purchase;
import java.util.List;

public interface PurchaseService {
    List<Purchase> getAllPurchases();
    Purchase createPurchase(Purchase purchase);
}
