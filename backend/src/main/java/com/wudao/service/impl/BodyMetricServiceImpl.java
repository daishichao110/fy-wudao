package com.wudao.service.impl;

import com.wudao.entity.BodyMetric;
import com.wudao.entity.User;
import com.wudao.mapper.BodyMetricMapper;
import com.wudao.mapper.UserMapper;
import com.wudao.service.BodyMetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BodyMetricServiceImpl implements BodyMetricService {

    private static final Logger log = LoggerFactory.getLogger(BodyMetricServiceImpl.class);

    @Autowired
    private BodyMetricMapper bodyMetricMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public BodyMetric getLatestMetric(Long studentId) {
        log.info("[BodyMetricService] Executing getLatestMetric() for studentId={}", studentId);
        if (studentId == null || studentId <= 0) {
            log.error("[BodyMetricService] Invalid studentId: {}", studentId);
            throw new IllegalArgumentException("学员ID不合法");
        }
        BodyMetric metric = bodyMetricMapper.selectLatestByStudentId(studentId);
        log.info("[BodyMetricService] Query result for studentId {}: {}", studentId, metric != null ? metric.getHeightCm() + "cm" : "NO_RECORD");
        return metric;
    }

    @Override
    public List<BodyMetric> getMetricHistory(Long studentId) {
        log.info("[BodyMetricService] Executing getMetricHistory() for studentId={}", studentId);
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("学员ID不合法");
        }
        List<BodyMetric> history = bodyMetricMapper.selectHistoryByStudentId(studentId);
        log.info("[BodyMetricService] Fetched {} history metric entries for student {}", history != null ? history.size() : 0, studentId);
        return history;
    }

    @Override
    public List<BodyMetric> getAllMetrics() {
        log.info("[BodyMetricService] Executing getAllMetrics() for Teachers/Admins...");
        List<BodyMetric> list = bodyMetricMapper.selectAll();
        log.info("[BodyMetricService] Fetched {} student metric records for teacher overview", list != null ? list.size() : 0);
        return list;
    }

    @Override
    @Transactional
    public BodyMetric saveMetric(BodyMetric metric) {
        log.info("[BodyMetricService] Executing saveMetric()...");

        // 1. 对象与学员 ID 校验
        if (metric == null) {
            throw new IllegalArgumentException("身材量体数据不可为空");
        }
        if (metric.getStudentId() == null || metric.getStudentId() <= 0) {
            throw new IllegalArgumentException("学员ID不合法");
        }

        // 2. 校验学员是否存在
        User student = userMapper.selectById(metric.getStudentId());
        if (student == null) {
            log.error("[BodyMetricService] Save failed: Student ID {} not found", metric.getStudentId());
            throw new IllegalArgumentException("指定录入身材档案的学员不存在(ID: " + metric.getStudentId() + ")");
        }
        metric.setStudentName(student.getRealName());

        // 3. 数值合理性区间校验 (高度防御)
        if (metric.getHeightCm() == null || metric.getHeightCm().compareTo(new BigDecimal("50.0")) < 0 || metric.getHeightCm().compareTo(new BigDecimal("220.0")) > 0) {
            throw new IllegalArgumentException("身高数值不合理 (须在 50.0cm ~ 220.0cm 之间)");
        }
        if (metric.getWeightKg() == null || metric.getWeightKg().compareTo(new BigDecimal("10.0")) < 0 || metric.getWeightKg().compareTo(new BigDecimal("150.0")) > 0) {
            throw new IllegalArgumentException("体重数值不合理 (须在 10.0kg ~ 150.0kg 之间)");
        }
        if (metric.getBustCm() == null || metric.getBustCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("胸围数值必须大于0");
        }
        if (metric.getWaistCm() == null || metric.getWaistCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("腰围数值必须大于0");
        }
        if (metric.getHipCm() == null || metric.getHipCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("臀围数值必须大于0");
        }
        if (metric.getTorsoLengthCm() == null || metric.getTorsoLengthCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("胴长数值必须大于0");
        }
        if (metric.getShoeSize() == null || metric.getShoeSize().compareTo(new BigDecimal("20.0")) < 0 || metric.getShoeSize().compareTo(new BigDecimal("48.0")) > 0) {
            throw new IllegalArgumentException("舞鞋码数值不合理 (须在 20.0 ~ 48.0 欧码之间)");
        }

        if (!StringUtils.hasText(metric.getMeasuredDate())) {
            metric.setMeasuredDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        if (metric.getMetricId() == null || metric.getMetricId() <= 0) {
            metric.setMetricId(com.wudao.common.SnowflakeIdWorker.generateId());
        }

        bodyMetricMapper.insert(metric);
        log.info("[BodyMetricService] BodyMetric record saved successfully with ID: {}", metric.getMetricId());
        return metric;
    }

    @Override
    public String generateCsvReport() {
        log.info("[BodyMetricService] Generating CSV Performance Costume Order Report...");
        List<BodyMetric> list = bodyMetricMapper.selectAll();
        StringBuilder sb = new StringBuilder();

        // 带有 UTF-8 BOM，保障 Excel 打开不乱码
        sb.append("学员ID,学员姓名,身高(cm),体重(kg),胸围(cm),腰围(cm),臀围(cm),胴长(cm),舞鞋码(欧码),测量日期\n");
        if (list != null) {
            for (BodyMetric m : list) {
                sb.append(m.getStudentId()).append(",")
                  .append(m.getStudentName()).append(",")
                  .append(m.getHeightCm()).append(",")
                  .append(m.getWeightKg()).append(",")
                  .append(m.getBustCm()).append(",")
                  .append(m.getWaistCm()).append(",")
                  .append(m.getHipCm()).append(",")
                  .append(m.getTorsoLengthCm()).append(",")
                  .append(m.getShoeSize()).append(",")
                  .append(m.getMeasuredDate()).append("\n");
            }
        }
        log.info("[BodyMetricService] CSV Report generated successfully. Total records: {}", list != null ? list.size() : 0);
        return sb.toString();
    }
}
