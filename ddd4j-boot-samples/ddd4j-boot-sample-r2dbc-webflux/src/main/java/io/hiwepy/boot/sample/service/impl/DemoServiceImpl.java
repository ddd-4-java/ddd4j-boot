/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.service.impl;

import io.ddd4j.boot.core.service.BaseServiceImpl;
import io.hiwepy.boot.sample.entity.DemoEntity;
import io.hiwepy.boot.sample.mapper.DemoMapper;
import io.hiwepy.boot.sample.service.IDemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

}
