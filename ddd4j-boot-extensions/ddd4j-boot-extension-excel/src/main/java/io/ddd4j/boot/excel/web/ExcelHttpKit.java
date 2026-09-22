package io.ddd4j.boot.excel.web;

import io.ddd4j.boot.excel.config.ExcelProperties;
import io.ddd4j.core.exception.BizRuntimeException;
import io.ddd4j.extension.excel.ExcelKit;
import io.ddd4j.extension.excel.importer.ImportResult;
import com.alibaba.excel.read.listener.ReadListener;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel Web 工具集（下载 / 上传）。
 *
 * <p>位于 boot 侧，依赖 Servlet + Spring Web；封装 {@link HttpServletResponse} 文件下载与
 * {@link MultipartFile} 文件上传的样板代码，并复用库侧 {@link ExcelKit}。
 *
 * <b>静态调用</b>
 * <pre>{@code
 * // 一行下载
 * ExcelHttpKit.download(response, "订单.xlsx", OrderVO.class, orderService.listAll());
 *
 * // 一行上传
 * ImportResult<OrderVO> result = ExcelHttpKit.upload(file, OrderVO.class);
 * }</pre>
 *
 * <b>Bean 注入（推荐，可与 ExcelProperties 联动）</b>
 * <pre>{@code
 * @Autowired
 * private ExcelHttpKit excelHttpKit;
 *
 * excelHttpKit.download(response, "订单.xlsx", bytes);
 * }</pre>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class ExcelHttpKit {

    private final ExcelProperties properties;

    /**
     * 默认构造（使用默认 {@link ExcelProperties}）。
     */
    public ExcelHttpKit() {
        this(new ExcelProperties());
    }

    /**
     * 注入配置构造（由 {@code Ddd4jExcelBootAutoConfiguration} 装配时使用）。
     *
     * @param properties Excel 配置（提供 maxUploadMB / charset 等参数）
     */
    public ExcelHttpKit(ExcelProperties properties) {
        this.properties = properties;
    }

    // ───────────────────── 下载 ─────────────────────

    /**
     * 下载 xlsx 字节数组到响应。
     *
     * @param response HTTP 响应
     * @param filename 文件名（含 .xlsx 扩展名）
     * @param bytes    xlsx 字节
     */
    public static void download(HttpServletResponse response, String filename, byte[] bytes) {
        download(response, ExcelAttachment.xlsx(filename), bytes);
    }

    /**
     * 下载自定义附件类型的字节数组。
     *
     * @param response  HTTP 响应
     * @param attachment 附件元数据
     * @param bytes      xlsx 字节
     */
    public static void download(HttpServletResponse response, ExcelAttachment attachment, byte[] bytes) {
        response.reset();
        response.setContentType(attachment.contentTypeWithCharset());
        response.setCharacterEncoding(attachment.getCharset().name());
        response.setHeader("Content-Disposition", attachment.contentDisposition());
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentLength(bytes.length);
        try (OutputStream out = response.getOutputStream()) {
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new BizRuntimeException(500, "excel.download.failed", e);
        }
    }

    /**
     * 下载 xlsx 字节并支持失败回 JSON。
     *
     * <p>调用方在外层 try-catch 包裹即可：失败时通过抛 {@link BizRuntimeException} 中断；
     * 本方法不会自行 reset 响应。如需"成功返回 Excel / 失败返回 JSON"的混合模式，
     * 由调用方在 catch 块中 {@code response.reset()} 后写 JSON。
     *
     * @param response HTTP 响应
     * @param filename 文件名
     * @param bytes    xlsx 字节
     * @param autoCloseStream 是否在写完后自动关闭流（默认 true）
     */
    public static void download(HttpServletResponse response, String filename, byte[] bytes,
                                boolean autoCloseStream) {
        response.reset();
        response.setContentType(ExcelAttachment.CONTENT_TYPE_XLSX);
        response.setCharacterEncoding("UTF-8");
        ExcelAttachment attachment = ExcelAttachment.xlsx(filename);
        response.setHeader("Content-Disposition", attachment.contentDisposition());
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentLength(bytes.length);
        try {
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            if (autoCloseStream) {
                out.close();
            }
        } catch (IOException e) {
            throw new BizRuntimeException(500, "excel.download.failed", e);
        }
    }

    /**
     * 一行下载：导出 → 字节 → Response（实例方法，便于注入 Bean 后调用）。
     *
     * @param response HTTP 响应
     * @param filename 文件名
     * @param head     表头类
     * @param data     数据
     */
    public void write(HttpServletResponse response, String filename,
                      Class<?> head, List<?> data) {
        byte[] bytes = ExcelKit.export(head, data);
        download(response, filename, bytes);
    }

    /**
     * 直接写字节到 Response（实例方法）。
     *
     * @param response HTTP 响应
     * @param filename 文件名
     * @param bytes    xlsx 字节
     */
    public void write(HttpServletResponse response, String filename, byte[] bytes) {
        download(response, filename, bytes);
    }

    // ───────────────────── 上传 ─────────────────────

    /**
     * 从上传文件中获取输入流（用于后续 {@link ExcelKit#importExcel}）。
     *
     * <p>调用方负责关闭返回的 InputStream（建议 try-with-resources）。
     *
     * @param file 上传文件
     * @return 输入流
     */
    public static InputStream upload(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new BizRuntimeException(400, "excel.upload.failed", e);
        }
    }

    /**
     * Web 上传：解析 MultipartFile 为导入结果（默认 ErrorCollectingReadListener）。
     *
     * @param file 上传文件
     * @param head 表头类
     * @param <T>  数据类型
     * @return 导入结果
     */
    public static <T> ImportResult<T> upload(MultipartFile file, Class<T> head) {
        try (InputStream in = file.getInputStream()) {
            return ExcelKit.importExcel(in, head);
        } catch (Exception e) {
            return io.ddd4j.extension.excel.importer.ImportResult.empty();
        }
    }

    /**
     * Web 上传：用自定义 listener 解析 MultipartFile。
     *
     * @param file     上传文件
     * @param head     表头类
     * @param listener 监听器
     * @param <T>      数据类型
     * @return 导入结果
     */
    public static <T> ImportResult<T> upload(MultipartFile file, Class<T> head, ReadListener<T> listener) {
        try (InputStream in = file.getInputStream()) {
            return ExcelKit.importExcel(in, head, listener);
        } catch (Exception e) {
            return io.ddd4j.extension.excel.importer.ImportResult.empty();
        }
    }

    /**
     * 实例方法上传（带 {@link ExcelProperties#getMaxUploadMB()} 大小校验）。
     *
     * @param file 上传文件
     * @param head 表头类
     * @param <T>  数据类型
     * @return 导入结果
     */
    public <T> ImportResult<T> read(MultipartFile file, Class<T> head) {
        validate(file);
        return upload(file, head);
    }

    /**
     * 校验上传文件大小与扩展名（使用 {@link ExcelProperties#getMaxUploadMB()} 配置上限）。
     *
     * @param file 上传文件
     */
    public void validate(MultipartFile file) {
        validate(file, properties.getMaxUploadMB(), List.of(".xlsx", ".xls"));
    }

    /**
     * 校验上传文件大小与扩展名。
     *
     * @param file       上传文件
     * @param maxMB      最大体积（MB）
     * @param extensions 允许的扩展名（如 {@code List.of(".xlsx", ".xls")}）
     * @throws BizRuntimeException 校验失败
     */
    public static void validate(MultipartFile file, int maxMB, List<String> extensions) {
        if (file == null || file.isEmpty()) {
            throw new BizRuntimeException(400, "excel.upload.empty");
        }
        long maxBytes = (long) maxMB * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BizRuntimeException(400, "excel.upload.too.large",
                    file.getSize(), maxBytes);
        }
        String original = file.getOriginalFilename();
        if (original == null) {
            throw new BizRuntimeException(400, "excel.upload.no.filename");
        }
        String lower = original.toLowerCase();
        boolean ok = extensions == null || extensions.isEmpty()
                || extensions.stream().anyMatch(lower::endsWith);
        if (!ok) {
            throw new BizRuntimeException(400, "excel.upload.invalid.extension", original);
        }
    }
}
