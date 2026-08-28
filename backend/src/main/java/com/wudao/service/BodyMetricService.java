package com.wudao.service;

import com.wudao.entity.BodyMetric;
import java.util.List;

public interface BodyMetricService {
    BodyMetric getLatestMetric(Long studentId);
    List<BodyMetric> getMetricHistory(Long studentId);
    List<BodyMetric> getAllMetrics();
    BodyMetric saveMetric(BodyMetric metric);
    String generateCsvReport();
}
