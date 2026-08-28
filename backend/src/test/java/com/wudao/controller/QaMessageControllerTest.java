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
public class QaMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetMyMessages() throws Exception {
        mockMvc.perform(get("/api/qa/my-messages?userId=6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testGetFeaturedList() throws Exception {
        mockMvc.perform(get("/api/qa/featured-list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testAskReplyAndFeatureFlow() throws Exception {
        String askJson = "{\n" +
                "  \"studentId\": 6,\n" +
                "  \"studentName\": \"李小桐\",\n" +
                "  \"teacherId\": 2,\n" +
                "  \"teacherName\": \"林依依老师\",\n" +
                "  \"questionContent\": \"测试提问：芭蕾转关身体平衡技巧\"\n" +
                "}";

        mockMvc.perform(post("/api/qa/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(askJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String replyJson = "{\n" +
                "  \"msgId\": 1,\n" +
                "  \"replyContent\": \"测试回复：注意收腹与核心稳定\"\n" +
                "}";

        mockMvc.perform(post("/api/qa/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
