package com.wudao.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BodyMetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetLatestMetric() throws Exception {
        mockMvc.perform(get("/api/metric/student/6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testGetAllMetrics() throws Exception {
        mockMvc.perform(get("/api/metric/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testExportCsv() throws Exception {
        mockMvc.perform(get("/api/metric/export-csv"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveMetric() throws Exception {
        String json = "{\n" +
                "  \"studentId\": 6,\n" +
                "  \"studentName\": \"李小桐\",\n" +
                "  \"heightCm\": 135.5,\n" +
                "  \"weightKg\": 30.0,\n" +
                "  \"bustCm\": 65.0,\n" +
                "  \"waistCm\": 58.0,\n" +
                "  \"hipCm\": 70.0,\n" +
                "  \"torsoLengthCm\": 50.0,\n" +
                "  \"shoeSize\": 32.0,\n" +
                "  \"measuredDate\": \"2026-08-21\"\n" +
                "}";

        mockMvc.perform(post("/api/metric/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
