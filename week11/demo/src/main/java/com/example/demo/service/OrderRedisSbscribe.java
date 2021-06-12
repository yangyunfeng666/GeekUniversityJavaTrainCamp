package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPubSub;

public class OrderRedisSbscribe extends JedisPubSub {


  @Autowired
  private OrderService orderService;

  @Override
  public void onMessage(String channel, String message) {
    System.out.println("channel:" + channel + " message "+ message);
    super.onMessage(channel, message);
  }

  @Override
  public void onSubscribe(String channel, int subscribedChannels) {
    System.out.println("channel:" + channel + " subscribedChannels "+ subscribedChannels);
    super.onSubscribe(channel, subscribedChannels);
  }

  @Override
  public void onUnsubscribe(String channel, int subscribedChannels) {
    System.out.println("channel:" + channel + " subscribedChannels "+ subscribedChannels);
    super.onUnsubscribe(channel, subscribedChannels);
  }
}
