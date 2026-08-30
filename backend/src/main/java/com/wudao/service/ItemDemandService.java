package com.wudao.service;

import com.wudao.entity.ItemDemand;
import com.wudao.entity.ItemDemandEnrollment;

import java.util.List;

public interface ItemDemandService {

    List<ItemDemand> getItemDemands(String danceClassName);

    String createItemDemand(ItemDemand demand);

    String updateItemDemand(ItemDemand demand);

    String enrollItemDemand(String itemId, String parentName, Integer quantity);

    String exportItemDemands();
}
