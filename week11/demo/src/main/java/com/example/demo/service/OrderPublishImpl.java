package com.example.demo.service;

import io.lettuce.core.pubsub.RedisPubSubListener;
import org.springframework.stereotype.Service;

@Service
public class OrderPublishImpl implements RedisPubSubListener<String,String> {

  @Override
  public void message(String s, String s2) {
  System.out.println("message s:"+s + "conent:" + s2);
  }

  @Override
  public void message(String s, String k1, String s2) {
    System.out.println("message s:"+s +"k1" +k1+ "conent:" + s2);
  }

  @Override
  public void subscribed(String s, long l) {

  }

  @Override
  public void psubscribed(String s, long l) {

  }

  @Override
  public void unsubscribed(String s, long l) {

  }

  @Override
  public void punsubscribed(String s, long l) {

  }
}
