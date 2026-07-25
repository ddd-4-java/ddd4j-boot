package io.ddd4j.boot.qrcode;

import com.google.zxing.exception.QrCodeErrorCode;
import com.google.zxing.exception.QrCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/** Stable HTTP error mapping for the opt-in QR code endpoints. */
@RestControllerAdvice(assignableTypes = QrCodeController.class)
public class QrCodeExceptionHandler {

    @ExceptionHandler(QrCodeException.class)
    public ResponseEntity<ErrorResponse> handleQrCode(QrCodeException exception) {
        return ResponseEntity.status(status(exception.getErrorCode()))
                .body(new ErrorResponse(exception.getErrorCode().name(), exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(QrCodeErrorCode.QRCODE_INVALID_ARGUMENT.name(), exception.getMessage()));
    }

    private HttpStatus status(QrCodeErrorCode errorCode) {
        if (errorCode == QrCodeErrorCode.QRCODE_IMAGE_TOO_LARGE) {
            return HttpStatus.PAYLOAD_TOO_LARGE;
        }
        if (errorCode == QrCodeErrorCode.QRCODE_UNSUPPORTED_FORMAT) {
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        }
        if (errorCode == QrCodeErrorCode.QRCODE_DECODE_NOT_FOUND
                || errorCode == QrCodeErrorCode.QRCODE_CAPACITY_EXCEEDED
                || errorCode == QrCodeErrorCode.QRCODE_SELF_CHECK_FAILED) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
        if (errorCode == QrCodeErrorCode.QRCODE_INVALID_ARGUMENT) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static class ErrorResponse {

        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
