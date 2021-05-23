package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.service.OrderService;
import com.example.demo.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private OrderService orderService;

  @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
  public IPage<OrderVo> queryPage(@RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "size",defaultValue = "20") int size) {
    return orderService.queryPage(size,page);
  }



  @RequestMapping(value = "/add", method = RequestMethod.POST)
  public int add(@RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "size",defaultValue = "20") int size)
      throws Exception {
    return orderService.add();
  }


  @RequestMapping(value = "/add1", method = RequestMethod.POST)
  public int add1(@RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "size",defaultValue = "20") int size) {
    return orderService.restTempAdd();
  }


}
