# Week08 作业题目
```
1.（选做）分析前面作业设计的表，是否可以做垂直拆分。
2.（必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。
3.（选做）模拟 1000 万的订单单表数据，迁移到上面作业 2 的分库分表中。
4.（选做）重新搭建一套 4 个库各 64 个表的分库分表，将作业 2 中的数据迁移到新分库。
5.（选做）列举常见的分布式事务，简单分析其使用场景和优缺点。
6.（必做）基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。
7.（选做）基于 ShardingSphere narayana XA 实现一个简单的分布式事务 demo。
8.（选做）基于 seata 框架实现 TCC 或 AT 模式的分布式事务 demo。
9.（选做☆）设计实现一个简单的 XA 分布式事务框架 demo，只需要能管理和调用 2 个 MySQL 的本地事务即可，不需要考虑全局事务的持久化和恢复、高可用等。
10.（选做☆）设计实现一个 TCC 分布式事务框架的简单 Demo，需要实现事务管理器，不需要实现全局事务的持久化和恢复、高可用等。
11.（选做☆）设计实现一个 AT 分布式事务框架的简单 Demo，仅需要支持根据主键 id 进行的单个删改操作的 SQL 或插入操作的事务。
```

## 2.（必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示

shardingSphere配置
server.xml
```
authentication:
  users:
    root:
      password: xxx  #配置密码
    sharding:
      password: sharding
      authorizedSchemas: sharding_db  #逻辑数据库

props:
  max-connections-size-per-query: 1
  acceptor-size: 16  # The default value is available processors count * 2.
  executor-size: 16  # Infinite by default.
  proxy-frontend-flush-threshold: 128  # The default value is 128.
    # LOCAL: Proxy will run with LOCAL transaction.
    # XA: Proxy will run with XA transaction.
    # BASE: Proxy will run with B.A.S.E transaction.
  proxy-transaction-type: LOCAL
  proxy-opentracing-enabled: false
  proxy-hint-enabled: false
  query-with-cipher-column: true
  sql-show: true #打印sql日志
  check-table-metadata-enabled: false

```
这里需要配置 登陆代理的密码，开启sql日志

编辑数据库规则配置，conf-sharding.xml
```
schemaName: sharding_db

dataSourceCommon:
  username: root
  password: xxxx
  connectionTimeoutMilliseconds: 30000
  idleTimeoutMilliseconds: 60000
  maxLifetimeMilliseconds: 1800000
  maxPoolSize: 50
  minPoolSize: 1
  maintenanceIntervalMilliseconds: 30000
#
dataSources:
  ds0:
    url: jdbc:mysql://39.108.1.211:3306/ds0?serverTimezone=UTC&useSSL=false
  ds1:
    url: jdbc:mysql://39.108.1.211:3306/ds1?serverTimezone=UTC&useSSL=false

rules:
- !SHARDING
  tables:
    t_order:
      actualDataNodes: ds${0..1}.t_order${0..15}
      tableStrategy:
        standard:
          shardingColumn: order_id
          shardingAlgorithmName: t_order_inline
      keyGenerateStrategy:
        column: order_id
        keyGeneratorName: snowflake
#    t_order_item:
#      actualDataNodes: ds_${0..1}.t_order_item_${0..1}
#      tableStrategy:
#        standard:
#          shardingColumn: order_id
#          shardingAlgorithmName: t_order_item_inline
#      keyGenerateStrategy:
#        column: order_item_id
#        keyGeneratorName: snowflake
#  bindingTables:
#    - t_order,t_order_item
  defaultDatabaseStrategy:
    standard:
      shardingColumn: user_id
      shardingAlgorithmName: database_inline
  defaultTableStrategy:
    none:
  
  shardingAlgorithms:
    database_inline:
      type: INLINE
      props:
        algorithm-expression: ds${user_id % 2}
    t_order_inline:
      type: INLINE
      props:
        algorithm-expression: t_order${order_id % 16}
#    t_order_item_inline:
#      type: INLINE
#      props:
#        algorithm-expression: t_order_item_${order_id % 2}
#  
  keyGenerators:
    snowflake:
      type: SNOWFLAKE
      props:
        worker-id: 1

```
这里配置连接真实数据库的账户密码，配置数据源，配置数据库的分库规则，确定分库字段，和确定分表字段，和分表字段。
这里配置了2个数据ds0,ds1，每个库16张表,t_order{0..15}。

配置完成后启动shardingSphere,如果提示没有jdbc.那么需要把mysql的mysql-connector-java的jar包放到lib目录下。

启动shardingsphere
```
./bin/start.sh  
```
默认是3307端口，如果需要 直接在命令后面添加端口号
查看打印的日志
```
tail -f /logs/stdout.log
```
那么就可以本地连接了。
```
mysql -u root -h 127.0.0.1 -P 3307 -A
```
需要添加-A参数，不要预读元数据，不然sql一直连接不上

创建t_order表
```
create table t_order (
`order_id` bigint(20) NOT NULL  ,
`user_id` bigint(20) NOT NULL,
`username` varchar(20) DEFAULT NULL COMMENT '姓名',
primary key(`order_id`)
);
```
那么这个sql,会在2个库上创建t_order0到t_order15的表。

jdbc连接shardingSphereProxy测试增删改查。
```


  public static void main(String[] args) {

    String createSql = "create table `t_order`(\n"
        + "`order_id` bigint(20)  NOT NULL comment '订单ID',\n"
        + "`user_id` bigint(20) NOT NULL comment '用户ID',\n"
        + "`name` varchar(20) NOT NULL comment '用户mc',\n"
        + "primary key(`order_id`)\n"
        + ")";

    String insertSql = "insert into t_order(`user_id`,`name`) values (?,?)";
    String querySql = "select * from t_order where user_id=?";
    String deleteSql = "delete from t_order where user_id =?";
    String upateSql = "update t_order set name =? where user_id =?";

    String dropSql = "drop table t_order ";
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager
          .getConnection("jdbc:mysql://39.108.1.211:3307/sharding_db?serverTimezone=UTC", "root",
              "xxx!");
      System.out.println(connection);
//      preparedStatement = connection.prepareStatement();
//      crate table
      preparedStatement= connection.prepareStatement(createSql);
      if (preparedStatement.execute()) {
        System.out.println("create the table t_order");
      }

      //设置非自动提交
//      connection.setAutoCommit(false);
      //insert
      PreparedStatement preparedStatement1  = connection.prepareStatement(insertSql);
      preparedStatement1.setInt(1,1);
      preparedStatement1.setString(2,"zhanhsa");
      int inserRows = preparedStatement1.executeUpdate();
      if (inserRows == 1) {
        System.out.println("insert into table a");
      }
      //query
      PreparedStatement preparedStatement2  = connection.prepareStatement(querySql);
      preparedStatement2.setInt(1,1);
      resultSet = preparedStatement2.executeQuery();
      while (resultSet.next()) {
        System.out.println(resultSet.getString("name"));
      }
      // update
      PreparedStatement preparedStatement3  = connection.prepareStatement(upateSql);
      preparedStatement3.setLong(2,1);
      preparedStatement3.setString(1,"lis");
      int updateRows = preparedStatement3.executeUpdate();
      if (updateRows == 1) {
        System.out.println("update the table a");
      }
      //query
      preparedStatement2.setInt(1,1);
      resultSet = preparedStatement2.executeQuery();
      while (resultSet.next()) {
        System.out.println(resultSet.getString("name"));
      }
      PreparedStatement preparedStatement4  = connection.prepareStatement(deleteSql);
      preparedStatement4.setInt(1,1);
      //delete
      int delRows = preparedStatement4.executeUpdate();
      if (delRows == 1) {
        System.out.println("delete the table a");
      }
      //query
      PreparedStatement preparedStatement5  = connection.prepareStatement(querySql);
      preparedStatement5.setInt(1,1);
      resultSet = preparedStatement5.executeQuery();
      while (resultSet.next()) {
        System.out.println(resultSet.getString("name"));
      }
      //crate table
      PreparedStatement preparedStatemen6  = connection.prepareStatement(dropSql);
      if (preparedStatemen6.execute()) {
        System.out.println("drop the table a");
      }
//      connection.commit();
    } catch (Exception e) {
      try {
        System.out.println(e);
//        connection.rollback();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }finally {
      try {
        resultSet.close();
        preparedStatement.close();
        connection.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }
```
这里的参数和平台的jdbc是一样的，只是配置的数据源是shardingShpereProxy的配置。




## 6.（必做）基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。

使用shardingSphere-jdbc,默认ShardingSphere的xa就是使用Atomikos的XA。
引入配置ShardingSphere配置
```
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-jdbc-core</artifactId>
    <version>${shardingsphere.version}</version>
</dependency>

<!-- 使用 XA 事务时，需要引入此模块 -->
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-transaction-xa-core</artifactId>
    <version>${shardingsphere.version}</version>
</dependency>


```
配置ShardingSphere-jdbc配置多分片,shardingSphere.yaml文件
```


# 配置真实数据源
dataSources:
  # 配置第 1 个数据源
  ds0: !!com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.jdbc.Driver
    jdbcUrl: jdbc:mysql://39.108.1.211:3306/ds0
    username: root
    password: X
  # 配置第 2 个数据源
  ds1: !!com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.jdbc.Driver
    jdbcUrl: jdbc:mysql://39.108.1.211:3306/ds1
    username: root
    password: X
rules:
  # 配置分片规则
  - !SHARDING
    tables:
      # 配置 t_order 表规则
      t_order:
        actualDataNodes: ds${0..1}.t_order${0..15}
        # 配置分库策略
        databaseStrategy:
          standard:
            shardingColumn: user_id
            shardingAlgorithmName: database_inline
        # 配置分表策略
        tableStrategy:
          standard:
            shardingColumn: order_id
            shardingAlgorithmName: table_inline
#        keyGenerateStrategy:
#          column: order_id
#          keyGeneratorName: snowflake

      #      t_order_item:
      # 省略配置 t_order_item 表规则...
      # ...

    # 配置分片算法
    shardingAlgorithms:
      database_inline:
        type: INLINE
        props:
          algorithm-expression: ds${user_id % 2}
      table_inline:
        type: INLINE
        props:
          algorithm-expression: t_order${order_id % 15}

    keyGenerators:
      snowflake:
        type: SNOWFLAKE
        props:
          worker-id: 1

```

这里配置了2个数据，和每个数据16张表和上面使用Proxy是一样的。
配置datasource
```
 // 创建 ShardingSphereDataSource
    @Bean
    DataSource getDataSource() {
        DataSource dataSource = null;
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(
                ResourceUtils.getFile(
                "classpath:shardingsphere.yaml"));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}

```
在resouces文件目录下，配置amotokos的配置文件jta.properties,修改默认配置
```

# 文件日志文件名字
com.atomikos.icatch.file=ak
#jta 事务超时时间
com.atomikos.icatch.max_timeout=10000
#最大活跃事务数
com.atomikos.icatch.max_actives=50
# 日志
com.atomikos.icatch.enable_logging=true
```
配置事务管理器
```

@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {
    
    @Bean
    public PlatformTransactionManager txManager(final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```
因为这里我使用了mybatis-plus的原因，需要在测试插入到不同的库，默认，分片数据的分布式XA事务

```

@Override
@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
@ShardingTransactionType(TransactionType.XA)
public int add() throws Exception {
  for (long i = 1 ;i < 10 ;i++) {
    OrderDo orderDo = new OrderDo();
    orderDo.setOrderId(snowFlakeUtil.nextId());
    orderDo.setUserId(i);
    orderDo.setUsername("张三"+i);
    //插入不同的库 user_id 不同，至少是2个库的数据
    int rows = orderMapper.insert(orderDo);
    if (i == 8) {
        //抛出异常 xa回滚
        throw new Exception("dsdsd");
    }
  }
  return 0;
}
```

在方法上注解了回滚的错误异常，和ShardingSphere的XA事务类型。数据插入，到第8条的时候，抛出异常没有，让xa回滚。



读取然后在读取原来的数据库，发现没有插入.
```
@Override
public IPage<OrderVo> queryPage(int size,int current) {
  IPage<OrderVo> page = new Page<>(current,size);
  page.setRecords(orderMapper.queryPage(page));
  return page;
}

```


