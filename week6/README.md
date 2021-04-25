第6周作业
```
1.（选做）尝试使用 Lambda/Stream/Guava 优化之前作业的代码。
2.（选做）尝试使用 Lambda/Stream/Guava 优化工作中编码的代码。
3.（选做）根据课上提供的材料，系统性学习一遍设计模式，并在工作学习中思考如何用设计模式解决问题。
4.（选做）根据课上提供的材料，深入了解 Google 和 Alibaba 编码规范，并根据这些规范，检查自己写代码是否符合规范，有什么可以改进的。
5.（选做）基于课程中的设计原则和最佳实践，分析是否可以将自己负责的业务系统进行数据库设计或是数据库服务器方面的优化
6.（必做）基于电商交易场景（用户、商品、订单），设计一套简单的表结构，提交 DDL 的 SQL 文件到 Github（后面 2 周的作业依然要是用到这个表结构）。
7.（选做）尽可能多的从“常见关系数据库”中列的清单，安装运行，并使用上一题的 SQL 测试简单的增删改查。
8.（选做）基于上一题，尝试对各个数据库测试 100 万订单数据的增删改查性能。
9.（选做）尝试对 MySQL 不同引擎下测试 100 万订单数据的增删改查性能。
10.（选做）模拟 1000 万订单数据，测试不同方式下导入导出（数据备份还原）MySQL 的速度，包括 jdbc 程序处理和命令行处理。思考和实践，如何提升处理效率。
11.（选做）对 MySQL 配置不同的数据库连接池（DBCP、C3P0、Druid、Hikari），测试增删改查 100 万次，对比性能，生成报告。
```

### 必做
#### 6题
```
（必做）基于电商交易场景（用户、商品、订单），设计一套简单的表结构，提交 DDL 的 SQL 文件到 Github（后面 2 周的作业依然要是用到这个表结构）。
```
思路
```
1.用户表 是用户登录表 包含用户ID，账号和密码 最基础的数据。
2.商品表 是商品的详细信息的表，商品的价格，商品的名称，商品的简介
3.订单表 是用户购买订单的表，订单id，用户id，订单价格，由于订单里面包含多个是商品，抽出另外的一张表订单产品表。
4.订单产品表 是订单和产品的关系，包含订单id，产品id，产品的数量。
```

用户表，用户id id,登录账号 login_id,密码 passwd ,用户状态 status。
```
CREATE TABLE `t_user` (
	`id` bigint(20) NOT NULL,
	`login_id` varchar(50) NOT NULL COMMENT '登陆密码',
	`passwd` varchar(100) NOT NULL COMMENT '密码',
	`status` varchar(20) DEFAULT 'ENABLE' COMMENT '状态',
	`create_user` varchar(50) DEFAULT NULL COMMENT '申请人',
	`create_date` date DEFAULT NULL COMMENT '创建日期',
	`update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
	`update_date` datetime DEFAULT NULL COMMENT '更新日期',
	`remark` varchar(50) DEFAULT NULL COMMENT '备注',
	PRIMARY KEY (`id`)
) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户登录表' CHECKSUM=0 DELAY_KEY_WRITE=0;
```
产品表 产品id id,产品名字name，产品描述detail，产品价格price ,产品状态status(varchar(20))
```
CREATE TABLE `t_product` (
	`id` bigint(20) NOT NULL,
	`name` varchar(50) NOT NULL COMMENT '产品名字',
	`detail` varchar(1000) Default NULL COMMENT '产品描述',
	`price` int(10) DEFAULT 0 COMMENT '产品价格',
	`status` varchar(20) DEFAULT 'ENABLE' COMMENT '状态',
	`create_user` varchar(50) DEFAULT NULL COMMENT '申请人',
	`create_date` date DEFAULT NULL COMMENT '创建日期',
	`update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
	`update_date` datetime DEFAULT NULL COMMENT '更新日期',
	`remark` varchar(50) DEFAULT NULL COMMENT '备注',
	PRIMARY KEY (`id`)
) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=DYNAMIC COMMENT='产品表' CHECKSUM=0 DELAY_KEY_WRITE=0;
```
订单表 订单id id,下单人id user_id,订单总价 amount，订单状态 order_status
```
CREATE TABLE `t_order` (
	`id` bigint(20) NOT NULL,
	`user_id` bigint(100) NOT NULL COMMENT '用户ID',
	`amount` int(10) DEFAULT 0 COMMENT '订单总价',
	`order_status` varchar(20) DEFAULT 'UN_PAY' COMMENT '订单状态',
	`create_user` varchar(50) DEFAULT NULL COMMENT '申请人',
	`create_date` date DEFAULT NULL COMMENT '创建日期',
	`update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
	`update_date` datetime DEFAULT NULL COMMENT '更新日期',
	`remark` varchar(50) DEFAULT NULL COMMENT '备注',
	PRIMARY KEY (`id`)
) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单表' CHECKSUM=0 DELAY_KEY_WRITE=0;
```
订单产品表 订单id order_id 订单表的主键Id，产品id product_id 产品表的主键ID，产品数量 num
```
CREATE TABLE `t_order_product` (
	`id` bigint(20) NOT NULL,
	`order_id` bigint(20) NOT NULL COMMENT '订单ID',
	`product_id` bigint(20) NOT NULL COMMENT '产品ID',
	`num` int(10) DEFAULT 0 COMMENT '购买数量',
	`create_user` varchar(50) DEFAULT NULL COMMENT '申请人',
	`create_date` date DEFAULT NULL COMMENT '创建日期',
	`update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
	`update_date` datetime DEFAULT NULL COMMENT '更新日期',
	`remark` varchar(50) DEFAULT NULL COMMENT '备注',
	PRIMARY KEY (`id`)
) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单产品表' CHECKSUM=0 DELAY_KEY_WRITE=0;
```
以上所有的表的主键采样分布式主键20位，有公共字段 创建人，更新时间，更新人，创建时间，备注。当然这些字段，可以自定义，如果对空间和存储有要求的情况下，根据业务进行取舍。



