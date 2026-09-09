package io.ddd4j.boot.sample.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.entity.TxLogEntity;
import io.ddd4j.boot.sample.mapper.DemoMapper;
import io.ddd4j.boot.sample.service.IDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * Demo示例表 服务实现类
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@Service
@Slf4j
public class DemoServiceImpl extends ServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

    // 本地事物
    @Transactional
    public void doSave(String txId, DemoEntity demo) {
        // 本地事物代码
        getBaseMapper().insert(demo);
        //记录日志到数据库,回查使用
        TxLogEntity txLog = new TxLogEntity();
        txLog.setTxLogId(txId);
        txLog.setContent("事物测试");
        txLog.setDate(new Date());
    }

}