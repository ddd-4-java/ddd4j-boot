/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.service.impl;

import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.mapper.DemoMapper;
import io.ddd4j.boot.sample.service.IDemoService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 演示业务服务实现：基于 MyBatis-Plus {@code ServiceImpl} 继承通用 CRUD 能力并实现 {@code IDemoService}。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

    /**
     * 构造演示业务服务实现。
     */
    public DemoServiceImpl() {
    }

}
