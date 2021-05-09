package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrepareStatementBatchInsert {


  public static void main(String[] args) {

    SnowFlakeUtil snowFlakeUtil = SnowFlakeUtil.getFlowIdInstance();

    String insertSql = "insert into t_user(`id`,`login_id`,`passwd`,`status`,`create_user`,`create_date`,`update_user`,`update_date`) values (?,?,?,?,?,?,?,?)";


    Connection connection = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager
          .getConnection("jdbc:mysql://39.108.1.211:3306/nacos?serverTimezone=UTC", "root",
              "Yang123456!qazwsx");
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
      System.out.println("the end time " + endTime);
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
