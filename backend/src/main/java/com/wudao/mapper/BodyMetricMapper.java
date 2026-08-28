package com.wudao.mapper;

import com.wudao.entity.BodyMetric;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BodyMetricMapper {
    BodyMetric selectLatestByStudentId(@Param("studentId") Long studentId);
    List<BodyMetric> selectHistoryByStudentId(@Param("studentId") Long studentId);
    List<BodyMetric> selectAll();
    int insert(BodyMetric metric);
}
