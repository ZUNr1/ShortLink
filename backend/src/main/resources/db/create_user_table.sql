create table user(
    id bigint primary key auto_increment comment 'id主键',
    name varchar(50) unique not null comment '用户名称(不可重复)',
    email varchar(100) unique not null comment '用户邮箱(不可重复)',
    password varchar(100) not null comment '用户密码(md5加密后)',
    salt varchar(100) not null comment '用户盐值',
    role tinyint default 0 comment '用户角色，0表示普通用户，1表示管理员',
    status tinyint default 1 comment '用户状态，1表示正常，0表示禁用',
    created_at datetime default current_timestamp comment '创建时间',
    key idx_created_at (created_at)
)comment '用户表'
