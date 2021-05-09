package com.example.demo.mapper;

import com.example.demo.dao.UserDao;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {


  List<UserDao> select();

}
