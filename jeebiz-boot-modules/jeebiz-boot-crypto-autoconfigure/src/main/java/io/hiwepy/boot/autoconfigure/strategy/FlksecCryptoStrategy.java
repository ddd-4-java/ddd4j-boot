package io.hiwepy.boot.autoconfigure.strategy;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hiwepy.boot.api.exception.BizRuntimeException;
import io.hiwepy.boot.autoconfigure.crypto.enums.CryptoType;
import io.hiwepy.boot.autoconfigure.crypto.enums.SymmetricAlgorithmType;
import io.hiwepy.boot.autoconfigure.crypto.vo.FlkSecDecryptResponseVO;
import io.hiwepy.boot.autoconfigure.crypto.vo.FlkSecEncryptResponseVO;
import io.hiwepy.boot.autoconfigure.crypto.vo.FlkSecSignResponseVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求加解密服务实现
 */
@Slf4j
public class FlksecCryptoStrategy implements CryptoStrategy {

    @Getter
    private ObjectMapper objectMapper;
    @Getter
    private RestClient restClient;
    private String address;
    private String port;

    public FlksecCryptoStrategy(RestClient restClient, String address, String port) {
        this.restClient = restClient;
        this.address = address;
        this.port = port;
    }

    @Override
    public CryptoType getType() {
        return CryptoType.FLKSEC;
    }

    @Override
    public <T> String encrypt(T value, SymmetricAlgorithmType algorithmType, String encMode, String padMode, String key, String iv, boolean plainIsEncode) {
        try {
            // 1、序列化Value
            String valueAsString = getObjectMapper().writeValueAsString(value);
            // 2、如果 plainIsEncode =true 则对 valueAsString 进行 Base64 编码
            if(plainIsEncode){
                valueAsString = Base64.encode(valueAsString);
                log.debug("Base64 Encode String to Encrypt : {}", value);
            }
            Map<String, String> bodyContent = new HashMap<>();
            // 加密的算法类型,目前系统支持 sm1,sm4
            bodyContent.put("algorithmType", "sm4");
            // 采用的加密模式，系统支持 ecb,cbc,cfb,ofb
            bodyContent.put("encMode", encMode);
            // 解密运算所采用的填充模式，系统支持 PKCS5Padding 和 NoPadding
            bodyContent.put("padMode", padMode);
            // Base64 格式的密钥字符串
            bodyContent.put("key", key);
            // Base64 格式的初始向量， 加密模式为 cbc,cfb,ofb 时该参数不能为空，解码后长度为 16 位，可自定义
            bodyContent.put("iv", iv);
            // 需要进行加密的数据
            bodyContent.put("data", valueAsString);
            // 明文是否编码
            bodyContent.put("plainIsEncode", String.valueOf(plainIsEncode));
            // 远程请求地址
            String url = String.format("https://%s:%s/api/crypto/sysEncrypt", address, port);
            FlkSecEncryptResponseVO encryptResponse =  restClient.post().uri(url).body(bodyContent).retrieve().body(FlkSecEncryptResponseVO.class);
            if (encryptResponse.getCode() == 200) {
                String responseString = StringUtils.defaultString(encryptResponse.getData());
                log.debug("Response Encrypt Value : {}", responseString);
                return responseString;
            } else {
                throw new BizRuntimeException(encryptResponse.getMsg());
            }
        } catch (IOException e) {
            log.error("调用远程接口加密失败：{}", e.getMessage());
            throw new BizRuntimeException("调用远程接口加密失败，请稍后重试");
        }
    }

    @Override
    public <T> T decrypt(String value, SymmetricAlgorithmType algorithmType, String encMode, String padMode, String key, String iv, boolean plainIsEncode, Class<T> rtType) {
        try {
            Map<String, String> bodyContent = new HashMap<>();
            // 加密的算法类型,目前系统支持 sm1,sm4
            bodyContent.put("algorithmType", "sm4");
            // 采用的加密模式，系统支持 ecb,cbc,cfb,ofb
            bodyContent.put("encMode", encMode);
            // 解密运算所采用的填充模式，系统支持 PKCS5Padding 和 NoPadding
            bodyContent.put("padMode", padMode);
            // Base64 格式的密钥字符串
            bodyContent.put("key", key);
            // Base64 格式的初始向量， 加密模式为 cbc,cfb,ofb 时该参数不能为空，解码后长度为 16 位，可自定义
            bodyContent.put("iv", iv);
            // 需要进行解密的数据
            bodyContent.put("data", value);
            // 明文是否编码
            bodyContent.put("plainIsEncode", String.valueOf(plainIsEncode));
            // 远程请求地址
            String url = String.format("https://%s:%s/api/crypto/sysDecrypt", address, port);
            FlkSecDecryptResponseVO decryptResponse =  restClient.post().uri(url).body(bodyContent).retrieve().body(FlkSecDecryptResponseVO.class);
            if (decryptResponse.getCode() == 200) {
                String responseString = StringUtils.defaultString(decryptResponse.getData());
                log.debug("Response Decrypt Value : {}", responseString);
                return getObjectMapper().readValue(value, rtType);
            } else {
                throw new BizRuntimeException(decryptResponse.getMsg());
            }
        } catch (IOException e) {
            log.error("调用远程接口解密失败：{}", e.getMessage());
            throw new BizRuntimeException("调用远程接口解密失败，请稍后重试");
        }
    }

    @Override
    public <T> String hmac(T value, HmacAlgorithm hmacAlgorithm, String key, String iv, boolean plainIsEncode) {
        try {
            // 1、序列化Value
            String valueAsString = getObjectMapper().writeValueAsString(value);
            // 2、如果 plainIsEncode =true 则对 valueAsString 进行 Base64 编码
            if(plainIsEncode){
                valueAsString = Base64.encode(valueAsString);
                log.debug("Base64 Encode String to Hmac : {}", value);
            }
            Map<String, String> bodyContent = new HashMap<>();
            // 明文是否编码
            bodyContent.put("plainIsEncode", String.valueOf(plainIsEncode));
            // Base64 格式的密钥字符串
            bodyContent.put("key", key);
            // 进行杂凑的数据，数据大小建议不要超过 100M，比较大的数据可以每 100M分块计算，最后进行比较
            bodyContent.put("data", valueAsString);
            // 远程请求地址
            String url = String.format("https://%s:%s/api/hmac/sm3hmac", address, port);
            FlkSecSignResponseVO signResponse =  restClient.post().uri(url).body(bodyContent).retrieve().body(FlkSecSignResponseVO.class);
            if (signResponse.getCode() == 200) {
                return StringUtils.defaultString(signResponse.getData());
            } else {
                throw new BizRuntimeException(signResponse.getMsg());
            }
        } catch (IOException e) {
            log.error("调用远程接口签名失败：{}", e.getMessage());
            throw new BizRuntimeException("调用远程接口签名失败，请稍后重试");
        }
    }

}
