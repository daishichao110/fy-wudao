package com.wudao.service;

import com.wudao.entity.BodyMetric;
import java.util.List;

public interface BodyMetricService {
    BodyMetric getLatestMetric(String studentId);
    List<BodyMetric> getMetricHistory(String studentId);
    List<BodyMetric> getAllMetrics();
    BodyMetric saveMetric(BodyMetric metric);
    String generateCsvReport();
}
