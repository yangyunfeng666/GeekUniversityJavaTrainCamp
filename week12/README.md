### 第12周作业
```
Week12 作业题目：
1.（必做）配置 redis 的主从复制，sentinel 高可用，Cluster 集群。
2.（选做）练习示例代码里下列类中的作业题: 08cache/redis/src/main/java/io/kimmking/cache/RedisApplication.java
3.（选做☆）练习 redission 的各种功能。
4.（选做☆☆）练习 hazelcast 的各种功能。
5.（选做☆☆☆）搭建 hazelcast 3 节点集群，写入 100 万数据到一个 map，模拟和演 示高可用。
6.（必做）搭建 ActiveMQ 服务，基于 JMS，写代码分别实现对于 queue 和 topic 的消息生产和消费，代码提交到 github。
7.（选做）基于数据库的订单表，模拟消息队列处理订单：
	•	一个程序往表里写新订单，标记状态为未处理 (status=0);
	•	另一个程序每隔 100ms 定时从表里读取所有 status=0 的订单，打印一下订单数据，然后改成完成 status=1；
	•	（挑战☆）考虑失败重试策略，考虑多个消费程序如何协作。
8.（选做）将上述订单处理场景，改成使用 ActiveMQ 发送消息处理模式。
9.（选做）使用 java 代码，创建一个 ActiveMQ Broker Server，并测试它。
10.（挑战☆☆）搭建 ActiveMQ 的 network 集群和 master-slave 主从结构。
11.（挑战☆☆☆）基于 ActiveMQ 的 MQTT 实现简单的聊天功能或者 Android 消息推送。
12.（挑战☆）创建一个 RabbitMQ，用 Java 代码实现简单的 AMQP 协议操作。
13.（挑战☆☆）搭建 RabbitMQ 集群，重新实现前面的订单处理。
14.（挑战☆☆☆）使用 Apache Camel 打通上述 ActiveMQ 集群和 RabbitMQ 集群，实现所有写入到 ActiveMQ 上的一个队列 q24 的消息，自动转发到 RabbitMQ。
15.（挑战☆☆☆）压测 ActiveMQ 和 RabbitMQ 的性能。

```
### 必做题1

1.（必做）配置 redis 的主从复制，sentinel 高可用，cluster 集群。

编辑2个redis.conf,一个是主的配置 redis6379.conf，一个是从的配置 redis6380.conf。
 redis6379.conf 配置
```
bind 127.0.0.1 -::1
protected-mode yes
port 6379
tcp-backlog 511
timeout 0
tcp-keepalive 300
daemonize no
pidfile "/var/run/redis_6379.pid"
loglevel notice
logfile ""
databases 16
always-show-logo no
set-proc-title yes
proc-title-template "{title} {listen-addr} {server-mode}"
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename "dump.rdb"
rdb-del-sync-files no
dir "/Users/yangyunfeng/Documents/soft/redis-6.2.0"
replica-serve-stale-data yes
replica-read-only yes
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-diskless-load disabled
repl-disable-tcp-nodelay no
replica-priority 100
acllog-max-len 128
lazyfree-lazy-eviction no
lazyfree-lazy-expire no
lazyfree-lazy-server-del no
replica-lazy-flush no
lazyfree-lazy-user-del no
lazyfree-lazy-user-flush no
oom-score-adj no
oom-score-adj-values 0 200 800
disable-thp yes
appendonly no
appendfilename "appendonly.aof"
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
aof-use-rdb-preamble yes
lua-time-limit 5000
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-size -2
list-compress-depth 0
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
hll-sparse-max-bytes 3000
stream-node-max-bytes 4kb
stream-node-max-entries 100
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
hz 10
dynamic-hz yes
aof-rewrite-incremental-fsync yes
rdb-save-incremental-fsync yes
jemalloc-bg-thread yes

```
启动 redis-server redis6379.conf 

 redis6380.conf 配置
 ```
 replica-serve-stale-data yes
replica-read-only yes
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-diskless-load disabled
repl-disable-tcp-nodelay no
replica-priority 100
acllog-max-len 128
lazyfree-lazy-eviction no
lazyfree-lazy-expire no
lazyfree-lazy-server-del no
replica-lazy-flush no
lazyfree-lazy-user-del no
lazyfree-lazy-user-flush no
oom-score-adj no
oom-score-adj-values 0 200 800
disable-thp yes
appendonly no
appendfilename "appendonly.aof"
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
aof-use-rdb-preamble yes
lua-time-limit 5000
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-size -2
list-compress-depth 0
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
hll-sparse-max-bytes 3000
stream-node-max-bytes 4kb
stream-node-max-entries 100
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
hz 10
dynamic-hz yes
aof-rewrite-incremental-fsync yes
rdb-save-incremental-fsync yes
jemalloc-bg-thread yes
 ```
启动 redis-server redis6380.conf

让redis6379是redis6380端口的主节点。需要登陆6380 执行如下命令
```
redis-cli -p 6380 
slaveof  127.0.0.1  6379
```
查看当前节点的状态
```
info
role:slave
master_host:127.0.0.1
master_port:6379
master_link_status:down
master_last_io_seconds_ago:-1
master_sync_in_progress:0
slave_repl_offset:49898
master_link_down_since_seconds:1623832425
slave_priority:100
slave_read_only:1
connected_slaves:0
master_failover_state:no-failover
master_replid:d871ab1db478274348569cc1e96211ba91c2bc65
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:49898
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:49898
```
看能不能设置值
```
set aaa 1
```
提示
```
error) READONLY You can't write against a read only replica
```
 登陆6379redis
 ```
 redis-cli -h 127.0.0.1 -p 6379
 
 ```
 设置值
 ```
 set aa 1
 ```
 然后在6380端口的redis上查看
 ```
 get aa
 ```
读取到了值是1.


#### sentinel 配置redis的主从切换
这里配置2个sentinel

一个sentinel配置的端口是26379 
```
port 26379
daemonize no
pidfile "/var/run/redis-sentinel.pid"
logfile ""
dir "/private/tmp"
sentinel monitor mymaster 127.0.0.1 6379 2
acllog-max-len 128
sentinel deny-scripts-reconfig yes
sentinel resolve-hostnames no
sentinel announce-hostnames no
protected-mode no
```
一个sentinel的端口是26380
```
port 26380
daemonize no
pidfile "/var/run/redis-sentinel.pid"
logfile ""
dir "/private/tmp"
sentinel monitor mymaster 127.0.0.1 6379 2
acllog-max-len 128
sentinel deny-scripts-reconfig yes
sentinel resolve-hostnames no
sentinel announce-hostnames no
protected-mode no
```
分别启动2个sentinel
```
redis-sentinel sentinel26379.conf 
redis-sentinel sentinel26380.conf 
```
然后杀掉6379redis。

查看日志
```
 +promoted-slave slave 127.0.0.1:6380 127.0.0.1 6380 @ mymaster 127.0.0.1 6379
17651:X 16 Jun 2021 18:05:15.631 # +failover-state-reconf-slaves master mymaster 127.0.0.1 6379
17651:X 16 Jun 2021 18:05:15.708 # +failover-end master mymaster 127.0.0.1 6379
17651:X 16 Jun 2021 18:05:15.708 # +switch-master mymaster 127.0.0.1 6379 127.0.0.1 6380
17651:X 16 Jun 2021 18:05:15.708 * +slave slave 127.0.0.1:6379 127.0.0.1 6379 @ mymaster 127.0.0.1 6380

```
切换6380为主节点。

在6380端口上查看角色信息
```
info
# Replication
role:master
connected_slaves:0
master_failover_state:no-failover
master_replid:5dd1e85159f79d47c174dc3d4fa653f522ccb898
master_replid2:50b8a43066fb875fe836d8ffb13c8964dd373bcf
master_repl_offset:69033
second_repl_offset:10433
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:69033
```

重启 6379端口的redis
```
redis-server redis6379.conf
```
查看角色信息
```
info
role:slave
master_host:127.0.0.1
master_port:6380
master_link_status:up
master_last_io_seconds_ago:1
master_sync_in_progress:0
slave_repl_offset:75468
slave_priority:100
slave_read_only:1
connected_slaves:0
master_failover_state:no-failover
master_replid:5dd1e85159f79d47c174dc3d4fa653f522ccb898
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:75468
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:72625
repl_backlog_histlen:2844
```
自从sentinel能主动切换主从。


#### cluster配置

修改修改redis6379.conf的 添加集群配置
```
cluster-enabled yes
```
同样redis6380.conf 添加配置
```
cluster-enabled yes
```
同样redis6381.conf 添加配置
```
cluster-enabled yes
```

启动三个redis
```
需要先删除配置存储文件里面各个文件的存储文件和node.conf文件
redis-server redis6380.conf 
redis-server redis6379.conf 
redis-server redis6381.conf 
```
添加集群
```
redis-cli -p 6379

cluster meet 127.0.0.1 6381  127.0.0.1 6380

```
查看集群状态
```
redis-cli -p 6379
cluster nodes

cluster nodes
0c4b69d489a131f6baa6264a604aca8d142e833e 127.0.0.1:6380@16380 master - 0 1623902614480 4 connected
5e67fd72cb271e3d514db96192c8c1dc9c3e949a 127.0.0.1:6379@16379 myself,master - 0 1623902613000 3 connected
6d85b71035a79af5ff0b6bc0029e4bb9f2426368 127.0.0.1:6381@16381 master - 0 1623902615507 0 connected 1180 8740 10439
```

编辑shell。分配卡槽
```
vim add_alots.sh
start=$1
end=$2
port=$3

for slot in `seq ${start} ${end}`
do
    echo "slot:${slot}"
    redis-cli -p ${port} cluster addslots ${slot}
done 
```
给三个redis分配卡槽 
```
sh add_alots.sh 0 5462 6379
sh add_alots.sh 5463 10922 6380
sh add_alots.sh 10923 16383 6381
```
查看cluster状态
```
cluster nodes
0c4b69d489a131f6baa6264a604aca8d142e833e 127.0.0.1:6380@16380 myself,master - 0 1623902845000 4 connected 5463-8739 8741-10438 10440-10922
6d85b71035a79af5ff0b6bc0029e4bb9f2426368 127.0.0.1:6381@16381 master - 0 1623902847163 0 connected 1180 8740 10439 10923-16383
5e67fd72cb271e3d514db96192c8c1dc9c3e949a 127.0.0.1:6379@16379 master - 0 1623902848185 3 connected 0-1179 1181-5462

```
可以在任何节点 但是需要以集群的模式登陆  redis-cli -c
```
MacBook-Pro:redis-6.2.0 yangyunfeng$ redis-cli -c -p 6379
127.0.0.1:6379> get dsds
-> Redirected to slot [7009] located at 127.0.0.1:6380
"aa"
127.0.0.1:6380> set aa
(error) ERR wrong number of arguments for 'set' command
127.0.0.1:6380> set aa bb
-> Redirected to slot [1180] located at 127.0.0.1:6381
OK
127.0.0.1:6381> get aa bb
(error) ERR wrong number of arguments for 'get' command
127.0.0.1:6381> get aa 
"bb"

```

redis cluster 集群命令有
```
集群
cluster info ：打印集群的信息
cluster nodes ：列出集群当前已知的所有节点（ node），以及这些节点的相关信息。
节点
cluster meet <ip> <port> ：将 ip 和 port 所指定的节点添加到集群当中，让它成为集群的一份子。
cluster forget <node_id> ：从集群中移除 node_id 指定的节点。
cluster replicate <master_node_id> ：将当前从节点设置为 node_id 指定的master节点的slave节点。只能针对slave节点操作。
cluster saveconfig ：将节点的配置文件保存到硬盘里面。
槽(slot)
cluster addslots <slot> [slot ...] ：将一个或多个槽（ slot）指派（ assign）给当前节点。
cluster delslots <slot> [slot ...] ：移除一个或多个槽对当前节点的指派。
cluster flushslots ：移除指派给当前节点的所有槽，让当前节点变成一个没有指派任何槽的节点。
cluster setslot <slot> node <node_id> ：将槽 slot 指派给 node_id 指定的节点，如果槽已经指派给
另一个节点，那么先让另一个节点删除该槽>，然后再进行指派。
cluster setslot <slot> migrating <node_id> ：将本节点的槽 slot 迁移到 node_id 指定的节点中。
cluster setslot <slot> importing <node_id> ：从 node_id 指定的节点中导入槽 slot 到本节点。
cluster setslot <slot> stable ：取消对槽 slot 的导入（ import）或者迁移（ migrate）。
键
cluster keyslot <key> ：计算键 key 应该被放置在哪个槽上。
cluster countkeysinslot <slot> ：返回槽 slot 目前包含的键值对数量。
cluster getkeysinslot <slot> <count> ：返回 count 个 slot 槽中的键
```

#### 必做题2
```
6.（必做）搭建 ActiveMQ 服务，基于 JMS，写代码分别实现对于 queue 和 topic 的消息生产和消费，代码提交到 github。
```
activeMq maven配置
```

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>

```

application.properties配置
```

spring.activemq.broker-url=tcp://127.0.0.1:61616
spring.activemq.user=admin
spring.activemq.password=admin
spring.activemq.pool.enabled=true
spring.activemq.pool.max-connections=50

spring.jms.cache.session-cache-size=5

```

ActiveMQ config 配置
```

@Configuration
@EnableJms
public class ActiveMqConfig {


  @Value("${spring.activemq.broker-url}")
  private String brokerUrl;

  @Value("${spring.activemq.user}")
  private String username;

  @Value("${spring.activemq.password}")
  private String password;


  @Bean
  public ConnectionFactory connectionFactory(){
    return new ActiveMQConnectionFactory(username, password, brokerUrl);
  }

  @Bean
  public JmsMessagingTemplate jmsMessageTemplate(){
    return new JmsMessagingTemplate(connectionFactory());
  }

  @Bean
  public JmsTemplate JmsTemplate(){
    return new JmsTemplate(connectionFactory());
  }


  // 在Queue模式中，对消息的监听需要对containerFactory进行配置
  @Bean("queueListener")
  public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(ConnectionFactory connectionFactory){
    SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPubSubDomain(false);
    return factory;
  }

  //在Topic模式中，对消息的监听需要对containerFactory进行配置
  @Bean("topicListener")
  public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(ConnectionFactory connectionFactory){
    SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPubSubDomain(true);
    return factory;
  }


```

消息的生产者 发送方
```

@RequestMapping("/test")
@RestController()
public class TestController {

  @Autowired
  private JmsTemplate jmsTemplate;

  @RequestMapping(method = RequestMethod.GET,value = "/sendQueue")
  public String send(@RequestParam("str") String str) {
    Destination destination = new ActiveMQQueue("aa.queue");
    jmsTemplate.send(destination, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(str);
      }
    });
    return "true";
  }

  @RequestMapping("/sendTopic")
  public String queue(@RequestParam("str") String str) {
    Destination destination = new ActiveMQTopic("aa.topic");
    jmsTemplate.send(destination, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(str);
      }
    });
    return "true";
  }
```
这里都是通过jmsTemplate对象，发送的对象text.

消费放监听配置
```
@Component
public class JmsServiceImpl {

  @JmsListener(destination = "aa.topic",containerFactory = "topicListener")
  public void receiver(String content) {
    System.out.println("aa.topic:" + content);
  }

  @JmsListener(destination = "aa.queue",containerFactory = "queueListener")
  public void queue(String content) {
    System.out.println("aa.queue:" + content);
  }

```
通过注解@JmsListener的配置 destionation的值是不同的topic或者queue的名字，配置不会的监听工厂。










	

