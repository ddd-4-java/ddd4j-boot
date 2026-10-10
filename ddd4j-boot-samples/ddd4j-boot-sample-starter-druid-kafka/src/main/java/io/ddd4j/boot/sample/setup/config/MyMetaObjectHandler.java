/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元数据自动填充处理器。
 * <p>
 * 在插入与更新操作时统一填充 {@code createTime}、{@code modifyTime} 等审计字段，
 * 避免各业务处重复手工赋值。
 * </p>
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 构造元数据自动填充处理器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MyMetaObjectHandler() {
    }

    /**
     * 插入操作时的字段填充逻辑，为插入记录填充创建时间。
     *
     * @param metaObject 待填充的元数据对象
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
     * 更新操作时的字段填充逻辑，为更新记录填充修改时间。
     *
     * @param metaObject 待填充的元数据对象
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

