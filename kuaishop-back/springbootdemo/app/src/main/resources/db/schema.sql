use quickshop;
-- 用户表
CREATE TABLE `user`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        varchar(50)  NOT NULL COMMENT '用户名',
    `password`        varchar(255) NOT NULL COMMENT '加密密码（BCrypt）',
    `nickname`        varchar(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`          varchar(255) DEFAULT NULL COMMENT '头像URL',
    `phone`           varchar(20)  DEFAULT NULL COMMENT '手机号',
    `email`           varchar(100) DEFAULT NULL COMMENT '邮箱',
    `gender`          int          DEFAULT NULL COMMENT '性别：0-未知 1-男 2-女',
    `status`          int          DEFAULT NULL COMMENT '状态：0-禁用 1-正常',
    `last_login_time` datetime     DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   varchar(45)  DEFAULT NULL COMMENT '最后登录IP',
    `create_time`     datetime     DEFAULT NULL COMMENT '注册时间',
    `update_time`     datetime     DEFAULT NULL COMMENT '更新时间',
    `identity`        int          NOT NULL COMMENT '身份',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 秒杀商品关联表
CREATE TABLE `seckill_product`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `activity_id`    bigint         DEFAULT NULL COMMENT '秒杀活动ID',
    `product_id`     bigint         DEFAULT NULL COMMENT '商品ID（关联商品主表）',
    `product_name`   varchar(255)   DEFAULT NULL COMMENT '商品名称（冗余存储，避免关联查询）',
    `seckill_price`  decimal(10, 2) DEFAULT NULL COMMENT '秒杀价格',
    `original_price` decimal(10, 2) DEFAULT NULL COMMENT '商品原价',
    `total_stock`    bigint         DEFAULT NULL COMMENT '总库存',
    `surplus_stock`  bigint         DEFAULT NULL COMMENT '剩余库存（核心字段，高并发更新）',
    `sold`           bigint         DEFAULT NULL COMMENT '已售数量',
    `limit_per_user` int            DEFAULT NULL COMMENT '每人限购数量',
    `create_time`    datetime       DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime       DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='秒杀商品关联表';

-- 秒杀活动表
CREATE TABLE `seckill_activity`
(
    `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀活动ID',
    `activity_name` varchar(255) DEFAULT NULL COMMENT '活动名称',
    `start_time`    datetime     DEFAULT NULL COMMENT '活动开始时间',
    `end_time`      datetime     DEFAULT NULL COMMENT '活动结束时间',
    `status`        int          DEFAULT NULL COMMENT '活动状态：0-未开始 1-进行中 2-已结束',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='秒杀活动表';

-- 商品分类表
CREATE TABLE `product_category`
(
    `id`          bigint      NOT NULL COMMENT '分类ID',
    `name`        varchar(50) NOT NULL COMMENT '分类名称',
    `parent_id`   bigint       DEFAULT NULL COMMENT '父分类ID，0表示一级分类',
    `level`       int          DEFAULT NULL COMMENT '分类层级：1-一级分类 2-二级分类',
    `icon`        varchar(100) DEFAULT NULL COMMENT '分类图标（可存图标URL或emoji）',
    `sort_order`  int          DEFAULT NULL COMMENT '排序值，越小越靠前',
    `status`      int          DEFAULT NULL COMMENT '状态：0-禁用 1-启用',
    `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类表';

-- 商品表
CREATE TABLE `product`
(
    `id`             bigint         NOT NULL COMMENT '商品ID',
    `name`           varchar(200)   NOT NULL COMMENT '商品名称',
    `description`    varchar(2000)  DEFAULT NULL COMMENT '商品描述',
    `detail`         varchar(2000)  DEFAULT NULL COMMENT '商品详情',
    `price`          decimal(10, 2) NOT NULL COMMENT '现价',
    `original_price` decimal(10, 2) DEFAULT NULL COMMENT '原价（划线价）',
    `stock`          int            DEFAULT NULL COMMENT '库存',
    `sales`          int            DEFAULT NULL COMMENT '销量',
    `category_id`    bigint         NOT NULL COMMENT '分类ID',
    `main_image`     varchar(255)   NOT NULL COMMENT '主图URL',
    `images`         json           DEFAULT NULL COMMENT '详情图数组',
    `specifications` json           DEFAULT NULL COMMENT '规格参数',
    `status`         int            DEFAULT NULL COMMENT '状态：0-下架 1-上架',
    `is_hot`         int            DEFAULT NULL COMMENT '是否热销',
    `is_new`         int            DEFAULT NULL COMMENT '是否新品',
    `view_count`     int            DEFAULT NULL COMMENT '浏览数',
    `create_time`    datetime       DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime       DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_hot` (`is_hot`),
    KEY `idx_is_new` (`is_new`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';

-- 订单表
CREATE TABLE `orders`
(
    `id`                bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`          varchar(32)    DEFAULT NULL COMMENT '订单号',
    `user_id`           bigint         DEFAULT NULL COMMENT '用户ID',
    `pay_amount`        decimal(10, 2) DEFAULT NULL COMMENT '实付金额',
    `order_status`      tinyint        DEFAULT NULL COMMENT '状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消',
    `reason`            varchar(200)   DEFAULT NULL COMMENT '原因',
    `info`              varchar(200)   DEFAULT NULL COMMENT '订单详情：数量、类型等',
    `logistics_company` varchar(200)   DEFAULT NULL COMMENT '物流公司',
    `logistics_no`      varchar(200)   DEFAULT NULL COMMENT '物流单号',
    `create_time`       datetime       DEFAULT NULL COMMENT '下单时间',
    `update_time`       datetime       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';

-- 购物车表
CREATE TABLE `cart`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id`     bigint NOT NULL COMMENT '用户ID',
    `product_id`  bigint NOT NULL COMMENT '商品ID',
    `quantity`    int      DEFAULT NULL COMMENT '数量',
    `selected`    int      DEFAULT NULL COMMENT '是否选中',
    `create_time` datetime DEFAULT NULL COMMENT '加入时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_selected` (`selected`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车表';

-- 收货地址表
CREATE TABLE `address`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`        bigint       NOT NULL COMMENT '用户ID',
    `receiver_name`  varchar(50)  NOT NULL COMMENT '收货人',
    `receiver_phone` varchar(20)  NOT NULL COMMENT '收货电话',
    `province`       varchar(50)  NOT NULL COMMENT '省份',
    `city`           varchar(50)  NOT NULL COMMENT '城市',
    `district`       varchar(50)  NOT NULL COMMENT '区县',
    `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
    `postal_code`    varchar(10) DEFAULT NULL COMMENT '邮政编码',
    `is_default`     int         DEFAULT NULL COMMENT '是否默认地址',
    `status`         int         DEFAULT NULL COMMENT '状态：0-删除 1-正常',
    `create_time`    datetime    DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_default` (`is_default`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='收货地址表';