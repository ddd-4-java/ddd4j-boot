/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

/**
 * 用户 MongoDB 文档实体：映射 user 集合，username 建唯一索引。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Data            // 生成getter/setter/hashCode/equals/toString（无参构造器已显式声明）
@AllArgsConstructor // 生成所有参数构造方法
// @AllArgsConstructor会导致@Data不生成无参构造方法，故手动显式声明无参构造器（与原 @NoArgsConstructor 等价，供 javadoc 可见与 com.fasterxml.jackson 反序列化使用）
@Document
public class User {
    /**
     * 无参构造，供框架反序列化实例化使用。
     */
    public User() {
    }

    /** 主键标识。 */
    @Id
    private String id; // 注解属性id为ID
    /** 用户名（唯一索引，不允许重复）。 */
    @Indexed(unique = true) // 注解属性username为索引，并且不能重复
    private String username;
    /** 姓名。 */
    private String name;
    /** 手机号。 */
    private String phone;
    /** 生日。 */
    private Date birthday;

}
