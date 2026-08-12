/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.repository.entities;

import io.ddd4j.core.ddd.model.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DemoEntity implements Entity<Long> {

    @Override
    public Long id() {
        return Long.valueOf(id);
    }

    private static final long serialVersionUID = 6189820231775242317L;

    private String id;

    private String name;

    private String text;

}
