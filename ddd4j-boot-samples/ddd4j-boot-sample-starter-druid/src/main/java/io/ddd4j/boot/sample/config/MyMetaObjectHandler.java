/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * Mybatis-Plus 元数据字段自动填充处理器。
 *
 * <p>插入与更新时统一为 createTime、modifyTime 字段写入当前时间。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 构造元数据填充处理器实例。
     *
     */
    public MyMetaObjectHandler() {
    }

    /**
     * 插入时的字段自动填充。
     *
     * @param metaObject 目标对象的元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        org.slf4j.LoggerFactory.getLogger(MyMetaObjectHandler.class).info("start insert fill ....");
        //this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        // 或者
        //this.strictUpdateFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        // 或者
        /*
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = LocalDateTime.now();

        localDateTime = localDateTime.plusSeconds(1);//设置超时时间为1秒

        ZonedDateTime zdt = localDateTime.atZone(zoneId);

        Date date = Date.from(zdt.toInstant());
        */
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now()); // 也可以使用(3.3.0 该方法有bug)
    }

    /**
     * 更新时的字段自动填充。
     *
     * @param metaObject 目标对象的元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        org.slf4j.LoggerFactory.getLogger(MyMetaObjectHandler.class).info("start update fill ....");
        //this.strictUpdateFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        // 或者
        // this.strictUpdateFill(metaObject, "modifyTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        // 或者
/*
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = LocalDateTime.now();

        localDateTime = localDateTime.plusSeconds(1);//设置超时时间为1秒

        ZonedDateTime zdt = localDateTime.atZone(zoneId);

        Date date = Date.from(zdt.toInstant());*/
        this.fillStrategy(metaObject, "modifyTime", LocalDateTime.now()); // 也可以使用(3.3.0 该方法有bug)
    }
}

