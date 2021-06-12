package com.example.demo.util;

import java.util.Collections;
import redis.clients.jedis.Jedis;

public class RedisLock {

  private static final String LOCK_SUCCESS = "OK";
  private static final String SET_IF_NOT_EXIST = "NX";
  private static final String SET_WITH_EXPIRE_TIME = "PX";


  private static final Long RELEASE_SUCCESS = 1L;


  public static boolean lock(Jedis jedis, String lockKey, String requestId, int expireTime) {
    String result = jedis
        .set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
    if (result == null) return false;
    if (result.equals(LOCK_SUCCESS)) {
      return true;
    }
    return false;
  }

  public static boolean unlock(Jedis jedis, String lockKey, String requestId) {
    String luaStr = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
    Object result = jedis
        .eval(luaStr, Collections.singletonList(lockKey), Collections.singletonList(requestId));
    if (RELEASE_SUCCESS.equals(result)) {
      return true;
    }
    return false;
  }

}
