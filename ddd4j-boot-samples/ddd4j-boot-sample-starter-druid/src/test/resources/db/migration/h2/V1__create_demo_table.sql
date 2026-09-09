CREATE TABLE t_demo (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    intro VARCHAR(500),
    order_by INT DEFAULT 0,
    status INT DEFAULT 1,
    is_deleted INT DEFAULT 0,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
