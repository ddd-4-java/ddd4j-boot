package io.ddd4j.boot.sample.demo.domain.repository;

import io.ddd4j.boot.sample.demo.domain.model.entity.DemoEntity;

import java.util.List;
import java.util.Optional;

/**
 * Demo仓储接口（领域层）
 */
public interface DemoRepository {

    /**
     * 保存Demo
     */
    DemoEntity save(DemoEntity entity);

    /**
     * 根据ID查询Demo
     */
    Optional<DemoEntity> findById(Long id);

    /**
     * 查询所有Demo
     */
    List<DemoEntity> findAll();

    /**
     * 删除Demo
     */
    void delete(Long id);
}

