


1.（选做）按照课程内容，动手验证 Hibernate 和 Mybatis 缓存。
2.（选做）使用 spring 或 guava cache，实现业务数据的查询缓存。
3.（挑战☆）编写代码，模拟缓存穿透，击穿，雪崩。
4.（挑战☆☆）自己动手设计一个简单的 cache，实现过期策略。
5.（选做）命令行下练习操作 Redis 的各种基本数据结构和命令。
6.（选做）分别基于 jedis，RedisTemplate，Lettuce，Redission 实现 redis 基本操作的 demo，可以使用 spring-boot 集成上述工具。
7.（选做）spring 集成练习:
	•	实现 update 方法，配合 @CachePut
	•	实现 delete 方法，配合 @CacheEvict
	•	将示例中的 spring 集成 Lettuce 改成 jedis 或 redisson
8.（必做）基于 Redis 封装分布式数据操作：
	•	在 Java 中实现一个简单的分布式锁；
	•	在 Java 中实现一个分布式计数器，模拟减库存。
9.（必做）基于 Redis 的 PubSub 实现订单异步处理
10.（挑战☆）基于其他各类场景，设计并在示例代码中实现简单 demo：
	•	实现分数排名或者排行榜；
	•	实现全局 ID 生成；
	•	基于 Bitmap 实现 id 去重；
	•	基于 HLL 实现点击量计数；
	•	以 redis 作为数据库，模拟使用 lua 脚本实现前面课程的外汇交易事务。
11.（挑战☆☆）升级改造项目：
	•	实现 guava cache 的 spring cache 适配；
	•	替换 jackson 序列化为 fastjson 或者 fst，kryo；
	•	对项目进行分析和性能调优。
12.（挑战☆☆☆）以 redis 作为基础实现上个模块的自定义 rpc 的注册中心。



### 选做题 1
```
（选做1）按照课程内容，动手验证 Hibernate 和 Mybatis 缓存。
```
 mybatis的xml配置
 ```
 <cache eviction="LRU" flushInterval="6000" size="500" readOnly="true"></cache>
 ```
 这样查询会在mybatis缓存数据。
 
### 选择题2
```
2.（选做）使用 spring 或 guava cache，实现业务数据的查询缓存。
``` 

添加guava maven配置
```
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
</dependency>
```



```
@Bean
public Cache cacheManager() {
    Cache<Long,UserDTO> cache
     = CacheBuilder.newBuilder()
        //设置并发级别为8，并发级别是指可以同时写缓存的线程数
        .concurrencyLevel(8)
        //设置写缓存后8秒钟过期
        .expireAfterWrite(8, TimeUnit.SECONDS)
        //设置缓存容器的初始容量为10
        .initialCapacity(10)
        //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
        .maximumSize(100).build();
    return cache;
}
```
存储用户id为key,用户对象为value.


### 必做题
```
8.（必做）基于 Redis 封装分布式数据操作：
	•	在 Java 中实现一个简单的分布式锁；
	•	在 Java 中实现一个分布式计数器，模拟减库存
```
	•	在 Java 中实现一个简单的分布式锁； 使用jedis实现
maven 依赖
```
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
</dependency>

```
加锁
```

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
  ```
  加锁使用set nx px 命令
  ```

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
```
解锁使用lua脚本，保证原子性。

测试
```
@Autowired
private Jedis jedis;

@RequestMapping("/lock")
public boolean test() {
  return RedisLock.lock(jedis,"1","1",20000);
}


@RequestMapping("/unlock")
public boolean unlock() {
  return RedisLock.unlock(jedis,"1","1");
}

```

#### 在 Java 中实现一个分布式计数器，模拟减库存

减库存，需要保证数据的一致性， 使用incr 和decr实现
```
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

```
使用redis的incr整形数据的原子性减少。

### 9.（必做）基于 Redis 的 PubSub 实现订单异步处理
如果是使用的jedis客户端 需要继承JedisPubSub 类，
```

public class OrderRedisSbscribe extends JedisPubSub {


  @Autowired
  private OrderService orderService;

  @Override
  public void onMessage(String channel, String message) {
    System.out.println("channel:" + channel + " message "+ message);
  
  	orderService.deal()    
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

  @Bean
    Jedis getRedis() {
        Jedis redis = new Jedis("xxxx");
        redis.auth("xxx");
        OrderRedisSbscribe sbscribe = new OrderRedisSbscribe();
	       redis.subscribe(sbscribe,"comment");
        redis.select(1);
        return redis;
    }


```
在jedis的subscribe 方法订阅channel和处理类


2.如果是lettunce的客户端，需要实现 RedisMessageListenerContainer,
```

@Bean
public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
    MessageListenerAdapter listenerAdapter){
  RedisMessageListenerContainer container = new RedisMessageListenerContainer();
  container.setConnectionFactory(connectionFactory);
  //订阅的channel 
  container.addMessageListener(listenerAdapter,new PatternTopic("comment"));
  return container;
}

@Bean
public MessageListenerAdapter listenerAdapter(MessageReceiver messageReceiver){
  //也有好几个重载方法，这边默认调用处理器的方法 叫handleMessage
  return new MessageListenerAdapter(messageReceiver);
}


```
然后在 MessageReceiver类实现MessageListener 方法
```


@Service
public class MessageReceiver implements MessageListener {

  @Autowired
  private OrderService orderService;

  @Override
  public void onMessage(Message message, byte[] bytes) {
    System.out.println("message :" + new String(message.getChannel()) +"body" +new String(message.getBody())  + " bytes" + new String(bytes));
  }
}

```









