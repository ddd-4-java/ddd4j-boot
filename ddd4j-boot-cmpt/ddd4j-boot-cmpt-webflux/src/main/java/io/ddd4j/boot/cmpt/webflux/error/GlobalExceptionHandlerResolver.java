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


    /**
     * 处理已知异常
     */
    @ExceptionHandler({CheckedException.class})
    @ResponseStatus(HttpStatus.OK)
    public R checkedException(CheckedException e) {
        log.error("自定义异常,i18n:{}, ex={}", i18n, e.toString(), e);
        if (i18n) {
            return R.failed(e.getCode() == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : e.getCode(), I18nUtil.getMessageByArgs(e.getMsgCode(), e.getArgs(), e.getMessage()));
        }
        return R.failed(e.getCode() == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : e.getCode(), e.getMessage());
    }


    /**
     * AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R handleAccessDeniedException(AccessDeniedException e) {
        String msg = SpringSecurityMessageSource.getAccessor().getMessage("AbstractAccessDecisionManager.accessDenied", e.getMessage());
        log.error("拒绝授权异常信息 ex={}", msg, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("sys.error.auth", "非法请求！"));
        }
        return R.failed(HttpStatus.FORBIDDEN.value(), "没有权限，请找管理员配置权限！");
    }


    /**
     * MethodArgumentNotValidException
     *
     * @NotNull @NotBlank 注解参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleAccessDeniedException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        log.error("参数校验:{}", message);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("bad.request", message));
            //if (printErrorDetail) {
            //	return R.failed(I18nUtil.getMessage("bad.request", message));
            //} else {
            //	return R.failed(I18nUtil.getMessage("bad.request", "错误请求参数！"));
            //}
        }
        return R.failed(HttpStatus.BAD_REQUEST.value(), message);
        //if (printErrorDetail) {
        //	return R.failed(HttpStatus.BAD_REQUEST.value(), message);
        //}
        //return R.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<String> methodNoSupported(HttpServletRequest req, HttpRequestMethodNotSupportedException e) {
        log.error("method no supported ", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("method.not.allowed", "错误的请求方法！"));
        }
        String msg = e.getMethod() + "不被允许的请求方法！请使用：" + e.getSupportedHttpMethods();
        return new R<>(HttpStatus.METHOD_NOT_ALLOWED.value(), msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> constraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        log.error("", e);
        rpcThrowException(req, e);
        String msg = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString() + ":" + violation.getMessage() + "；")
                .collect(Collectors.joining());
        if (i18n) {
            if (printErrorDetail) {
                return R.failed(I18nUtil.getMessage("bad.request", msg));
            }
            return R.failed(I18nUtil.getMessage("bad.request", "错误请求参数！"));
        }
        if (printErrorDetail) {
            return R.failed(HttpStatus.BAD_REQUEST.value(), msg);
        }
        return new R<>(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> argumentNotValid(HttpServletRequest req, BindException e) {
        log.error("", e);
        rpcThrowException(req, e);
        StringBuilder msg = new StringBuilder();
        BindingResult result = e.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError error : fieldErrors) {
            msg.append(error.getDefaultMessage()).append("；");
        }
        if (i18n) {
            if (printErrorDetail) {
                return R.failed(I18nUtil.getMessage("bad.request", msg.toString()));
            }
            return R.failed(I18nUtil.getMessage("bad.request", "错误请求参数！"));
        }
        return new R<>(HttpStatus.BAD_REQUEST.value(), msg.toString());
    }


    @ExceptionHandler(value = {MultipartException.class, RequestTooBigException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> multipartException(HttpServletRequest req, Exception e) {
        log.error("文件上传大小限制：", e);
        if (i18n) {
            return R.failed(I18nUtil.getMessageByArgs("max.file", new Object[]{fileSize}, "文件上传大小超过限制！"));
        }
        return new R<>(HttpStatus.BAD_REQUEST.value(), "文件上传大小不可大于：" + fileSize);
    }


    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> argumentTypeMismatch(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("bad.request", "错误请求参数！"));
        }
        String msg = e.getName() + "必须是：" + e.getRequiredType() + "类型！";
        return R.failed(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> mediaTypeNotSupported(HttpServletRequest req, HttpMediaTypeNotSupportedException e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("bad.request", "不支持的Content-Type类型！"));
        }
        String msg = e.getContentType() + "不支持！Content-Type必须是：" + e.getSupportedMediaTypes() + "类型！";
        return R.failed(HttpStatus.BAD_REQUEST.value(), msg);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<String> handle(HttpServletRequest request, NoHandlerFoundException e) {
        log.error("", e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("not.found", "请求地址不存在！"));
        }
        return new R<>(404, "地址错误！！！" + request.getRequestURI() + "非法访问!");
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<String> violation(HttpServletRequest req, SQLIntegrityConstraintViolationException e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("sys.sql.error", "SQL错误！"));
        }
        if (printErrorDetail) {
            return R.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL错误！");
        } else {
            return new R<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL错误！", e.getMessage());
        }
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> duplicate(HttpServletRequest req, DuplicateKeyException e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("sys.duplicate.key", "重复提交数据！"));
        }
        if (printErrorDetail) {
            return R.failed(HttpStatus.BAD_REQUEST.value(), "重复提交数据！");
        } else {
            return new R<>(HttpStatus.BAD_REQUEST.value(), "重复提交数据！", e.getMessage());
        }
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> requestParameter(HttpServletRequest req, MissingServletRequestParameterException e) {
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("bad.request.param", "参数异常！"));
        }
        return R.failed(HttpStatus.BAD_REQUEST.value(), "参数异常：" + e.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> notReadable(HttpServletRequest req, HttpMessageNotReadableException e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("bad.request.param", "请求格式有误！"));
        }
        if (printErrorDetail) {
            return R.failed(HttpStatus.BAD_REQUEST.value(), "请求格式有误！" + e.getMessage());
        }
        return R.failed(HttpStatus.BAD_REQUEST.value(), "请求格式有误！");
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> sqlError(HttpServletRequest req, HttpMessageNotReadableException e) {
        log.error("", e);
        rpcThrowException(req, e);
        if (i18n) {
            return R.failed(I18nUtil.getMessage("sys.sql.error", "系统错误！"));
        }
        if (printErrorDetail) {
            return R.failed(HttpStatus.BAD_REQUEST.value(), "请求格式有误！" + e.getMessage());
        }
        return R.failed(HttpStatus.BAD_REQUEST.value(), "请求格式有误！");
    }

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