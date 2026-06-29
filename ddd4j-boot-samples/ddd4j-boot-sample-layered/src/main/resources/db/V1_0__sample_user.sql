-- sample_user 表结构
CREATE TABLE IF NOT EXISTS `sample_user`
(
    `id`
    VARCHAR
(
    64
) NOT NULL COMMENT '用户ID',
    `phone` VARCHAR
(
    20
) NOT NULL COMMENT '手机号（业务键）',
    `nickname` VARCHAR
(
    100
) DEFAULT NULL COMMENT '昵称',
    `status` INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `del_flag` INT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY
(
    `id`
),
    UNIQUE KEY `uk_phone`
(
    `phone`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例用户表';
