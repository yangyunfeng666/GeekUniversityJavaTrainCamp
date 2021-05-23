package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.vo.OrderVo;

public interface OrderService {

  IPage<OrderVo> queryPage(int size,int current);

  int add() throws Exception;

  int restTempAdd();
}
