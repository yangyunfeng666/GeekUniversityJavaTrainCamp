# 毕业设计

### JVM
jvm的关键思考是和经验认识。
```
jvm是每个java应用启动的时候，创建的一个虚拟机。包含对象堆，线程栈，程序计数器，方法区，本地方法区。而堆分为年轻代和老年代，基于分带收集的架构，在不同的垃圾回收器下，进行不同的垃圾回收算法。线程栈是每个线程私有的栈，每个方法的调用会生成对应的栈帧，每个栈帧里面包含，本地变量表，返回值，class指针。程序计数器是下条待执行java字节码文件的序号，当执行本地方法的时候，没有值。方法区也是非堆，存储类型信息，和常量池，css压缩指针，codecache是jit编译后的代码缓存。也需要对gc各种特点和情况进行适当的调优，设置jvm启动参数，查看堆，线程，gc回收，日志，分析。
```

### NIO
```
Nio是区分不同的io模式的区别和实现。然后是具体的Netty的不同模式的实现。Netty的实现思想，也是采用分别处理接收和处理两个方面的优化。接收采用线程池，处理后在绑定到处理的线程池。处理的线程池让不同的NioEventLoop去处理不同的Hander。然后把事件处理都放到ChannelPipilenl里面，构成一个处理链。主要是理解Reactor模型，网络的拆包和粘包，网络的拥塞处理。
```

### 并发编程
```
多线程并发编程主要是解决多线程的可见性，有序行，原子性问题。既能使用多核的优势，也没有多核带来的安全性，准确性问题。java最常见的就是使用synchronized加锁，把一个代码块或者方法，添加锁，让多个线程读取锁的时候，是串行的，就不存在并发问题，但是串行导致性能的降低，而且存在不可打断，不能自动取消锁等问题，就引入了lock显示锁。比如ReentrenLock，都是解决Synchronized锁的确定设计，然后有设计了公平锁，可重入锁，读写锁。然后使用AbstractQueueSynchronizer的抽象队列，设计了CoutDownLatch，Semphere，CycleBarrier等工具类。针对多线程的方法结果的异步化设计了Future，FutrueTask，CompeleteTask 组合异步结果类。jdk针对不同的集合设计了可并发安全的集合数据结构，比如ConcurrentHashMap，CopyOnWriteArrayList，针对原来的集合添加Collection.Sysnchronzie工具包。
```
### Spring
```
Spring是如今web开发后台的事实标准，通过IOC解决对象之间的依赖问题，通过AOP字节码增强实现对象的代理，然后通过ApplicationContext管理所有的对象的生命周期。Spring boot通过嵌入tomcat等容器，Spring Start集成第三方包，自动化配置等，不断的演化成如今的生态。集成jdbc，实现JPA数据持久化能力，对Hibernate，mybatis等第三方的ORM支持，通过AOP+声明式事务管理事务。
```

### Mysql
```
mysql是支持事务的数据，有INnodb引擎提供。事务级别分为，读未提交，读以提交，可重复度，串行化。通过undo日志保证事务的一致性，同在事务的时候，通过MVCC机制，通过数据行存放的历史undo日志指针，回滚。在事务链上的数据事务ID保证可见性。通过redo日志保证事务的持久性，保证在事务提交后，在断点或者其他崩溃的情况下，重做执行日志得到最终的结果。

```
### 分库分表
```
分库分表从根本上解决数据库的IO问题和容量问题。但是要选取可行的中间件进行处理，把原本在一个数据的数据，分布在不同的库和表上，需要规划好表的分库分表规则。但是分库分表，有通过jdbc直接连接的，也有通过proxyer代理的，直接连接的性能损坏较小，proxyer代码侵入性好。分库分表后，需要考虑业务的影响，比如查询业务，事务一致性。针对事务，可以通过强一致性的XA事务，两阶段提交。也可以使用柔性事务，在保证最终一致性上，通过业务手段，来达到，以牺牲强一致性来换取性能。分布式柔性事务有Seaga，TCC。
```

###  微服务
```
微服务需要进行业务的差分，服务的粒度是高内聚，低耦合，从业务小的开始拆，参考DDD。但是随着服务的多，带来了部署，发布，监控，故障处理，问题，需要根据Devopts，自动化运维，监控，告警等基础设施。spring cloud算是微服务实现的一个事实标准，其中他通过不同的组件实现了，注册中心，配置中心，网关，网络请求，熔断限流，负载，认证，监控，日志等组件的实现。
```

### 分布式缓存
```
随着用户量的增加，读数据的读取，需要愈来愈快的响应速度，当我们需要把一些读多，写少的数据在海量并发读取的时候，就需要缓存来处理大量的并发读。通过用空间换时间的方式。redis就是这样的中间件，可以集群多副本的提供服务。但是我们也需要处理，缓存雪崩，缓存击穿，缓存穿透的情况。
```

### 分布式消息队列
```
分布式消息队列是解决业务异步化的问题。把业务处理抽象成，异步事件处理的管道，在管道中处理业务，通过消息组件，让不同的业务服务之间通过消息来达到数据流的流动。消息队列需要支持分布式，也是需要集群部署，高可用，高性能等特点。那么像kafka这个的中间件就满足条件，通过partion来分区不同的副本到不同的broker上部署，topic通过顺序写，提供吞吐。通过不同的确认模式来保证消息的可靠性投递，而且可用根据offset可用重复的消费消息，保证消息的幂等。
```







