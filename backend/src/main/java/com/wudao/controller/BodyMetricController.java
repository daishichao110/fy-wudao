package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.BodyMetric;
import com.wudao.service.BodyMetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/metric")
public class BodyMetricController {

    private static final Logger log = LoggerFactory.getLogger(BodyMetricController.class);

    @Autowired
    private BodyMetricService bodyMetricService;

    @GetMapping("/student/{studentId}")
    public Result<BodyMetric> getLatestMetric(@PathVariable("studentId") String studentId) {
        log.info("[REST API GET /api/metric/student/{}] Querying latest body metrics", studentId);
        BodyMetric metric = bodyMetricService.getLatestMetric(studentId);
        return Result.success(metric);
    }

    @GetMapping("/history/{studentId}")
    public Result<List<BodyMetric>> getMetricHistory(@PathVariable("studentId") String studentId) {
        log.info("[REST API GET /api/metric/history/{}] Querying metric history", studentId);
        List<BodyMetric> history = bodyMetricService.getMetricHistory(studentId);
        return Result.success(history);
    }

    @GetMapping("/all")
    public Result<List<BodyMetric>> getAllMetrics() {
        log.info("[REST API GET /api/metric/all] Querying all student metrics for Teacher/Admin overview");
        List<BodyMetric> list = bodyMetricService.getAllMetrics();
        return Result.success(list);
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        log.info("[REST API GET /api/metric/export-csv] Exporting performance costume orders CSV report");
        String csvContent = bodyMetricService.generateCsvReport();
        
        // 加上 UTF-8 BOM 字节 (0xEF, 0xBB, 0xBF) 保障 Excel 打开无中文乱码
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        byte[] output = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, output, 0, bom.length);
        System.arraycopy(csvBytes, 0, output, bom.length, csvBytes.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "costume_orders.csv");

        return ResponseEntity.ok().headers(headers).body(output);
    }

    @PostMapping("/save")
    public Result<BodyMetric> saveMetric(@RequestBody BodyMetric metric) {
        log.info("[REST API POST /api/metric/save] Saving body metrics for studentId: {}", metric != null ? metric.getStudentId() : null);
        BodyMetric saved = bodyMetricService.saveMetric(metric);
        return Result.success("身材档案保存成功！", saved);
    }
}
