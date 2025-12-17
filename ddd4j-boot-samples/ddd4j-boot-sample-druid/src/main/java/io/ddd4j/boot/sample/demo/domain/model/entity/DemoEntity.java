package io.ddd4j.boot.sample.demo.domain.model.entity;

import io.ddd4j.boot.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Demo实体（领域层）
 */
@Data
@Accessors(chain = true)
public class DemoEntity extends BaseEntity<DemoEntity> {
    
    private Long id;
    
    private String name;
    
    private String intro;
    
    private Integer orderBy;
    
    private Integer status;
}

