```
1.（选做）使 Java 里的动态代理，实现一个简单的 AOP。
2.（必做）写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 GitHub。
3.（选做）实现一个 Spring XML 自定义配置，配置一组 Bean，例如：Student/Klass/School。

4.（选做，会添加到高手附加题）
4.1 （挑战）讲网关的 frontend/backend/filter/router 线程池都改造成 Spring 配置方式；
4.2 （挑战）基于 AOP 改造 Netty 网关，filter 和 router 使用 AOP 方式实现；
4.3 （中级挑战）基于前述改造，将网关请求前后端分离，中级使用 JMS 传递消息；
4.4 （中级挑战）尝试使用 ByteBuddy 实现一个简单的基于类的 AOP；
4.5 （超级挑战）尝试使用 ByteBuddy 与 Instrument 实现一个简单 JavaAgent 实现无侵入下的 AOP。

5.（选做）总结一下，单例的各种写法，比较它们的优劣。
6.（选做）maven/spring 的 profile 机制，都有什么用法？
7.（选做）总结 Hibernate 与 MyBatis 的各方面异同点。
8.（必做）给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。
9.（选做）学习 MyBatis-generator 的用法和原理，学会自定义 TypeHandler 处理复杂类型。
10.（必做）研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：
1）使用 JDBC 原生接口，实现数据库的增删改查操作。
2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
3）配置 Hikari 连接池，改进上述操作。提交代码到 GitHub。

附加题（可以后面上完数据库的课再考虑做）：
(挑战) 基于 AOP 和自定义注解，实现 @MyCache(60) 对于指定方法返回值缓存 60 秒。
(挑战) 自定义实现一个数据库连接池，并整合 Hibernate/Mybatis/Spring/SpringBoot。
(挑战) 基于 MyBatis 实现一个简单的分库分表 + 读写分离 + 分布式 ID 生成方案。


```

### 第一道必做题 


3.0（必做）写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 GitHub。

#### 第一种从xml中加载

xml配置文件
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:aop="http://www.springframework.org/schema/aop"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-3.2.xsd
                        http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">


  <bean id="student" class="com.example.demo.Student">
    <property name="name" value="学生1">
    </property>
  </bean>


</beans>

```
java 实体类
```
public class Student {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

```
加载 classPathXmlApplication加载
```

public static void main(String[] args) {
  ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
  Student student = (Student) context.getBean("student");
  System.out.println(student.getName());
}

```
#### 第二种方法 
Bean注解
xml 配置
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:aop="http://www.springframework.org/schema/aop"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-3.2.xsd
                        http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">

	//配置扫描对象包路径
  <context:component-scan base-package="com.example.demo"/>

</beans>

```
在扫描包下添加configure, student的创建bean对象
```
@Component
public class BeanConfig {

  @Bean
  Student student() {
    Student student = new Student();
    student.setName("学生2");
    return student;
  }
}

```
使用 从ApplicationContext里面读取bean对象
```

public class Title3 {

  public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    Student student = (Student) context.getBean("student");
    System.out.println(student.getName());
  }

}

```


### 第二道必做题
8.给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。
新建model，然后在resource目录下，创建MATE-INF文件夹，然后在MATE-INF文件下，创建2个文件，分别是

spring.provides，和spring.factories

srping.provides内容
```
provides: testModel
```
spring.provides 定义当前starter的model的文件名字

spring.factories内容如下
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.demo.TestAutoConfiguration
```
这个TestAutoConfiguration文件就是SpringBoot加载这个starter需要启动的配置的类，也是这个starter的默认配置初始化。
TestAutoConfiguration 文件

```
@Configuration
@ComponentScan("com.example.demo")
@ConditionalOnProperty(prefix = "testModel.start",name = "enabled",havingValue = "true",matchIfMissing = true)
public class TestAutoConfiguration {

  @Bean(name = "student100")
  Student get() {
    Student student = new Student();
    return student;
  }
}

```
这个文件，初始化扫描com.example.demo包，也是初始化本地包的其他配置文件，ConditionalOnProperty定义是当引用包的需要配置前缀为testModel.start.enabled=true的时候这个starter才生效。然后就是下面的初始化bean操作。

### 第三道必做题
10.
```
研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：
1）使用 JDBC 原生接口，实现数据库的增删改查操作。
2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
3）配置 Hikari 连接池，改进上述操作。提交代码到 GitHub。
```
代码
1.使用JDBC 元素接口增删改查
```


public static void main(String[] args) {

  String createSql = "create table a (`id` int(0) not null, `name` varchar(20) default null ,`age` int(2) default 0,primary key(`id`))";

  String insertSql = "insert into a(`id`,`name`,`age`) values (1,'zhan',30)";
  String querySql = "select * from a where id = 1";
  String deleteSql = "delete from a where id = 1";
  String upateSql = "update a set name = 'dd' where id = 1";

  String dropSql = "drop table a ";
  Statement preparedStatement = null;
  Connection connection = null;
  ResultSet resultSet = null;
  try {
    Class.forName("com.mysql.jdbc.Driver");
    connection = DriverManager
        .getConnection("jdbc:mysql://localhost:3306/dubbo?serverTimezone=UTC", "root",
            "dsada!qazwsx");
    System.out.println(connection);
    preparedStatement = connection.createStatement();
    //crate table
    boolean createTable = preparedStatement.execute(createSql);
    if (createTable) {
      System.out.println("create the table a");
    }

    //insert
    int rows = preparedStatement.executeUpdate(insertSql);
    if (rows == 1) {
      System.out.println("insert into table a");
    }

    //query
    resultSet = preparedStatement.executeQuery(querySql);
    while (resultSet.next()) {
      System.out.println(resultSet.getString("name"));
    }
    // update
    int updateRows = preparedStatement.executeUpdate(upateSql);
    if (updateRows == 1) {
      System.out.println("update the table a");
    }
    //delete
    int delRows = preparedStatement.executeUpdate(deleteSql);
    if (delRows == 1) {
      System.out.println("delete the table a");
    }
    //query
    resultSet = preparedStatement.executeQuery(querySql);
    while (resultSet.next()) {
      System.out.println(resultSet.getString("name"));
    }
    //crate table
    boolean dropTable = preparedStatement.execute(dropSql);
    if (dropTable) {
      System.out.println("drop the table a");
    }
  } catch (Exception e) {
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
2.使用事务，PrepareStatement 方式，批处理方式，改进上述操作。

```

public static void main(String[] args) {

    String createSql = "create table a (`id` int(0) not null, `name` varchar(20) default null ,`age` int(2) default 0,primary key(`id`))";

    String insertSql = "insert into a(`id`,`name`,`age`) values (?,?,?)";
    String querySql = "select * from a where id =?";
    String deleteSql = "delete from a where id =?";
    String upateSql = "update a set name =? where id =?";

    String dropSql = "drop table a ";
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager
          .getConnection("jdbc:mysql://39.108.1.211:3306/dubbo?serverTimezone=UTC", "root",
              "Yandsds");
      System.out.println(connection);
//      preparedStatement = connection.prepareStatement();
      //crate table
      preparedStatement= connection.prepareStatement(createSql);
      if (preparedStatement.execute()) {
        System.out.println("create the table a");
      }

      //设置非自动提交
      connection.setAutoCommit(false);
      //insert
      PreparedStatement preparedStatement1  = connection.prepareStatement(insertSql);
      preparedStatement1.setInt(1,1);
      preparedStatement1.setString(2,"hp");
      preparedStatement1.setInt(3,30);
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
      preparedStatement3.setInt(2,1);
      preparedStatement3.setString(1,"dsds");
      int updateRows = preparedStatement3.executeUpdate();
      if (updateRows == 1) {
        System.out.println("update the table a");
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
//      int rowss = preparedStatemen6.execute();
      if (preparedStatemen6.execute()) {
        System.out.println("drop the table a");
      }
      connection.commit();
    } catch (Exception e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
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
主要是两点，一个是设置非自动提交，第二个是执行未出现问题，commit,出现异常，rollback()


3.配置 Hikari 连接池，改进上述操作。提交代码到 GitHub。
```

public class HikariTest {

  private static HikariDataSource initDataSource(String url, int jdbcPoolSize,String username,String passwd) {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
    config.setJdbcUrl(url);
    config.setAutoCommit(false);
    config.setUsername(username);
    config.setPassword(passwd);
    config.setConnectionTestQuery("SELECT 1;");
    config.setMaximumPoolSize(jdbcPoolSize);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setMaxLifetime(1000);
    config.setIdleTimeout(3000);
    return new HikariDataSource(config);
  }

  public static void main(String[] args) {


    HikariDataSource hikariDataSource = initDataSource("jdbc:mysql://39.108.1.211:3306/dubbo?serverTimezone=UTC",10,"root","Yandsds");

    String createSql = "create table a (`id` int(0) not null, `name` varchar(20) default null ,`age` int(2) default 0,primary key(`id`))";

    String insertSql = "insert into a(`id`,`name`,`age`) values (1,'zhan',30)";
    String querySql = "select * from a where id = 1";
    String deleteSql = "delete from a where id = 1";
    String upateSql = "update a set name = 'dd' where id = 1";

    String dropSql = "drop table a ";
    Statement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = hikariDataSource.getConnection();
      System.out.println(connection);
      preparedStatement = connection.createStatement();
      //crate table
      boolean createTable = preparedStatement.execute(createSql);
      if (createTable) {
        System.out.println("create the table a");
      }

      //insert
      int rows = preparedStatement.executeUpdate(insertSql);
      if (rows == 1) {
        System.out.println("insert into table a");
      }

      //query
      resultSet = preparedStatement.executeQuery(querySql);
      while (resultSet.next()) {
        System.out.println(resultSet.getString("name"));
      }
      // update
      int updateRows = preparedStatement.executeUpdate(upateSql);
      if (updateRows == 1) {
        System.out.println("update the table a");
      }
      //delete
      int delRows = preparedStatement.executeUpdate(deleteSql);
      if (delRows == 1) {
        System.out.println("delete the table a");
      }
      //query
      resultSet = preparedStatement.executeQuery(querySql);
      while (resultSet.next()) {
        System.out.println(resultSet.getString("name"));
      }
      //crate table
      boolean dropTable = preparedStatement.execute(dropSql);
      if (dropTable) {
        System.out.println("drop the table a");
      }
    } catch (Exception e) {
      try {
        resultSet.close();
        preparedStatement.close();
        connection.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }
}

```
这里主要是需要初始化hikariDataSource,然后在每个事务中都重新读取应该连接，但是我这里只使用了一个连接，其实本质是一样的。


	

