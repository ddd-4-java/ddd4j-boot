package io.ddd4j.boot.mq.impl.serialization;

import io.ddd4j.boot.core.utils.JsonKit;
import io.ddd4j.boot.mq.serialization.MQMessageSerialization;
import org.springframework.stereotype.Component;

@Component("JsonMQEventSerialization")
/**
 * @deprecated legacy 序列化实现，新代码使用 {@link MQMessageSerialization}。
 */
@Deprecated
public class JsonMQEventSerialization implements MQMessageSerialization {

    @Override
    public <S, T> T deserialize(S src, Class<T> dist) throws RuntimeException {
        if (src == null || ((String)src).isEmpty()) {
            return null;
        }
        return JsonKit.toObject(src, dist);
    }

    @Override
    public String serialize(Object src) throws RuntimeException {
        return JsonKit.toJson(src);
    }

}