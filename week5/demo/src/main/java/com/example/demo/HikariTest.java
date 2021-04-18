package com.example.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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


    HikariDataSource hikariDataSource = initDataSource("jdbc:mysql://39.108.1.211:3306/dubbo?serverTimezone=UTC",10,"root","Y!qazwsx");

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
