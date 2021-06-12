package com.example.demo.controller;

import apple.laf.JRSUIConstants.WindowClipCorners;
import com.example.demo.util.RedisLock;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RequestMapping("/test")
@RestController
public class TestController {

  private String key = "product1";

  @Autowired
  private Jedis jedis;

  @Autowired
  private RedisTemplate<String,String> redisTemplate;

  @RequestMapping("/lock")
  public boolean test() {
    return RedisLock.lock(jedis, "1", "1", 20000);
  }


  @RequestMapping("/unlock")
  public boolean unlock() {
    return RedisLock.unlock(jedis, "1", "1");
  }

  /**
   * 初始化库存
   * @param productId
   * @param num
   * @return
   */
  @RequestMapping("/set")
  public boolean incrt(@RequestParam("productId") String productId,@RequestParam("num") long num) {
    String incr = jedis.set(productId,num+"");
    if(incr != null && "OK".equals(incr)) {
      return true;
    }
    return false;
  }

  /**
   * 添加库存
   * @param productId
   * @return
   */
  @RequestMapping("/incrt")
  public boolean incrt(@RequestParam("productId") String productId) {
    Long incr = jedis.incr(productId);
    System.out.println(incr);
    return true;
  }

  /**
   * 减少库存
   * @return
   */
  @RequestMapping("/dec")
  public boolean dec(@RequestParam("productId") String productId) {
    Long incr = jedis.decr(productId);
    if (incr != null && incr > 0L) {
      System.out.println(incr);
      return true;
    }
    return false;
  }


  @RequestMapping("/storeIn")
  public boolean storeIn() {
    for (int i = 0; i < 10; i++) {
      jedis.lpush(key, i + "");
    }
    return true;
  }

  @RequestMapping("/con")
  public boolean con() {
    redisTemplate.opsForValue().set("aaa","1111");
    String aaa = redisTemplate.opsForValue().get("aaa");
    System.out.println(aaa);
    return true;
  }

  @RequestMapping("/out")
  public boolean out() {
    ExecutorService executor = Executors.newFixedThreadPool(30);
    for (int i = 0; i < 30; i++) {
     Future<String> future = executor.submit(new SpikeTask(i, jedis));
      String productId = null;
      try {
        productId = future.get();
        if (productId != null && productId.length() != 0) {
          System.out.println("顾客" +  productId + "号商品");
        } else {
          System.out.println("顾客" +  "没有抢到商品");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }

    System.out.println("抢完");
    return true;
  }


  class SpikeTask implements Callable<String> {

    private int customerId;

    private Jedis client;

    public SpikeTask(int customerId, Jedis jedisPool) {
      this.customerId = customerId;
      this.client = jedisPool;
    }

    @Override
    public String call() {

      String productId = client.lpop(key);
      return productId;
    }

  }
}
