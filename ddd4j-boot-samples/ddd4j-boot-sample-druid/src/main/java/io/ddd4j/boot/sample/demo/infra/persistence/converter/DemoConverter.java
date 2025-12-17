package io.ddd4j.boot.sample.demo.infra.persistence.converter;

import io.ddd4j.boot.sample.demo.domain.model.entity.DemoEntity;
import org.springframework.stereotype.Component;

/**
 * Demo转换器（领域对象与持久化实体转换）
 */
@Component
public class DemoConverter {
    
    /**
     * 领域对象转持久化实体
     */
    public io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity toEntity(DemoEntity domain) {
        if (domain == null) {
            return null;
        }
        
        io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity entity =
                new io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setIntro(domain.getIntro());
        entity.setOrderBy(domain.getOrderBy());
        entity.setStatus(domain.getStatus());
        
        return entity;
    }
    
    /**
     * 持久化实体转领域对象
     */
    public DemoEntity toDomain(io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        DemoEntity domain = new DemoEntity();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setIntro(entity.getIntro());
        domain.setOrderBy(entity.getOrderBy());
        domain.setStatus(entity.getStatus());
        domain.setCreateTime(entity.getCreateTime());
        domain.setUpdateTime(entity.getUpdateTime());
        
        return domain;
    }
}
