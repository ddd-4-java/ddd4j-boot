package io.ddd4j.boot.sample.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息报文传输对象：封装消息标签、路由键与报文体。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Data
@Accessors(chain = true)
public class MessageDTO {

    /**
     * 无参构造，供框架反序列化实例化使用。
     */
    public MessageDTO() {
    }

    private String tag;
    private String key;
    private String body;

}
