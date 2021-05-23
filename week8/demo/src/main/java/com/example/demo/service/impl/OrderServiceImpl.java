package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.SnowFlakeUtil;
import com.example.demo.dao.OrderDo;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.OrderService;
import com.example.demo.vo.OrderVo;
import javax.annotation.Resource;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
  @ShardingTransactionType(TransactionType.XA)
  public int add() throws Exception {
    for (long i = 1 ;i < 10 ;i++) {
      OrderDo orderDo = new OrderDo();
      orderDo.setOrderId(snowFlakeUtil.nextId());
      orderDo.setUserId(i);
      orderDo.setUsername("张三"+i);
      //插入不同的库 user_id 不同，至少是2个库的数据
      int rows = orderMapper.insert(orderDo);
      if (i == 8) {
          //抛出异常
          throw new Exception("dsdsd");
      }
    }
    return 0;
  }


  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SnowFlakeUtil snowFlakeUtil;

  @Override
  @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
  @ShardingTransactionType(TransactionType.XA)
  public int restTempAdd() {
    for (long i = 1 ;i < 10 ;i++) {
      //插入不同的库 user_id 不同，至少是2个库的数据
      String insertSql = "INSERT INTO t_order(user_id,username) values (?,?)";
      long finalI = i;
      jdbcTemplate.execute(insertSql, (PreparedStatementCallback<Object>) ps -> {
        ps.setLong(1, finalI);
        ps.setString(2,"dasdasd");
        ps.executeUpdate();
        return 1;
      });
    }
    return 0;
  }
}
