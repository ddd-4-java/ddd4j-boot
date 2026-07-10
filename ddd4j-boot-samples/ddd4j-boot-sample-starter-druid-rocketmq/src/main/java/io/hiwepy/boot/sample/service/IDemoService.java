package io.hiwepy.boot.sample.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.hiwepy.boot.sample.entity.DemoEntity;

/**
 * <p>
 * Demo示例表 服务类
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
public interface IDemoService extends IService<DemoEntity> {

    void doSave(String txId, DemoEntity demo);

}
