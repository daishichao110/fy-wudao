package com.wudao.mapper;

import com.wudao.entity.BodyMetric;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BodyMetricMapper {
    BodyMetric selectLatestByStudentId(@Param("studentId") String studentId);
    List<BodyMetric> selectHistoryByStudentId(@Param("studentId") String studentId);
    List<BodyMetric> selectAll();
    int insert(BodyMetric metric);
}
