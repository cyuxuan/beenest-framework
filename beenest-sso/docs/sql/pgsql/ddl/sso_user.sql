-- SSO User Table
CREATE TABLE IF NOT EXISTS sso_user (
    id BIGSERIAL PRIMARY KEY,
    account VARCHAR(50) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sso_user IS 'SSO 用户管理表';
COMMENT ON COLUMN sso_user.id IS '主键';
COMMENT ON COLUMN sso_user.account IS '账号';
COMMENT ON COLUMN sso_user.username IS '用户名';
COMMENT ON COLUMN sso_user.password IS '密码';
COMMENT ON COLUMN sso_user.status IS '状态 (1: 启用, 0: 禁用)';
COMMENT ON COLUMN sso_user.create_time IS '创建时间';
COMMENT ON COLUMN sso_user.update_time IS '更新时间';
