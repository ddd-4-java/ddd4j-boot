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
     *
     * @param entity 实体对象
     * @return 新增结果
     */
    DemoEntity save(DemoEntity entity);

    /**
     * 根据ID查询Demo
     *
     * @param id 标识 ID
     * @return 查询结果
     */
    Optional<DemoEntity> findById(Long id);

    /**
     * 查询所有Demo
     *
     * @return 查询结果
     */
    List<DemoEntity> findAll();

    /**
     * 删除Demo
     *
     * @param id 标识 ID
     */
    void delete(Long id);
}

