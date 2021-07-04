# 第13周题
```
1.（必做）搭建一个 3 节点 Kafka 集群，测试功能和性能；实现 spring kafka 下对 kafka 集群的操作，将代码提交到 github。
2.（选做）安装 kafka-manager 工具，监控 kafka 集群状态。
3.（挑战☆）演练本课提及的各种生产者和消费者特性。
4.（挑战☆☆☆）Kafka 金融领域实战：在证券或者外汇、数字货币类金融核心交易系统里，对于订单的处理，大概可以分为收单、定序、撮合、清算等步骤。其中我们一般可以用 mq 来实现订单定序，然后将订单发送给撮合模块。
	•	收单：请实现一个订单的 rest 接口，能够接收一个订单 Order 对象；
	•	定序：将 Order 对象写入到 kafka 集群的 order.usd2cny 队列，要求数据有序并且不丢失；
	•	撮合：模拟撮合程序（不需要实现撮合逻辑），从 kafka 获取 order 数据，并打印订单信息，要求可重放, 顺序消费, 消息仅处理一次。
5.（选做）自己安装和操作 RabbitMQ，RocketMQ，Pulsar，以及 Camel 和 Spring Integration。
6.（必做）思考和设计自定义 MQ 第二个版本或第三个版本，写代码实现其中至少一个功能点，把设计思路和实现代码，提交到 GitHub。
7.（挑战☆☆☆☆☆）完成所有其他版本的要求。期限一年。
```

### 必做题
```
1.（必做）搭建一个 3 节点 Kafka 集群，测试功能和性能；实现 spring kafka 下对 kafka 集群的操作，将代码提交到 github。
```

1.下载地址
```
http://kafka.apache.org/downloads
```
2.启动kafka
```
vim  config/server.properties
```
放开
```
listeners=PLAINTEXT://localhost:9092
```
3.启动zookeeper
```
sh bin/zookeeper-server-start.sh config/zookeeper.properties

sh bin/kafka-server-start.sh config/server.properties
```
集群配置文件
```
kafka9091.properties kafka9092.properties kafka9093.properties
```
vim kafka9091.properties
```
broker.id=1
listeners=PLAINTEXT://:9091
broker.list=localhost:9091,localhost:9092,localhost:9093
log.dirs=/tmp/kafka/kafka-logs1

num.network.threads=3

num.io.threads=8

socket.send.buffer.bytes=102400

socket.receive.buffer.bytes=102400

socket.request.max.bytes=104857600

num.partitions=1

num.recovery.threads.per.data.dir=1

offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=18000
group.initial.rebalance.delay.ms=0
```

vim kafka9092.properties
```
broker.id=2
listeners=PLAINTEXT://:9092
broker.list=localhost:9091,localhost:9092,localhost:9093
log.dirs=/tmp/kafka/kafka-logs2

num.network.threads=3

num.io.threads=8

socket.send.buffer.bytes=102400

socket.receive.buffer.bytes=102400

socket.request.max.bytes=104857600

num.partitions=1

num.recovery.threads.per.data.dir=1

offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=18000
group.initial.rebalance.delay.ms=0
```

vim kafka9093.properties
```
broker.id=3
listeners=PLAINTEXT://:9093
broker.list=localhost:9091,localhost:9092,localhost:9093
log.dirs=/tmp/kafka/kafka-logs3

num.network.threads=3

num.io.threads=8

socket.send.buffer.bytes=102400

socket.receive.buffer.bytes=102400

socket.request.max.bytes=104857600

num.partitions=1

num.recovery.threads.per.data.dir=1

offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=18000
group.initial.rebalance.delay.ms=0
```
创建日志文件夹
```
mkdir /tmp/kafka/kafka-logs3 /tmp/kafka/kafka-logs2  /tmp/kafka/kafka-logs1
```

zookeeper 清除非必要的数据 1.直接删除数据文件
```
查看zookeeper的datadir,删除低下的数据
```
2.使用zoomInspector
```
java -jar zoomInspector.jar
手动删除，除了zookeeper之外的所有节点。
```
启动zookeeper 和 kafka集群
```
./bin/zookeeper-server-start.sh config/zookeeper.properties 

./bin/kafka-server-start.sh config/kafka9091.properties

./bin/kafka-server-start.sh config/kafka9092.properties

./bin/kafka-server-start.sh config/kafka9093.properties
```


执行创建topic而切有2个副本
```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test32 --partitions 3 --replication-factor 2
```
查看topic列表
```
bin/kafka-topics.sh --zookeeper localhost:2181 --list
```

查看具体的topic
```
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic test32

Topic: test32	PartitionCount: 3	ReplicationFactor: 2	Configs: 
	Topic: test32	Partition: 0	Leader: 2	Replicas: 2,3	Isr: 2,3
	Topic: test32	Partition: 1	Leader: 3	Replicas: 3,1	Isr: 3,1
	Topic: test32	Partition: 2	Leader: 1	Replicas: 1,2	Isr: 1,2
```

生产者
```
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test32
```

消费者
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9091 --topic test32 --frombeginning
```

执行生产性能测试

```
bin/kafka-producer-perf-test.sh --topic test32 --num-records 100000 --record-size 1000 --throughput 200000 --producer-props bootstrap.servers=localhost:9092

100000 records sent, 16733.601071 records/sec (15.96 MB/sec), 1451.23 ms avg latency, 2584.00 ms max latency, 1446 ms 50th, 2240 ms 95th, 2498 ms 99th, 2578 ms 99.9th

```
发送100000个数据，每秒16733个

执行消费性能测试
```

bin/kafka-consumer-perf-test.sh --bootstrap-server localhost:9091 --topic test32 --fetch-size 1048576 --messages 100000 --threads 1

start.time, end.time, data.consumed.in.MB, MB.sec, data.consumed.in.nMsg, nMsg.sec, rebalance.time.ms, fetch.time.ms, fetch.MB.sec, fetch.nMsg.sec
2021-07-03 16:47:23:514, 2021-07-03 16:47:24:798, 95.3674, 74.2737, 100006, 77886.2928, 1625302044067, -1625302042783, -0.0000, -0.0001
```
总消费了100006个，平均77886。


spring boot 配置 kafaka
maven配置
```
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```
配置ProductorFactor 和 Topic
```


@EnableKafka
public class KafkaConfig {

  @Bean
  public Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,1);//顺序发送
    props.put(ProducerConfig.ACKS_CONFIG,"all");
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);//可靠性配置 默认ack = all/-1
    props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"transactionId");//开启事务配置事务ID
    // See https://kafka.apache.org/documentation/#producerconfigs for more properties
    return props;
  }

  @Bean
  public ProducerFactory<Integer, String> producerFactory() {
    DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory<>(producerConfigs());
    factory.setTransactionIdPrefix("tx000");
    factory.transactionCapable();
    return factory;
  }


  @Bean
  public KafkaTemplate<Integer, String> kafkaTemplate() {
    return new KafkaTemplate<Integer, String>(producerFactory());
  }

  @Bean
  public NewTopic topic4() {
    return TopicBuilder.name("defaultBoth")
        .build();
  }

	//定义topic 分区 副本数
  @Bean
  public NewTopic topic5() {
    return TopicBuilder.name("defaultPart")
        .partitions(3)
        .replicas(2)
        .build();
  }

  @Bean
  public NewTopic topic6() {
    return TopicBuilder.name("defaultRepl")
        .partitions(3)
        .replicas(2)
        .build();
  }
}
```
设置消费者
```

// 消费监听
@KafkaListener(topics = {"defaultPart"},groupId = "aaa")
public void onMessage1(ConsumerRecord<?, ?> record){
  // 消费的哪个topic、partition的消息,打印出消息内容
  System.out.println("简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
}

```

普通发送
```
@RequestMapping("/send/{str}")
public String product(@PathVariable String str) {
  template.send("defaultPart",str);
  return "true";
}
```
同步发送
```
/**
 * 同步发送
 * @param str
 * @return
 * @throws ExecutionException
 * @throws InterruptedException
 */
@RequestMapping("/sendAsyn/{str}")
public String sendAsyn(@PathVariable String str) throws ExecutionException, InterruptedException {
  ProducerRecord producerRecord = new ProducerRecord("defaultPart",str);
  Future<RecordMetadata> send = producerFactory.createProducer().send(producerRecord);
  RecordMetadata recordMetadata =  send.get();
  System.out.println(recordMetadata.toString());
  return "true";
}
```

可靠性消息发送
```
/**
 * 可靠发送
 * @param str
 * @return
 * @throws ExecutionException
 * @throws InterruptedException
 */
@RequestMapping("/transSend/{str}")
public String transSend(@PathVariable String str) throws ExecutionException, InterruptedException {

  Properties properties = new Properties();
  properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      StringSerializer.class.getName());
  properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      StringSerializer.class.getName());
  properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
  properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "trans");
  properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);
  KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

  ProducerRecord producerRecord = new ProducerRecord("defaultPart",str);

  producer.initTransactions();
  producer.beginTransaction();
  try {
    Future<RecordMetadata> send = producer.send(producerRecord, (RecordMetadata recordMetadata, Exception e) -> {
            if (e != null) {
              producer.abortTransaction();;
            }
          }
        );
    RecordMetadata recordMetadata = send.get();
    System.out.println(recordMetadata.topic() + ":" + recordMetadata.partition() + ":" + recordMetadata.offset() );
  } catch (Exception e) {
      producer.abortTransaction();
  }
  return "true";
}
```

### 6.（必做）思考和设计自定义 MQ 第二个版本或第三个版本，写代码实现其中至少一个功能点，把设计思路和实现代码，提交到 GitHub。

设计思路
```
1.定义一个可变长队列数据结构，比如数据项项目是ArrayList。
2.针对客户端读取队列，有维护改客户端，已经读取的数据offset.和确认数据ack的下标。
3.每次客户端读去数据和收到数据确认都需要维护offset和ack的数据值
```
没有实现，后面有时间在想想，怎么实现。






