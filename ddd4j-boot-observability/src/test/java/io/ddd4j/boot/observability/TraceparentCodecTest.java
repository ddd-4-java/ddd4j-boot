package io.ddd4j.boot.observability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TraceparentCodec} 单元测试：W3C traceparent 编解码契约。
 *
 * <p>对应 spec：boot-trace-observability / Trace context propagation。
 */
class TraceparentCodecTest {

    /** W3C 规范示例值（RFC 4122 UUID 大小写混合不允许，规范示例为小写十六进制） */
    private static final String VALID = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";

    @Test
    @DisplayName("合法 traceparent 应被识别为有效")
    void validTraceparentShouldBeAccepted() {
        assertThat(TraceparentCodec.isValid(VALID)).isTrue();
    }

    @Test
    @DisplayName("非法 traceparent 应被拒绝（空/截断/全零 id/错误版本）")
    void invalidTraceparentShouldBeRejected() {
        assertThat(TraceparentCodec.isValid(null)).isFalse();
        assertThat(TraceparentCodec.isValid("")).isFalse();
        assertThat(TraceparentCodec.isValid("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7")).isFalse();
        assertThat(TraceparentCodec.isValid("00-00000000000000000000000000000000-00f067aa0ba902b7-01")).isFalse();
        assertThat(TraceparentCodec.isValid("00-4bf92f3577b34da6a3ce929d0e0e4736-0000000000000000-01")).isFalse();
        assertThat(TraceparentCodec.isValid("ff-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01")).isFalse();
        assertThat(TraceparentCodec.isValid("00-ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ-00f067aa0ba902b7-01")).isFalse();
    }

    @Test
    @DisplayName("应能从合法 traceparent 中提取 traceId")
    void shouldExtractTraceId() {
        assertThat(TraceparentCodec.extractTraceId(VALID)).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
    }

    @Test
    @DisplayName("新生成的 traceparent 必须合法且 traceId 全局唯一形态正确")
    void generatedTraceparentShouldBeValid() {
        String first = TraceparentCodec.generateTraceparent();
        String second = TraceparentCodec.generateTraceparent();
        assertThat(TraceparentCodec.isValid(first)).isTrue();
        assertThat(TraceparentCodec.isValid(second)).isTrue();
        assertThat(TraceparentCodec.extractTraceId(first)).isNotEqualTo(TraceparentCodec.extractTraceId(second));
    }
}
