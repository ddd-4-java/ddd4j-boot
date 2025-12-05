package com.github.hiwepy.ddd4j.crypto.strategy;

import cn.hutool.crypto.digest.HmacAlgorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hiwepy.boot.api.ApiCode;
import io.hiwepy.boot.api.exception.BizRuntimeException;
import com.github.hiwepy.ddd4j.crypto.domain.enums.CryptoType;
import com.github.hiwepy.ddd4j.crypto.domain.enums.SymmetricAlgorithmType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求加解密内部服务实现
 */
@Slf4j
public class NoOpCryptoStrategy implements CryptoStrategy {

    @Getter
    private ObjectMapper objectMapper;

    public NoOpCryptoStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CryptoType getType() {
        return CryptoType.NOOP;
    }

    @Override
    public <T> String encrypt(T value, SymmetricAlgorithmType algorithmType, String encMode, String padMode, String key, String iv, boolean plainIsEncode) {
        try {
            return getObjectMapper().writeValueAsString(value);
        } catch (Exception ex) {
            log.error("Json Processing Error : {}", ex.getMessage());
            throw new BizRuntimeException(ApiCode.SC_INTERNAL_SERVER_ERROR, "Json Processing Error");
        }
    }

    @Override
    public <T> T decrypt(String value, SymmetricAlgorithmType algorithmType, String encMode, String padMode, String key, String iv, boolean plainIsEncode, Class<T> rtType) {
        try {
            return getObjectMapper().readValue(value, rtType);
        } catch (Exception ex) {
            log.error("Json Processing Error : {}", ex.getMessage());
            throw new BizRuntimeException(ApiCode.SC_INTERNAL_SERVER_ERROR, "Json Processing Error");
        }
    }

    @Override
    public <T> String hmac(T value, HmacAlgorithm hmacAlgorithm, String key, String iv, boolean plainIsEncode) {
        return "";
    }


}
