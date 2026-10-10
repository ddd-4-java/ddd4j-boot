package io.ddd4j.boot.sample.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息发送请求数据传输对象。
 * <p>
 * 统一承载消息的主题、标签、业务 key 与报文体，
 * 便于演示接口在同一批入参中切换不同的发送方式。
 * </p>
 */
@Data
@Accessors(chain = true)
public class MessageDTO {

    private String topic;
    private String tag;
    private String key;
    private String body;

    /**
     * 构造消息发送请求数据传输对象（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MessageDTO() {
    }

}
