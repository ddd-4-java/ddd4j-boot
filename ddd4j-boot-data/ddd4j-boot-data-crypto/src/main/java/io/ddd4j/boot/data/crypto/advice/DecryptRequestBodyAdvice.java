package io.ddd4j.boot.data.crypto.advice;

import cn.hutool.crypto.symmetric.AES;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ddd4j.core.ApiCode;
import io.ddd4j.core.dto.BaseDTO;
import io.ddd4j.core.dto.RequestData;
import io.ddd4j.core.exception.ParamException;
import io.ddd4j.data.crypto.CryptoConstant;
import io.ddd4j.data.crypto.annotation.RequestDecryption;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * requestBody 自动解密。
 *
 * <p>从 {@code ddd4j-data-crypto} 迁入 boot 层（依赖 Spring WebMVC）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@ControllerAdvice
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AES aes;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(RequestDecryption.class);
    }

    @SneakyThrows
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (servletRequestAttributes == null) {
            throw new ParamException(ApiCode.SC_FAIL, "request错误");
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();

        ServletInputStream inputStream = request.getInputStream();
        RequestData requestData = objectMapper.readValue(inputStream, RequestData.class);

        if (requestData == null || StringUtils.isBlank(requestData.getText())) {
            throw new ParamException(ApiCode.SC_FAIL, "参数错误");
        }

        String text = requestData.getText();

        request.setAttribute(CryptoConstant.INPUT_ORIGINAL_DATA, text);

        String decryptText = null;
        try {
            decryptText = aes.decryptStr(text);
        } catch (Exception e) {
            throw new ParamException(ApiCode.SC_FAIL, "解密失败");
        }

        if (StringUtils.isBlank(decryptText)) {
            throw new ParamException(ApiCode.SC_FAIL, "解密失败");
        }

        request.setAttribute(CryptoConstant.INPUT_DECRYPT_DATA, decryptText);

        Object result = objectMapper.readValue(decryptText, body.getClass());

        if (result instanceof BaseDTO) {
            Long currentTimeMillis = ((BaseDTO) result).getCurrentTimeMillis();
            long effective = 60 * 1000;

            long expire = System.currentTimeMillis() - currentTimeMillis;

            if (Math.abs(expire) > effective) {
                throw new ParamException(ApiCode.SC_FAIL, "时间戳不合法");
            }

            return result;
        } else {
            throw new ParamException(ApiCode.SC_FAIL, String.format("请求参数类型：%s 未继承：%s", result.getClass().getName(), BaseDTO.class.getName()));
        }
    }

    @SneakyThrows
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String typeName = targetType.getTypeName();
        Class<?> bodyClass = Class.forName(typeName);
        return bodyClass.newInstance();
    }

}
