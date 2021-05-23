package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dao.OrderDo;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.OrderService;
import com.example.demo.vo.OrderVo;
import java.util.Random;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Resource
  private OrderMapper orderMapper;

  @Override
  public IPage<OrderVo> queryPage(int size,int current) {
    IPage<OrderVo> page = new Page<>(current,size);
    page.setRecords(orderMapper.queryPage(page));
    return page;
  }

  @Override
  public int add() {
    Random random = new Random(100);
    for (int i = 0 ;i < 10 ;i++) {
      OrderDo orderDo = new OrderDo();
      orderDo.setUserId(random.nextLong());
      orderDo.setUsername("张三"+i);
    }
    return 0;
  }
}
