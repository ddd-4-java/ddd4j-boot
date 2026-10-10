package io.ddd4j.boot.qrcode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 二维码端点的稳定 HTTP 错误映射（仅作用于 {@link QrCodeController}）。
 *
 * <p>将参数非法、IO 失败与内部状态异常映射为固定的错误码响应体。
 */
@RestControllerAdvice(assignableTypes = QrCodeController.class)
public class QrCodeExceptionHandler {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本异常处理类。
     */
    public QrCodeExceptionHandler() {
    }

    /**
     * 参数非法 → 400，错误码 {@code QRCODE_INVALID_ARGUMENT}。
     *
     * @param exception 捕获的参数非法异常
     * @return 400 状态的错误响应体
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("QRCODE_INVALID_ARGUMENT", exception.getMessage()));
    }

    /**
     * IO 失败 → 400，错误码 {@code QRCODE_IO_ERROR}。
     *
     * @param exception 捕获的 IO 异常
     * @return 400 状态的错误响应体
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIo(IOException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("QRCODE_IO_ERROR", exception.getMessage()));
    }

    /**
     * 内部状态异常 → 500，错误码 {@code QRCODE_INTERNAL_ERROR}。
     *
     * @param exception 捕获的内部状态异常
     * @return 500 状态的错误响应体
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleInternal(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("QRCODE_INTERNAL_ERROR", exception.getMessage()));
    }

    /**
     * 统一错误响应体（错误码 + 错误信息）。
     */
    public static class ErrorResponse {

        /** 机器可读的稳定错误码。 */
        private final String code;
        /** 人类可读的错误信息。 */
        private final String message;

        /**
         * 以错误码与错误信息构造响应体。
         *
         * @param code    稳定错误码
         * @param message 错误信息
         */
        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        /** 获取稳定错误码。
         * @return 错误码 */
        public String getCode() {
            return code;
        }

        /** 获取错误信息。
         * @return 错误信息 */
        public String getMessage() {
            return message;
        }
    }
}
