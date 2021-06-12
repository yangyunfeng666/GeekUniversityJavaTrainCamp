package com.example.demo;

import com.example.demo.service.OrderPublishImpl;
import com.example.demo.service.OrderRedisSbscribe;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@SpringBootApplication
@ComponentScans( {@ComponentScan("com.example.demo")})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }



    @Bean
    Jedis getRedis() {
        Jedis redis = new Jedis("r-.redis.rds.aliyuncs.com");
        redis.auth("");
//        OrderRedisSbscribe sbscribe = new OrderRedisSbscribe();
//        redis.subscribe(sbscribe,"comment");
        redis.select(1);
        return redis;
    }


}
