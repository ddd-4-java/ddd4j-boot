package io.ddd4j.boot.sample.order;

import io.ddd4j.core.auth.AuthRequest;
import io.ddd4j.core.subject.SubjectProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties =
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration")
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubjectProvider subjectProvider;

    @Test
    void shouldCreateAndReadOrderThroughSharedApplicationKernel() throws Exception {
        String authorization = "Bearer " + subjectProvider.getSubject().login(AuthRequest.of("boot-sample-user"));
        String response = mockMvc.perform(post("/api/orders")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderNo\":\"BOOT-ORDER-001\",\"buyerId\":\"buyer-1\",\"buyerName\":\"Alice\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNo").value("BOOT-ORDER-001"))
                .andReturn().getResponse().getContentAsString();
        String orderId = response.replaceFirst("(?s).*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(post("/api/orders/{orderId}/lines", orderId)
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":\"goods-1\",\"goodsName\":\"DDD Book\",\"quantity\":2,\"unitPrice\":\"59.90\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAmount").value(119.80));

        mockMvc.perform(post("/api/orders/{orderId}/pay", orderId)
                        .header("Authorization", authorization)
                        .header("Idempotency-Key", "payment-BOOT-ORDER-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNo").value("BOOT-ORDER-001"));
    }
}
