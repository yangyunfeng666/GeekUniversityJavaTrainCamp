package com.example.demo.controller;

import com.example.demo.dao.UserDao;
import com.example.demo.datasource.DataSourceContextHolder;
import com.example.demo.datasource.DataSourceType;
import com.example.demo.mapper.UserMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
