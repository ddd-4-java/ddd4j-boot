package io.ddd4j.boot.sample.service;

import com.baomidou.mybatisplus.spring.service.IService;
import io.ddd4j.boot.sample.entity.DemoEntity;

/**
 * <p>
 * Demo示例表 服务类
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
public interface IDemoService extends IService<DemoEntity> {

    /**
     * 在本地事务内保存 Demo 记录并登记事务日志。
     *
     * @param txId 事务流水号
     * @param demo 待保存的 Demo 实体
     */
    void doSave(String txId, DemoEntity demo);

}
