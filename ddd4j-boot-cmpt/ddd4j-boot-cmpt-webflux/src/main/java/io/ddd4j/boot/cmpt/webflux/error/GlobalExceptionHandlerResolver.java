package io.ddd4j.boot.cmpt.webflux.error;

import io.ddd4j.cloud.cmpt.core.util.R;
import io.ddd4j.cloud.cmpt.i18n.util.I18nUtil;
import io.undertow.server.RequestTooBigException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static io.ddd4j.cloud.cmpt.core.constant.SecurityConstants.FROM;
import static io.ddd4j.cloud.cmpt.core.constant.SecurityConstants.FROM_IN;

/**
 * 全局异常处理器
 *
 */
@Slf4j
@ConditionalOnWebApplication(type = Type.SERVLET)
@RestControllerAdvice
@RefreshScope
public class GlobalExceptionHandlerResolver {

    /**
     * 根据环境是否给客户端抛出未知具体异常信息
     */
    @Value("${print.error.detail:false}")
    private boolean printErrorDetail;

    @Value("${cloud.i18n.enable:false}")
    private boolean i18n;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String fileSize;







    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public R<String> otherExceptionHandler(HttpServletRequest req, Exception e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("sys.error", "系统错误！"));
        }
        R<String> r = new R<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统错误！");
        if (e instanceof IllegalArgumentException) {
            r.setMsg(e.getMessage());
        } else {
            if (printErrorDetail) {
                if (e instanceof NullPointerException) {
                    r.setMsg("空指针异常！");
                } else {
                    r.setMsg(e.getMessage());
                }
            } else {
                r.setMsg("系统错误！");
            }
        }
        return r;

    }


    private void rpcThrowException(HttpServletRequest req, Exception e) {
        String rpc = req.getHeader(FROM);
        if (FROM_IN.equalsIgnoreCase(rpc)) {
            // throw new CheckedException(e);
            log.error("内部服务调用异常！", e);
        }
    }
}