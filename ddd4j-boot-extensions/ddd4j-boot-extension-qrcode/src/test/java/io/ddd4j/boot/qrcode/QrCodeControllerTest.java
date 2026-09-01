package io.ddd4j.boot.qrcode;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.ddd4j.extension.qrcode.DefaultQrCodeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.lang.reflect.Type;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class QrCodeControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private DefaultQrCodeService service;
    private QrCodeProperties props;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = new DefaultQrCodeService();
        props = new QrCodeProperties();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QrCodeController(service, props))
                .setControllerAdvice(new QrCodeExceptionHandler())
                .setMessageConverters(new Jackson3JsonHttpMessageConverter(), new org.springframework.http.converter.ByteArrayHttpMessageConverter())
                .build();
    }

    @AfterEach
    void tearDown() {
        if (service != null) {
            service.close();
        }
    }

    @Test
    void render_shouldReturnPngImage() throws Exception {
        String json = objectMapper.writeValueAsString(renderRequest("hello-qr", 256, 256));

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/qrcodes/render")
                                .contentType("application/json")
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("image/png"))
                .andReturn().getResponse();

        byte[] body = response.getContentAsByteArray();
        assertThat(body).isNotEmpty();
    }

    @Test
    void batch_twoValidItems_shouldBothSucceed() throws Exception {
        String json = objectMapper.writeValueAsString(Arrays.asList(
                batchItem("item-1", "batch-1"),
                batchItem("item-2", "batch-2")));

        String responseBody = mockMvc.perform(
                        MockMvcRequestBuilders.post("/qrcodes/batch")
                                .contentType("application/json")
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode items = objectMapper.readTree(responseBody);
        assertThat(items).hasSize(2);

        for (int i = 0; i < 2; i++) {
            assertThat(items.get(i).get("success").asBoolean()).isTrue();
            assertThat(items.get(i).get("dataUri").asText()).startsWith("data:image/png;base64,");
            assertThat(items.get(i).get("dataUri").asText().length()).isGreaterThan("data:image/png;base64,".length());
        }
        assertThat(items.get(0).get("itemId").asText()).isEqualTo("item-1");
        assertThat(items.get(1).get("itemId").asText()).isEqualTo("item-2");
    }

    @Test
    void batch_blankItemId_shouldReturnPerItemFailure() throws Exception {
        // One valid item + one with blank itemId (service-level guard in createBatchTask)
        String json = objectMapper.writeValueAsString(Arrays.asList(
                batchItem("item-ok", "valid-content"),
                batchItem("", "also-valid-content")));

        String responseBody = mockMvc.perform(
                        MockMvcRequestBuilders.post("/qrcodes/batch")
                                .contentType("application/json")
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode items = objectMapper.readTree(responseBody);
        assertThat(items).hasSize(2);

        // First item succeeds
        assertThat(items.get(0).get("itemId").asText()).isEqualTo("item-ok");
        assertThat(items.get(0).get("success").asBoolean()).isTrue();
        assertThat(items.get(0).get("dataUri").asText()).startsWith("data:image/png;base64,");

        // Second item fails because blank itemId triggers guard in createBatchTask
        assertThat(items.get(1).get("itemId").asText()).isEqualTo("");
        assertThat(items.get(1).get("success").asBoolean()).isFalse();
        assertThat(items.get(1).get("errorCode").asText()).isEqualTo("QRCODE_RENDER_FAILED");
        assertThat(items.get(1).get("dataUri").isNull()).isTrue();
    }

    @Test
    void batch_blankContent_shouldReturn400() throws Exception {
        // Blank content is rejected by QrCodeRequest constructor (IllegalArgumentException)
        // which the exception handler maps to 400 QRCODE_INVALID_ARGUMENT.
        // This is a whole-request failure (not per-item), because the exception
        // is thrown during the controller's for-loop before reaching the service.
        String json = objectMapper.writeValueAsString(Arrays.asList(
                batchItem("item-1", "valid"),
                batchItem("item-2", "")));

        String responseBody = mockMvc.perform(
                        MockMvcRequestBuilders.post("/qrcodes/batch")
                                .contentType("application/json")
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(responseBody);
        assertThat(error.get("code").asText()).isEqualTo("QRCODE_INVALID_ARGUMENT");
        assertThat(error.get("message").asText()).contains("content must not be blank");
    }

    @Test
    void decode_roundTrip_shouldReturnOriginalContent() throws Exception {
        // First render a QR code
        byte[] qrBytes = mockMvc.perform(
                        MockMvcRequestBuilders.post("/qrcodes/render")
                                .contentType("application/json")
                                .content("{\"content\":\"round-trip-test\",\"width\":256,\"height\":256}"))
                .andReturn().getResponse().getContentAsByteArray();

        // Now decode it
        MockMultipartFile file = new MockMultipartFile(
                "file", "qr.png", "image/png", qrBytes);

        String responseBody = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/qrcodes/decode").file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode result = objectMapper.readTree(responseBody);
        assertThat(result.get("results")).isNotEmpty();
        assertThat(result.get("results").get(0).get("text").asText()).isEqualTo("round-trip-test");
    }

    @Test
    void decode_oversizedFile_shouldReturn400() throws Exception {
        // Set a tiny upload limit
        props.setMaxUploadBytes(10);
        // Rebuild MockMvc with the updated props
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QrCodeController(service, props))
                .setControllerAdvice(new QrCodeExceptionHandler())
                .setMessageConverters(new Jackson3JsonHttpMessageConverter(), new org.springframework.http.converter.ByteArrayHttpMessageConverter())
                .build();

        byte[] bigBytes = new byte[100];
        Arrays.fill(bigBytes, (byte) 0xFF);
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.png", "image/png", bigBytes);

        String responseBody = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/qrcodes/decode").file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(responseBody);
        assertThat(error.get("code").asText()).isEqualTo("QRCODE_INVALID_ARGUMENT");
    }

    private QrCodeController.RenderRequest renderRequest(String content, int width, int height) {
        QrCodeController.RenderRequest req = new QrCodeController.RenderRequest();
        req.setContent(content);
        req.setWidth(width);
        req.setHeight(height);
        return req;
    }

    private QrCodeController.BatchRenderRequest batchItem(String itemId, String content) {
        QrCodeController.BatchRenderRequest item = new QrCodeController.BatchRenderRequest();
        item.setItemId(itemId);
        item.setRequest(renderRequest(content, 256, 256));
        return item;
    }
    /**
     * Jackson 3 最小 JSON 转换器（standalone MockMvc 用；Spring 6 无官方 Jackson3 集成）。
     */
    static class Jackson3JsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements org.springframework.http.converter.GenericHttpMessageConverter<Object> {

        private final ObjectMapper mapper = new ObjectMapper();

        Jackson3JsonHttpMessageConverter() {
            super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
            return canRead(mediaType);
        }

        @Override
        public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
            return canWrite(mediaType);
        }

        @Override
        public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, org.springframework.http.converter.HttpMessageNotReadableException {
            // Jackson 3：泛型容器（List<BatchRenderRequest>）必须走 JavaType，否则退化为 LinkedHashMap
            return mapper.readValue(inputMessage.getBody(), mapper.constructType(type));
        }

        @Override
        protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
            return mapper.readValue(inputMessage.getBody(), mapper.constructType(clazz));
        }

        @Override
        public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage)
                throws IOException, org.springframework.http.converter.HttpMessageNotWritableException {
            writeInternal(o, outputMessage);
        }

        @Override
        protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException {
            mapper.writeValue(outputMessage.getBody(), o);
        }
    }
}
