/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.entity;

import io.ddd4j.core.entity.BaseEntity;

public class DemoEntity extends BaseEntity<DemoEntity> {

    private static final long serialVersionUID = 6189820231775242317L;

    private String id;

    private String name;

    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
