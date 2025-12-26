package io.ddd4j.boot.sample.service.impl;

import io.ddd4j.boot.core.service.BaseServiceImpl;
import io.hiwepy.boot.sample.entity.DemoEntity;
import io.hiwepy.boot.sample.mapper.DemoMapper;
import io.hiwepy.boot.sample.service.IDemoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Demo示例表 服务实现类
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

}
