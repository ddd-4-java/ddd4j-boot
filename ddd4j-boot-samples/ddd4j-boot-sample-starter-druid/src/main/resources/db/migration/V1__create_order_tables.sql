-- 创建Demo表
CREATE TABLE IF NOT EXISTS t_demo
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY
    COMMENT
    'ID',
    name
    VARCHAR
(
    255
) NOT NULL COMMENT '名称',
    intro VARCHAR
(
    500
) COMMENT '描述',
    order_by INT DEFAULT 0 COMMENT '显示顺序',
    status INT DEFAULT 1 COMMENT '状态（0:禁用|1:可用）',
    is_deleted INT DEFAULT 0 COMMENT '是否删除（0:未删除,1:已删除）',
    create_by BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '修改人ID',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name
(
    name
),
    INDEX idx_status
(
    status
),
    INDEX idx_create_time
(
    create_time
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Demo表';

-- 创建订单表
CREATE TABLE IF NOT EXISTS t_order
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY
    COMMENT
    '订单ID',
    order_no
    VARCHAR
(
    64
) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status VARCHAR
(
    32
) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消',
    total_amount DECIMAL
(
    10,
    2
) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    currency VARCHAR
(
    8
) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    province VARCHAR
(
    64
) COMMENT '省份',
    city VARCHAR
(
    64
) COMMENT '城市',
    district VARCHAR
(
    64
) COMMENT '区县',
    detail VARCHAR
(
    255
) COMMENT '详细地址',
    zip_code VARCHAR
(
    16
) COMMENT '邮编',
    remark VARCHAR
(
    500
) COMMENT '备注',
    paid_time DATETIME COMMENT '支付时间',
    shipped_time DATETIME COMMENT '发货时间',
    delivered_time DATETIME COMMENT '送达时间',
    is_deleted INT DEFAULT 0 COMMENT '是否删除（0:未删除,1:已删除）',
    create_by BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '修改人ID',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id
(
    user_id
),
    INDEX idx_order_no
(
    order_no
),
    INDEX idx_status
(
    status
),
    INDEX idx_create_time
(
    create_time
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建订单项表
CREATE TABLE IF NOT EXISTS t_order_item
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY
    COMMENT
    '订单项ID',
    order_id
    BIGINT
    NOT
    NULL
    COMMENT
    '订单ID',
    product_id
    VARCHAR
(
    64
) NOT NULL COMMENT '商品ID',
    product_name VARCHAR
(
    255
) NOT NULL COMMENT '商品名称',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL
(
    10,
    2
) NOT NULL COMMENT '单价',
    total_price DECIMAL
(
    10,
    2
) NOT NULL COMMENT '总价',
    currency VARCHAR
(
    8
) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    is_deleted INT DEFAULT 0 COMMENT '是否删除（0:未删除,1:已删除）',
    create_by BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '修改人ID',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id
(
    order_id
),
    INDEX idx_product_id
(
    product_id
),
    FOREIGN KEY
(
    order_id
) REFERENCES t_order
(
    id
)
                                                            ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

