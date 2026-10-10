/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元对象自动填充处理器：在插入/更新时回填创建时间与修改时间。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public MyMetaObjectHandler() {
    }

    /**
     * 插入时自动填充 {@code createTime} 字段。
     *
     * @param metaObject 目标实体的元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
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
     * 更新时自动填充 {@code modifyTime} 字段。
     *
     * @param metaObject 目标实体的元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
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

