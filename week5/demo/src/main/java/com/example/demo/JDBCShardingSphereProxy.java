package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCShardingSphereProxy {


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
              "Yang123456!qazwsx");
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
}
