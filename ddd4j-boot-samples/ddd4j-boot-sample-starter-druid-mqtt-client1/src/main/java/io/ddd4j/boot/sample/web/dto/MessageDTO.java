package io.ddd4j.boot.sample.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * MQTT 消息传输对象。
 * <p>
 * 承载一次消息投递所需的标签、标识键与消息体，配合 Lombok {@code @Data}
 * 与 {@code @Accessors(chain = true)} 提供链式赋值能力。
 * </p>
 */
@Data
@Accessors(chain = true)
public class MessageDTO {

    private String tag;
    private String key;
    private String body;

    /**
     * 构造 MQTT 消息传输对象（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MessageDTO() {
    }

}
