/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.service.impl;

import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.mapper.DemoMapper;
import io.ddd4j.boot.sample.service.IDemoService;
import io.ddd4j.core.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

}
