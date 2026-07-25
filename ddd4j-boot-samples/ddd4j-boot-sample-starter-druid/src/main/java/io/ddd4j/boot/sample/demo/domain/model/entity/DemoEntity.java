package io.ddd4j.boot.sample.demo.domain.model.entity;

import io.ddd4j.core.ddd.model.Entity;



/**
 * Demo实体（领域层）
 */


public class DemoEntity implements Entity<Long> {

    @Override
    public Long id() {
        return id;
    }

    private Long id;

    private String name;

    private String intro;

    private Integer orderBy;

    private Integer status;
}

