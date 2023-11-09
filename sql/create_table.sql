-- 创建库
create database if not exists enndfp_douyin;

-- 切换库
use enndfp_douyin;

create table user
(
    id                    bigint auto_increment comment 'id' primary key,
    phone                 varchar(32)                           not null comment '手机号',
    nickname              varchar(256)                          not null comment '昵称',
    douyin_num            varchar(256)                          not null comment '抖音号，唯一标识，需要限制修改次数，可以用于付费修改',
    face                  varchar(1024)                         not null comment '头像',
    sex                   tinyint     default 2                 not null comment '性别 1:男  0:女  2:保密',
    birthday              date                                  not null comment '生日',
    country               varchar(32) default '中国'            null comment '国家',
    province              varchar(32) default NULL              null comment '省份',
    city                  varchar(32) default NULL              null comment '城市',
    district              varchar(32) default NULL              null comment '区县',
    `description`         varchar(256)                          not null comment '个人简介',
    bg_img                varchar(1024)                         null comment '个人介绍的背景图',
    douyin_update_allowed tinyint     default 1                 not null comment '抖音能否被修改，1：默认，可以修改；0，无法修改',
    created_time          datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time          datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted            tinyint     default 0                 not null comment '是否删除',
    constraint unique_phone
        unique (phone),
    constraint unique_douyin_num
        unique (douyin_num)
)
    comment '用户表';

create table vlog
(
    id              bigint auto_increment comment 'id' primary key,
    vlog_user_id    bigint                                 not null comment 'vlog视频发布者',
    url             varchar(1024)                          not null comment '视频播放地址',
    cover           varchar(1024)                          not null comment '视频封面',
    title           varchar(256) default NULL              null comment '视频标题',
    width           int                                    not null comment '视频width',
    height          int                                    not null comment '视频height',
    like_counts     int          default 0                 not null comment '点赞总数',
    comments_counts int          default 0                 not null comment '评论总数',
    is_private      tinyint      default 0                 not null comment '是否私密 0：不私密 1：私密',
    created_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted      tinyint      default 0                 not null comment '是否删除'
)
    comment '短视频表';

create table my_liked_vlog
(
    id           bigint auto_increment comment 'id' primary key,
    user_id      bigint                                 not null comment '用户id',
    vlog_id      bigint                                 not null comment '短视频id',
    created_time datetime default CURRENT_TIMESTAMP     not null comment '创建时间',
    updated_time datetime default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted_time datetime default '2000-01-01 00:00:01' not null comment '删除时间',
    is_deleted   tinyint  default 0                     not null comment '是否删除',
    constraint unique_user_liked_vlog_deleted_time
        unique (user_id, vlog_id, deleted_time)
)
    comment '点赞短视频关系表';

create table fans
(
    id            bigint auto_increment comment 'id' primary key,
    fan_id        bigint                                 not null comment '喜欢的作者用户id',
    vloger_id     bigint                                 not null comment '粉丝用户id',
    is_mutual_fan tinyint                                not null comment '是否互相关注 0:否 1:是',
    created_time  datetime default CURRENT_TIMESTAMP     not null comment '创建时间',
    updated_time  datetime default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted_time  datetime default '2000-01-01 00:00:01' not null comment '删除时间',
    is_deleted    tinyint  default 0                     not null comment '是否删除',
    constraint unique_fan_vloger_deleted_time
        unique (fan_id, vloger_id, deleted_time)
)
    comment '粉丝表';

create table comment
(
    id                bigint auto_increment comment 'id' primary key,
    comment_user_id   bigint                             not null comment '发布留言用户id',
    vlog_id           bigint                             not null comment '视频id',
    vlog_user_id      bigint                             not null comment '视频作者id',
    father_comment_id bigint                             not null comment '如果是回复留言，则本条为子留言，需要关联查询',
    content           varchar(256)                       not null comment '留言内容',
    like_counts       int      default 0                 not null comment '留言的点赞总数',
    created_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted        tinyint  default 0                 not null comment '是否删除'
)
    comment '评论表';