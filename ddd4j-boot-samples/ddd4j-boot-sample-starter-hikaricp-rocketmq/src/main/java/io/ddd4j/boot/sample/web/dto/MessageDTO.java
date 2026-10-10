package io.ddd4j.boot.sample.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息数据传输对象，承载主题、标签、键与消息体。
 */
@Data
@Accessors(chain = true)
public class MessageDTO {

    /**
     * 构造消息数据传输对象实例。
     */
    public MessageDTO() {
    }

    /**
     * 主题。
     */
    private String topic;

    /**
     * 标签。
     */
    private String tag;

    /**
     * 消息键。
     */
    private String key;

    /**
     * 消息体。
     */
    private String body;

}
