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
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testListPurchases() throws Exception {
        mockMvc.perform(get("/api/purchase/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testCreatePurchase() throws Exception {
        String json = "{\n" +
                "  \"itemName\": \"舞蹈拉伸大把木砖\",\n" +
                "  \"category\": \"教具\",\n" +
                "  \"totalAmount\": 500.00,\n" +
                "  \"unitPrice\": 50.00,\n" +
                "  \"quantity\": 10,\n" +
                "  \"proofUrl\": \"https://example.com/proof.jpg\",\n" +
                "  \"remark\": \"单元测试采购\"\n" +
                "}";

        mockMvc.perform(post("/api/purchase/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
