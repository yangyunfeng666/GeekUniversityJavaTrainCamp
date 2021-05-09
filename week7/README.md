
# 第7周题目
```

1.（选做）用今天课上学习的知识，分析自己系统的 SQL 和表结构
2.（必做）按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率
3.（选做）按自己设计的表结构，插入 1000 万订单模拟数据，测试不同方式的插入效
4.（选做）使用不同的索引或组合，测试不同方式查询效率
5.（选做）调整测试数据，使得数据尽量均匀，模拟 1 年时间内的交易，计算一年的销售报表：销售总额，订单数，客单价，每月销售量，前十的商品等等（可以自己设计更多指标）
6.（选做）尝试自己做一个 ID 生成器（可以模拟 Seq 或 Snowflake）
7.（选做）尝试实现或改造一个非精确分页的程序
8.（选做）配置一遍异步复制，半同步复制、组复制
9.（必做）读写分离 - 动态切换数据源版本 1.0
10.（必做）读写分离 - 数据库框架版本 2.0
11.（选做）读写分离 - 数据库中间件版本 3.0
12.（选做）配置 MHA，模拟 master 宕机
13.（选做）配置 MGR，模拟 master 宕机
14.（选做）配置 Orchestrator，模拟 master 宕机，演练 UI 调整拓扑结构
```
## 选做题1
题目
```
用今天课上学习的知识，分析自己系统的 SQL 和表结构
```
前面的数据库的设计是存在问题的：
```
1.状态字段，使用了varchar字段，从节约存储空间的角度是需要设置成int字段，比如TINTINT(1)。
2.时间字段，以前都是设计成datetime类型，可以设计成bigint字段，更佳节约空间和查询比较的时候更方便查询。
3.字符集的选取上，以前选取的是utf8,其实是假的utf8字符集，应该使用utf8mb4。
4.去掉不常用的多余的字段，比如remark字段，可能可以使用价值，如果后面需要，在宁外的从表中设计。
```
使用我把我的用户表查询设计了如下。
```
CREATE TABLE `t_user` (
	`id` bigint(20) NOT NULL,
	`login_id` varchar(50) NOT NULL COMMENT '登陆账号',
	`passwd` varchar(50) NOT NULL COMMENT '密码',
	`status` TINYINT(1) DEFAULT 0 COMMENT '状态',
	`create_user` varchar(20) DEFAULT '' COMMENT '申请人',
	`create_date` BIGINT(20) DEFAULT 0 COMMENT '创建日期',
	`update_user` varchar(20) DEFAULT '' COMMENT '更新人',
	`update_date` BIGINT(20) DEFAULT 0 COMMENT '更新日期',
	PRIMARY KEY (`id`)
) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4  COMMENT='用户登录表';
```
## 必做题2
题目
```
按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率
```
这里就模拟插入用户表100万。我只测试了1w。
### 思路1
```
从效率上，如果当条插入，需要每条都解析sql，可以使用批量插入的，比如insert into（字段） 中间插入数据，但是整个数据的的包不能大于max_allowed_packet的值，可以先

show variables LIKE '%max_allowed_packet%';
查看大小，如果不够可以设置值
set global max_allowed_packet 
```
插入的java代码使用prepareStatment 减少sql的解析，批量addBatch插入
```
  public static void main(String[] args) {

    SnowFlakeUtil snowFlakeUtil = SnowFlakeUtil.getFlowIdInstance();
    
    String insertSql = "insert into t_user(`id`,`login_id`,`passwd`,`status`,`create_user`,`create_date`,`update_user`,`update_date`) values (?,?,?,?,?,?,?,?)";


    PreparedStatement preparedStatement = null;
    Connection connection = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager
          .getConnection("jdbc:mysql://xxxx:3306/nacos?serverTimezone=UTC", "root",
              "Ydss");
      System.out.println(connection);
//      preparedStatement = connection.prepareStatement();
      //crate table
//      preparedStatement = connection.prepareStatement(createSql);
//      if (preparedStatement.execute()) {
//        System.out.println("create the table t_user");
//      }

      //设置非自动提交
      connection.setAutoCommit(false);
      //insert
      Long startLong = System.currentTimeMillis();
      System.out.println("the start time " +startLong);
      PreparedStatement preparedStatement1 = connection.prepareStatement(insertSql);
      for (int i = 0;i < 10000; i++) {
        preparedStatement1.setLong(1, snowFlakeUtil.nextId());
        preparedStatement1.setString(2, i+"");
        preparedStatement1.setString(3, "1");
        preparedStatement1.setInt(4, 1);
        preparedStatement1.setString(5, "1");
        preparedStatement1.setLong(6, System.currentTimeMillis());
        preparedStatement1.setString(7, "1");
        preparedStatement1.setLong(8, System.currentTimeMillis());
        preparedStatement1.addBatch();
        System.out.println(""+i);
      }
      preparedStatement1.executeBatch();
      connection.commit();
      Long endTime = System.currentTimeMillis();
      System.out.println("the end time " +endTime);
      System.out.println("the total time " + (endTime - startLong));
    } catch (Exception e) {
      try {
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }finally {
      try {
        connection.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }
}

```
输出
```
the start time 1620400091510
the end time 1620400245803
the total time 154293 
```
1w条数据大约154秒，可能是网络的问题吧，当然也是很慢的，
### 思路2
当然我们可以先去掉主键索引，然后再重构主键索引。
去掉主键索引
```
alter table t_user drop primary key;
```

输出
```
the start time 1620400547528
the end time 1620400676766
the total time 129238
```
稍微快了25秒

然后添加索引
```
alter table t_user add primary key(id);
```
### 思路3 
查询备份文件路径
show variables like '%secure%'
```
+--------------------------+-----------------------+
| Variable_name            | Value                 |
+--------------------------+-----------------------+
| require_secure_transport | OFF                   |
| secure_auth              | ON                    |
| secure_file_priv         | /var/lib/mysql-files/ |
+--------------------------+-----------------------+
```
导出文件到文件
```
 select * from t_user into outfile "/var/lib/mysql-files/e.sql" fields terminated by ',';
Query OK, 10000 rows affected (0.01 sec)
```
导出只需要了0.01s
然后删除数据的t_user的数据
```
truncate  `t_user`;
```
loadData 从svn 文件导入数据库，使用mysql load data命令
```
load data infile "/var/lib/mysql-files/e.sql" into table t_user fields terminated by ',';
Query OK, 10000 rows affected (0.18 sec)
Records: 10000  Deleted: 0  Skipped: 0  Warnings: 0
```
从e.sql文件中到入数据到本地库，使用，分割数据。
也可以先去掉索引这些，然后成功后再重建。
这里使用了0.18s

## 必做题9
题目
```
（必做）读写分离 - 动态切换数据源版本 1.0
```
### 思路1
通过实现AbstractRoutingDataSource的determineCurrentLookupKey方法，从上下文中读取，数据库连接。通过key value的形式，存放连接。然后dao在读取的时候是从线程的上下文读取连接。

对AbstractRoutingDataSource是实现
```

@Component("DynamicDataSource")
public class DynamicDataSource extends AbstractRoutingDataSource {

  public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
    super.setDefaultTargetDataSource(defaultTargetDataSource);
    super.setTargetDataSources(targetDataSources);
    super.afterPropertiesSet();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return DataSourceContextHolder.getDataSourceType();
  }

}
```
然后把连接存放到DataSourceContextHolder里面
```

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.default-datasource")
    public DataSource defaultDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.target-datasources.datasource1")
    public DataSource dataSource1() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean
    @Primary
    public DataSource dynamicDataSource(DataSource defaultDataSource, DataSource dataSource1) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.DEFAULT_DATASOURCE.name(), defaultDataSource);
        targetDataSources.put(DataSourceType.DATASOURCE1.name(), dataSource1);
        return new DynamicDataSource(defaultDataSource, targetDataSources);
    }
}

```
DataSourceContextHolder 的keyValue存放连接的上下文
```
ublic class DataSourceContextHolder {

    public static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    public static void setDataSourceType(String dsType) {
        CONTEXT_HOLDER.set(dsType);
    }

    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    public static void removeDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

}

public enum  DataSourceType {

    /** 默认数据源key */
    DEFAULT_DATASOURCE,

    /** 数据源1key*/
    DATASOURCE1,

}

```
使用的时候,需要手动的切换上下文的连接
```

@RestController
@RequestMapping("/test")
public class TestController {

  @Autowired
  private UserMapper userMapper;

  @GetMapping("/test")
  public List<UserDao> test(@RequestParam("index") int index) {
    if (index == 1) {
      DataSourceContextHolder.setDataSourceType(DataSourceType.DEFAULT_DATASOURCE.name());
    } else {
      DataSourceContextHolder.setDataSourceType(DataSourceType.DATASOURCE1.name());
    }
    List<UserDao> list = userMapper.select();
    DataSourceContextHolder.removeDataSourceType();
    return list;
  }
}
```
使用完成后从本地线程池中移除

## 必做题9
题目
```
（必做）读写分离 - 数据库框架版本 2.0
```
### 思路1
这里使用shardingsphere-jdbc,进行读写分离。不就行分库分表
配置 
```
#指定mybatis信息
mybatis.config-location=classpath:mybatis-config.xml

spring.shardingsphere.datasource.names=master,slave0
# 数据源 主库
spring.shardingsphere.datasource.master.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.master.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master.url=jdbc:mysql://localhost:3306/master?characterEncoding=utf-8
spring.shardingsphere.datasource.master.username=root
spring.shardingsphere.datasource.master.password=123456
# 数据源 从库
spring.shardingsphere.datasource.slave0.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.slave0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.slave0.url=jdbc:mysql://localhost:3306/slave?characterEncoding=utf-8
spring.shardingsphere.datasource.slave0.username=root
spring.shardingsphere.datasource.slave0.password=123456

# 读写分离
spring.shardingsphere.masterslave.load-balance-algorithm-type=round_robin
spring.shardingsphere.masterslave.name=ms
spring.shardingsphere.masterslave.master-data-source-name=master
spring.shardingsphere.masterslave.slave-data-source-names=slave0
#打印sql
spring.shardingsphere.props.sql.show=true

```
配置ds0和ds1两个数据库，使用随机读取算法，
然后是使用的时候直接使用mybatis使用就可以了
```
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * @Description: 保存用户
     */
    @PostMapping("save-user")
    public Object saveUser() {
        return userService.saveOne(new User("小小", "女", 3));
    }
    /**
     * @Description: 获取用户列表
     */
    @GetMapping("list-user")
    public Object listUser() {
        return userService.list();
    }
}
```

### 思路2
使用mycat 




