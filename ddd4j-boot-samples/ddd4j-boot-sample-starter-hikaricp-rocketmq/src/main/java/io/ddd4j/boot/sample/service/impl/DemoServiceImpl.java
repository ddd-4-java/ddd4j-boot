package io.ddd4j.boot.sample.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.mapper.DemoMapper;
import io.ddd4j.boot.sample.service.IDemoService;
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
public class DemoServiceImpl extends ServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

}
