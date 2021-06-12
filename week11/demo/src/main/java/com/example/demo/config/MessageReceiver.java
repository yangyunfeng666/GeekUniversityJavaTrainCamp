package com.example.demo.config;

import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver implements MessageListener {

  @Autowired
  private OrderService orderService;

  @Override
  public void onMessage(Message message, byte[] bytes) {
    System.out.println("message :" + new String(message.getChannel()) +"body" +new String(message.getBody())  + " bytes" + new String(bytes));
  }
}
