package com.wudao.mapper;

import com.wudao.entity.ItemDemand;
import com.wudao.entity.ItemDemandEnrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemDemandMapper {

    List<ItemDemand> selectAll(@Param("danceClassName") String danceClassName);

    ItemDemand selectById(@Param("itemId") String itemId);

    int insertDemand(ItemDemand demand);

    int updateDemand(ItemDemand demand);

    List<ItemDemandEnrollment> selectEnrollmentsByItemId(@Param("itemId") String itemId);

    int upsertEnrollment(ItemDemandEnrollment enrollment);

    int updateSizeSummaryStr(@Param("itemId") String itemId, @Param("sizeSummaryStr") String sizeSummaryStr);
}
