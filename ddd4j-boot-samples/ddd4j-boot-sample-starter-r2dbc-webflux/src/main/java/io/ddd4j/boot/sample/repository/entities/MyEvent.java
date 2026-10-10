/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.repository.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 仓储层事件 MongoDB 文档：映射 event 集合，承载事件标识与消息内容。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Data            // 生成getter/setter/hashCode/equals/toString（无参构造器已显式声明）
@AllArgsConstructor
@Document(collection = "event") // 1
public class MyEvent {
    /**
     * 无参构造，供框架反序列化实例化使用。
     */
    public MyEvent() {
    }

    /** 事件主键。 */
    @Id
    private Long id;    // 2
    /** 事件消息内容。 */
    private String message;
}
