package io.ddd4j.boot.observability;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * W3C Trace Context {@code traceparent} 头编解码器（纯 Java、框架中立）。
 *
 * <p>格式：{@code {version}-{trace-id}-{parent-id}-{trace-flags}}，
 * 其中 trace-id 为 32 位小写十六进制（非全零），parent-id 为 16 位小写十六进制（非全零）。
 *
 * <p>对应 spec：boot-trace-observability / Trace context propagation；
 * 头名冻结于家族 design D9（{@code traceparent}）。
 *
 * <p>JDK 8 线（Spring Boot 2.3–2.7）最小集：Boot 2.x 生态无 micrometer-tracing/OTel 同代栈，
 * 本类为纯 Java 退化实现——不含 OTel SpanContext 转换与当前 span 读取
 * （完整版见 3.0.x+ / 4.0.x+ 分支），编解码/生成契约与完整版一致。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class TraceparentCodec {

    /**
     * trace-id 十六进制长度（W3C 标准：32 字符）。
     */
    private static final int TRACE_ID_LENGTH = 32;

    /**
     * parent-id 十六进制长度（W3C 标准：16 字符）。
     */
    private static final int SPAN_ID_LENGTH = 16;

    /**
     * trace-flags 长度（W3C 标准：2 字符）。
     */
    private static final int FLAGS_LENGTH = 2;

    /**
     * 随机标识发生器（trace/span id 生成）。
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    private TraceparentCodec() {
    }

    /**
     * 判断 traceparent 头值是否合法。
     *
     * @param traceparent 头值（允许 null/空串）
     * @return true 表示格式合法且 id 非全零、版本非 ff
     */
    public static boolean isValid(final String traceparent) {
        if (Objects.isNull(traceparent)) {
            return false;
        }
        String[] parts = traceparent.split("-");
        if (parts.length != 4) {
            return false;
        }
        // 版本为 2 位十六进制且不允许 ff（W3C 规定 ff 为保留值）
        if (parts[0].length() != FLAGS_LENGTH || !isHex(parts[0]) || "ff".equalsIgnoreCase(parts[0])) {
            return false;
        }
        if (parts[1].length() != TRACE_ID_LENGTH || !isHex(parts[1]) || isAllZero(parts[1])) {
            return false;
        }
        if (parts[2].length() != SPAN_ID_LENGTH || !isHex(parts[2]) || isAllZero(parts[2])) {
            return false;
        }
        return parts[3].length() == FLAGS_LENGTH && isHex(parts[3]);
    }

    /**
     * 从合法 traceparent 中提取 trace 标识。
     *
     * @param traceparent 头值
     * @return 32 位 trace 标识；非法输入返回 null
     */
    public static String extractTraceId(final String traceparent) {
        if (!isValid(traceparent)) {
            return null;
        }
        return traceparent.split("-")[1].toLowerCase();
    }

    /**
     * 生成全新的合法 traceparent（版本 00，flags=01 采样）。
     *
     * @return 全新 traceparent 头值
     */
    public static String generateTraceparent() {
        return "00-" + generateTraceId() + "-" + generateSpanId() + "-01";
    }

    /**
     * 以指定 trace 标识生成 traceparent（新 span id，flags=01 采样）。
     *
     * @param traceId 32 位 trace 标识
     * @return traceparent 头值
     */
    public static String generateTraceparent(final String traceId) {
        return "00-" + traceId + "-" + generateSpanId() + "-01";
    }

    /**
     * 生成 32 位随机 trace 标识。
     *
     * @return 16 字节随机数的小写十六进制串
     */
    public static String generateTraceId() {
        return toHex(16);
    }

    /**
     * 生成 16 位随机 span 标识。
     *
     * @return 8 字节随机数的小写十六进制串
     */
    public static String generateSpanId() {
        return toHex(8);
    }

    /**
     * 生成指定字节数的小写十六进制随机串。
     *
     * @param bytes 字节数
     * @return 十六进制串
     */
    private static String toHex(final int bytes) {
        byte[] buffer = new byte[bytes];
        RANDOM.nextBytes(buffer);
        StringBuilder builder = new StringBuilder(bytes * 2);
        for (byte b : buffer) {
            builder.append(Character.forDigit((b >> 4) & 0xF, 16));
            builder.append(Character.forDigit(b & 0xF, 16));
        }
        return builder.toString();
    }

    /**
     * 判断字符串是否全部为十六进制字符。
     *
     * @param value 待判字符串
     * @return true 表示全部为十六进制字符
     */
    private static boolean isHex(final String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.digit(value.charAt(i), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断十六进制串是否全零。
     *
     * @param value 待判字符串
     * @return true 表示全零
     */
    private static boolean isAllZero(final String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }
}
