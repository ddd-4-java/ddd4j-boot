package io.ddd4j.boot.observability;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * 追踪开销基线数据落盘工具（对应家族 design D7 / spec Bounded overhead）。
 *
 * <p>基准测试调用 {@link #write} 把「追踪关闭 vs 开启」的单次开销写入
 * {@code target/observability-baseline/<name>.json}，随构建证据留存；
 * 入仓副本由人工从执行输出拷贝至 {@code docs/observability/}（仅记录基线数据，不作硬门禁）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class TraceOverheadBaseline {

    private TraceOverheadBaseline() {
    }

    /**
     * 写入一组基准数据。
     *
     * @param name       基线名称（文件名成分）
     * @param iterations 迭代次数
     * @param offMicros  关闭追踪时单次平均开销（µs）
     * @param onMicros   开启追踪时单次平均开销（µs）
     * @return 落盘文件路径
     * @throws IOException 写入失败
     */
    public static Path write(final String name, final int iterations,
                             final double offMicros, final double onMicros) throws IOException {
        Objects.requireNonNull(name, "name must not be null");
        Path directory = Paths.get("target", "observability-baseline");
        Files.createDirectories(directory);
        Path file = directory.resolve(name + ".json");
        String json = "{\n"
                + "  \"name\": \"" + name + "\",\n"
                + "  \"timestamp\": \"" + OffsetDateTime.now() + "\",\n"
                + "  \"javaVersion\": \"" + System.getProperty("java.version") + "\",\n"
                + "  \"iterations\": " + iterations + ",\n"
                + String.format("  \"tracingOffAvgMicros\": %.3f,%n", offMicros)
                + String.format("  \"tracingOnAvgMicros\": %.3f,%n", onMicros)
                + String.format("  \"overheadAvgMicros\": %.3f,%n", (onMicros - offMicros))
                + String.format("  \"overheadRatio\": %.2f%n", (offMicros <= 0.0D ? 0.0D : onMicros / offMicros))
                + "}";
        Files.writeString(file, json, StandardCharsets.UTF_8);
        return file;
    }
}
