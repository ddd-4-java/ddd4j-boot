package io.ddd4j.boot.cola.exception;

import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.exception.SysException;
import io.ddd4j.core.api.ApiCode;
import io.ddd4j.core.api.R;
import io.ddd4j.web.webmvc.exception.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * COLA 异常处理器。
 *
 * <p>将 COLA 的 {@link BizException}（业务异常）和 {@link SysException}（系统异常）
 * 转换为统一的 {@link R}。
 *
 * <p>异常映射规则：
 * <ul>
 *   <li>{@link BizException} → {@code ApiCode.FAIL}（业务失败，HTTP 200，响应码表示业务失败）</li>
 *   <li>{@link SysException} → {@code ApiCode.SERVER_ERROR}（500 系统错误）</li>
 *   <li>其他 COLA 异常 → {@code ApiCode.SERVER_ERROR}</li>
 * </ul>
 *
 * @author wandl
 * @since 3.4.x
 */
@RestControllerAdvice
@Order(0)
@Slf4j
public class ColaExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理 COLA 业务异常。
     *
     * @param ex COLA 业务异常
     * @return 统一响应（code/msg/data）
     */
    @ExceptionHandler(BizException.class)
    public R<String> handleBizException(BizException ex) {
        log.warn("COLA 业务异常: errCode={}, message={}", ex.getErrCode(), ex.getMessage());
        // 使用统一业务失败码；COLA errCode 记录在日志中。
        String message = ex.getMessage() != null ? ex.getMessage() : "COLA 业务异常";
        return R.fail(message);
    }

    /**
     * 处理 COLA 系统异常。
     *
     * @param ex COLA 系统异常
     * @return 统一响应（500）
     */
    @ExceptionHandler(SysException.class)
    public R<String> handleSysException(SysException ex) {
        log.error("COLA 系统异常: errCode={}, message={}", ex.getErrCode(), ex.getMessage(), ex);
        return ApiCode.SERVER_ERROR.toResponse(
                ex.getMessage() != null ? "COLA: " + ex.getMessage() : "COLA: 系统异常");
    }

}
