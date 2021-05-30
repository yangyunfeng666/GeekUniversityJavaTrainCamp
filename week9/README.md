# 第9周题目
```
1.（选做）实现简单的 Protocol Buffer/Thrift/gRPC(选任一个) 远程调用 demo。
2.（选做）实现简单的 WebService-Axis2/CXF 远程调用 demo。
3.（必做）改造自定义 RPC 的程序，提交到 GitHub：
	•	尝试将服务端写死查找接口实现类变成泛型和反射；
	•	尝试将客户端动态代理改成 AOP，添加异常处理；
	•	尝试使用 Netty+HTTP 作为 client 端传输方式。
4.（选做☆☆））升级自定义 RPC 的程序：
	•	尝试使用压测并分析优化 RPC 性能；
	•	尝试使用 Netty+TCP 作为两端传输方式；
	•	尝试自定义二进制序列化；
	•	尝试压测改进后的 RPC 并分析优化，有问题欢迎群里讨论；
	•	尝试将 fastjson 改成 xstream；
	•	尝试使用字节码生成方式代替服务端反射。
5.（选做）按课程第二部分练习各个技术点的应用。
6.（选做）按 dubbo-samples 项目的各个 demo 学习具体功能使用。
7.（必做）结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub:
	•	用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
	•	用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
	•	设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。
8.（挑战☆☆）尝试扩展 Dubbo
	•	基于上次作业的自定义序列化，实现 Dubbo 的序列化扩展 ;
	•	基于上次作业的自定义 RPC，实现 Dubbo 的 RPC 扩展 ;
	•	在 Dubbo 的 filter 机制上，实现 REST 权限控制，可参考 dubbox;
	•	实现一个自定义 Dubbo 的 Cluster/Loadbalance 扩展，如果一分钟内调用某个服务 / 提供者超过 10 次，则拒绝提供服务直到下一分钟 ;
	•	整合 Dubbo+Sentinel，实现限流功能 ;
	•	整合 Dubbo 与 Skywalking，实现全链路性能监控。

```
## 必做题1：
```
3.（必做）改造自定义 RPC 的程序，提交到 GitHub：
	•	尝试将服务端写死查找接口实现类变成泛型和反射；
	•	尝试将客户端动态代理改成 AOP，添加异常处理；
	•	尝试使用 Netty+HTTP 作为 client 端传输方式。
```
#### 尝试将服务端写死查找接口实现类变成泛型和反射；

需要改写RpcfxResolver 的查询接口和实现类
```
public interface RpcfxResolver {
    <T> T resolve(Class<T> serviceClass);
}
```
实现类的
```

public class DemoResolver implements RpcfxResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

//    @Override
//    public Object resolve(String serviceClass) {
//        return this.applicationContext.getBean(serviceClass);
//    }

    @Override
    public <T> T resolve(Class<T> serviceClass) {
        return this.applicationContext.getBean(serviceClass);
    }
}
```
调用的时候使用
```
// 作业1：改成泛型和反射
Object service = null;//this.applicationContext.getBean(serviceClass);
try {
    service = resolver.resolve(Class.forName(serviceClass));
} catch (ClassNotFoundException e) {
    e.printStackTrace();
}
```

#### 尝试将客户端动态代理改成 AOP，添加异常处理；

使用cglib字节码增强代理
实现MethodInterceptor 的字节码增加回调方法。
```

package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.Filter;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServiceMethodInterceptor implements MethodInterceptor {

  public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

  private final String url;

  private final Filter[] filters;

  public ServiceMethodInterceptor(String url, Filter[] filters) {
    this.url = url;
    this.filters = filters;
  }

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
      throws Throwable {


    RpcfxRequest request = new RpcfxRequest();
    String className = o.getClass().getName();
    String[] s = className.split("\\$\\$");

    request.setServiceClass(s[0]);
    request.setMethod(method.getName());
    request.setParams(objects);

    if (null!=filters) {
      for (Filter filter : filters) {
        if (!filter.filter(request)) {
          return null;
        }
      }
    }

    RpcfxResponse response = post(request, url);

    // 加filter地方之三
    // Student.setTeacher("cuijing");

    // 这里判断response.status，处理异常
    // 考虑封装一个全局的RpcfxException

    return JSON.parse(response.getResult().toString());
  }


  private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
    String reqJson = JSON.toJSONString(req);
    System.out.println("req json: "+reqJson);

    // 1.可以复用client
    // 2.尝试使用httpclient或者netty client
    OkHttpClient client = new OkHttpClient();
    final Request request = new Request.Builder()
        .url(url)
        .post(RequestBody.create(JSONTYPE, reqJson))
        .build();
    String respJson = client.newCall(request).execute().body().string();
    System.out.println("resp json: "+respJson);
    return JSON.parseObject(respJson, RpcfxResponse.class);
  }
}


```
在调用的时候替换原来的动态代理
```

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {

        // 0. 替换动态代理 -> AOP
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new ServiceMethodInterceptor(url,filters));
        return (T) enhancer.create();
//        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url, filters));

    }

```

#### 尝试使用 Netty+HTTP 作为 client 端传输方式。

使用Netty替换的发起请求的Okhttp请求。 需要配置Netty使用Post提交，改写OKhttp的逻辑，暂时没有时间做了
```

```

## 必做题2
```
7.（必做）结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub:
	•	用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
	•	用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
	•	设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。
```
hmily maven引用
```

<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>hmily-spring-boot-starter-dubbo</artifactId>
    <version>2.1.1</version>
</dependency>


```

api定义
```
public interface AccountService {

  /**
   * 给B
   * @param userB 用户B
   * @param userA 用户A
   * @param rmd 兑换的人民币
   */
  void accout(Long userB,Long userA,int rmd);

}

```
表设计
```
CREATE TABLE `test`.`account` (
	`id` int(11) NOT NULL,
	`username` varchar(20) DEFAULT NULL,
	`rmd` int(10) DEFAULT 0,
	`doller` int(10),
	PRIMARY KEY (`id`)
) ENGINE = 'innodb' AUTO_INCREMENT=1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=DYNAMIC COMMENT='' CHECKSUM=0 DELAY_KEY_WRITE=0;


CREATE TABLE `test`.`freeze_account` (
	`id` int(11) NOT NULL,
	`username` varchar(20) DEFAULT NULL,
	`rmd` int(10) DEFAULT 0,
	`doller` int(10),
	PRIMARY KEY (`id`)
) ENGINE = 'innodb' AUTO_INCREMENT=1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=DYNAMIC COMMENT='' CHECKSUM=0 DELAY_KEY_WRITE=0;
```
account表和冻结freeze_account表的表结构是一样的。而且需要在创建一个账号的时候，在两个表里面添加相同的用户id, 当然我们可以使用业务去处理。但是我现在是理想的情况下，预插入了2个表。这样逻辑更简单
```


insert into account(id,username,rmd,doller) values(1,"A",0,0);

insert into account(id,username,rmd,doller) values(2,"B",0,0);



insert into freeze_account(id,username,rmd,doller) values(1,"A",0,0);

insert into freeze_account(id,username,rmd,doller) values(2,"B",0,0);
```


使用tcc的主要预留和处理
```
服务提供端配置如下

@DubboService
public class AccountServiceImpl implements AccountService {

  @Resource
  AccountMapper accountMapper;

  @Resource
  FreezeAccountMapper freezeAccountMapper;

  /**
   * b账号把rmd 兑换成美元，而A账号把相应的美元兑换成人民币
   * @param userB 用户B
   * @param userA 用户A
   * @param doller 兑换的美元
   */
  @Override
  @HmilyTCC(confirmMethod = "accoutConfirm",cancelMethod = "accoutCancel")
  public void accout(Long userB, Long userA, int doller) {
    //预留资源 1.B账号 把rmd 减7，而 A账号的美元也减1美元
    accountMapper.accountRmdDel(userB,7 * doller);
    freezeAccountMapper.accountRmdAdd(userB,7*doller);
    accountMapper.accountDollerDel(userA,1 * doller);
    freezeAccountMapper.accountDollerAdd(userA,1 * doller);

  }

  public void accoutConfirm(Long userB, Long userA, int doller) {
    //提交 1.A账号 把rmd 加7，而 B账号的美元加1美元
    accountMapper.accountRmdAdd(userA,7 * doller);
    freezeAccountMapper.accountRmdDel(userA,7 * doller);
    accountMapper.accountDollerAdd(userB,1 * doller);
    freezeAccountMapper.accountDollerDel(userB,1 * doller);
  }

  public void accoutCancel(Long userB, Long userA, int doller) {
    //取消 1.B账号 把rmd 加7，而 A账号的美元加1美元
    accountMapper.accountRmdAdd(userB,7 * doller);
    freezeAccountMapper.accountRmdDel(userB,7 * doller);
    accountMapper.accountDollerAdd(userA,1 * doller);
    freezeAccountMapper.accountDollerDel(userA,1 * doller);
  }

}

```
AccountMapper.java
```

@Mapper
public interface AccountMapper {

    /**
     * 用户人民币账号增加
     * @param id
     * @param count
     * @return
     */
    int accountRmdAdd(@Param("id") Long id,@Param("count") int count);

    /**
     * 用户人民币账号减少
     * @param id
     * @param count
     * @return
     */
    int accountRmdDel(@Param("id") Long id,@Param("count") int count);

    /**
     * 用户美元账号增加
     * @param id
     * @param count
     * @return
     */
    int accountDollerAdd(@Param("id") Long id,@Param("count") int count);

    /**
     * 用户美元账号减少
     * @param id
     * @param count
     * @return
     */
    int accountDollerDel(@Param("id") Long id,@Param("count") int count);

}

```
		
AccountMapper.xml
```
<mapper namespace="com.example.demo.dao.mapper.AccountMapper">

    <update id="accountRmdAdd">
        UPDATE account SET rmd = rmd + #{count} WHERE id = #{id}
    </update>
    <update id="accountRmdDel">
        UPDATE account SET rmd = rmd - #{count} WHERE id = #{id}
    </update>
    <update id="accountDollerDel">
        UPDATE account SET doller = doller - #{count} WHERE id = #{id}
    </update>
    <update id="accountDollerAdd">
        UPDATE account SET doller = doller + #{count} WHERE id = #{id}
    </update>
</mapper>

```
FreezeAccountMapper.java
```

@Mapper
public interface FreezeAccountMapper {

    /**
     * 用户人民币账号增加
     * @param id
     * @param count
     * @return
     */
    int accountRmdAdd(@Param("id") Long id,@Param("count") int count);

    /**
     * 用户人民币账号减少
     * @param id
     * @param count
     * @return
     */
    int accountRmdDel(@Param("id") Long id,@Param("count") int count);

    /**
     * 用户美元账号增加
     * @param id
     * @param count
     * @return
     */
    int accountDollerAdd(@Param("id") Long id,@Param("count") int count);

    /**
     * 用户美元账号减少
     * @param id
     * @param count
     * @return
     */
    int accountDollerDel(@Param("id") Long id,@Param("count") int count);

}

```
		
FreezeAccountMapper.xml
```
<mapper namespace="com.example.demo.dao.mapper.FreezeAccountMapper">

    <update id="accountRmdAdd">
        UPDATE account SET rmd = rmd + #{count} WHERE id = #{id}
    </update>
    <update id="accountRmdDel">
        UPDATE account SET rmd = rmd - #{count} WHERE id = #{id}
    </update>
    <update id="accountDollerDel">
        UPDATE account SET doller = doller - #{count} WHERE id = #{id}
    </update>
    <update id="accountDollerAdd">
        UPDATE account SET doller = doller + #{count} WHERE id = #{id}
    </update>
</mapper>

```
数据库，要根据id进行分库分表。还需要配置shardingSphere-jdbc的配置多数据源，还没有测试，晚点测试下。


		


