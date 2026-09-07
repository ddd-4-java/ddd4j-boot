package io.ddd4j.boot.excel.web;

import com.alibaba.excel.annotation.ExcelProperty;
import io.ddd4j.boot.excel.config.ExcelProperties;
import io.ddd4j.core.exception.BizRuntimeException;
import io.ddd4j.extension.excel.ExcelKit;
import io.ddd4j.extension.excel.importer.ImportResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcelHttpKitTest {

    // ── download ──────────────────────────────────────────────

    @Test
    void download_shouldSetCorrectHeadersAndBody() {
        byte[] payload = new byte[]{1, 2, 3, 4, 5};
        MockHttpServletResponse response = new MockHttpServletResponse();

        ExcelHttpKit.download(response, "订单.xlsx", payload);

        assertThat(response.getContentType())
                .startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(response.getHeader("Content-Disposition"))
                .isEqualTo("attachment;filename*=utf-8''%E8%AE%A2%E5%8D%95.xlsx");
        assertThat(response.getHeader("Access-Control-Expose-Headers"))
                .isEqualTo("Content-Disposition");
        assertThat(response.getContentAsByteArray()).isEqualTo(payload);
    }

    // ── write round-trip ─────────────────────────────────────

    @Test
    void write_roundTrip_shouldPreserveData() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<SimpleUser> data = Arrays.asList(
                new SimpleUser(1L, "Alice"),
                new SimpleUser(2L, "Bob"));

        ExcelHttpKit kit = new ExcelHttpKit();
        kit.write(response, "users.xlsx", SimpleUser.class, data);

        byte[] written = response.getContentAsByteArray();
        assertThat(written).isNotEmpty();

        // Round-trip: read back with ExcelKit
        ImportResult<SimpleUser> result = ExcelKit.importExcel(
                new ByteArrayInputStream(written), SimpleUser.class);
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getId()).isEqualTo(1L);
        assertThat(result.getData().get(0).getName()).isEqualTo("Alice");
        assertThat(result.getData().get(1).getId()).isEqualTo(2L);
        assertThat(result.getData().get(1).getName()).isEqualTo("Bob");
    }

    // ── upload happy path ─────────────────────────────────────

    @Test
    void upload_validFile_shouldReturnRows() {
        // First export some data
        byte[] bytes = ExcelKit.export(SimpleUser.class,
                Arrays.asList(new SimpleUser(10L, "Charlie")));
        MockMultipartFile file = new MockMultipartFile(
                "file", "data.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);

        ImportResult<SimpleUser> result = ExcelHttpKit.upload(file, SimpleUser.class);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getName()).isEqualTo("Charlie");
    }

    // ── validate: too large ───────────────────────────────────

    @Test
    void validate_fileTooLarge_shouldThrowBizException() {
        byte[] bytes = new byte[200];
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);

        // maxMB = 0 means maxBytes = 0, any non-empty file exceeds it
        assertThatThrownBy(() -> ExcelHttpKit.validate(file, 0, Arrays.asList(".xlsx")))
                .isInstanceOf(BizRuntimeException.class)
                .satisfies(ex -> {
                    BizRuntimeException biz = (BizRuntimeException) ex;
                    assertThat(biz.getCode()).isEqualTo(400);
                    assertThat(biz.getMessage()).contains("excel.upload.too.large");
                });
    }

    // ── validate: invalid extension ───────────────────────────

    @Test
    void validate_invalidExtension_shouldThrowBizException() {
        byte[] bytes = new byte[]{1};
        MockMultipartFile file = new MockMultipartFile(
                "file", "data.csv",
                "text/csv", bytes);

        assertThatThrownBy(() -> ExcelHttpKit.validate(file, 50, Arrays.asList(".xlsx", ".xls")))
                .isInstanceOf(BizRuntimeException.class)
                .satisfies(ex -> {
                    BizRuntimeException biz = (BizRuntimeException) ex;
                    assertThat(biz.getCode()).isEqualTo(400);
                    assertThat(biz.getMessage()).contains("excel.upload.invalid.extension");
                });
    }

    // ── upload garbage bytes ──────────────────────────────────

    @Test
    void upload_garbageBytes_shouldReturnEmptyResult() {
        // Valid .xlsx extension but garbage content
        byte[] garbage = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        MockMultipartFile file = new MockMultipartFile(
                "file", "garbage.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                garbage);

        // ExcelHttpKit.upload catches all exceptions and returns ImportResult.empty()
        ImportResult<SimpleUser> result = ExcelHttpKit.upload(file, SimpleUser.class);
        assertThat(result.getData()).isEmpty();
        assertThat(result.getErrors()).isEmpty();
    }

    // ── test DTO ─────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleUser {
        @ExcelProperty("ID")
        private Long id;

        @ExcelProperty("Name")
        private String name;
    }
}
