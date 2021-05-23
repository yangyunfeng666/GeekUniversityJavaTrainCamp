package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dao.OrderDo;
import com.example.demo.vo.OrderVo;
import java.util.List;

public interface OrderMapper extends BaseMapper<OrderDo> {

  List<OrderVo> queryPage(IPage<OrderVo> page);

}
