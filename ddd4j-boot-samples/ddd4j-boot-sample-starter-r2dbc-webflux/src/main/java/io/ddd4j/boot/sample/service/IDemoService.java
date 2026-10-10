/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.service;

import io.ddd4j.boot.sample.entity.DemoEntity;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * 演示业务服务接口：继承 MyBatis-Plus {@code IService}，约定面向 {@code DemoEntity} 的通用服务能力。
 *
 * @author ddd4j
 * @since 1.0.0
 */
public interface IDemoService extends IService<DemoEntity> {


}
