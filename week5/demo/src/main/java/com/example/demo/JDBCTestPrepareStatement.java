package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCTestPrepareStatement {


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
              "Yang123456!qazwsx");
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
}
