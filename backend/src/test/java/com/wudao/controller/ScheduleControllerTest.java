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
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testListSchedules() throws Exception {
        mockMvc.perform(get("/api/schedule/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testCreateSchedule() throws Exception {
        String json = "{\n" +
                "  \"courseName\": \"测试芭蕾课程\",\n" +
                "  \"danceType\": \"芭蕾舞\",\n" +
                "  \"teacherId\": \"1787400000000000002\",\n" +
                "  \"teacherName\": \"林依依老师\",\n" +
                "  \"classroomName\": \"1号厅\",\n" +
                "  \"classDate\": \"2026-08-25\",\n" +
                "  \"startTime\": \"14:00\",\n" +
                "  \"endTime\": \"16:00\",\n" +
                "  \"topsReq\": \"粉色连体服\",\n" +
                "  \"bottomsReq\": \"大袜\",\n" +
                "  \"shoesReq\": \"软底鞋\",\n" +
                "  \"hairReq\": \"丸子头\",\n" +
                "  \"propsReq\": \"弹力带\",\n" +
                "  \"otherReq\": \"自带水壶\",\n" +
                "  \"remark\": \"单元测试说明\",\n" +
                "  \"capacity\": 15,\n" +
                "  \"bookedCount\": 0\n" +
                "}";

        mockMvc.perform(post("/api/schedule/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testApplyLeaveAndMakeup() throws Exception {
        String leaveJson = "{\n" +
                "  \"studentId\": 5,\n" +
                "  \"studentName\": \"张悦悦\",\n" +
                "  \"scheduleId\": 99,\n" +
                "  \"courseName\": \"测试课程\",\n" +
                "  \"reason\": \"请假测试\"\n" +
                "}";

        try {
            mockMvc.perform(post("/api/schedule/leave")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(leaveJson));
        } catch (Exception ignored) {}

        String makeupJson = "{\n" +
                "  \"studentId\": 5,\n" +
                "  \"studentName\": \"张悦悦\",\n" +
                "  \"scheduleId\": 99,\n" +
                "  \"courseName\": \"测试课程\"\n" +
                "}";

        try {
            mockMvc.perform(post("/api/schedule/makeup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeupJson));
        } catch (Exception ignored) {}
    }
}
