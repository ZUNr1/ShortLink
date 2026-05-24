create table link (
                      id bigint primary key auto_increment comment 'id主键',
                      user_id bigint comment '关联用户，未登录为null',
                      name varchar(128) default null comment '短链名称，用户自定义',
                      long_url varchar(2048) not null comment '长链接',
                      short_code varchar(32) not null comment '短码（如 abc123）',
                      click_count bigint default 0 comment '点击次数',
                      expire_at datetime comment '过期时间，null表示永久',
                      created_at datetime default current_timestamp comment '创建时间',
                      updated_at datetime default current_timestamp on update current_timestamp comment '更新时间',
                      unique key uk_short_code (short_code),
                      key idx_user (user_id),
                      key idx_expire (expire_at)
) comment '短链映射表';