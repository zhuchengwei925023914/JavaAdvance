[TOC]

# Eureka服务注册与发现机制

### 无服务注册中心

![image-20191010223031273](assets/image-20191010223031273.png)

问题：

1. 接口系统服务器不固定，随时可能增删机器。
2. 接口调用方无法知晓具体服务的IP和Port地址。(除非手动调整调用者的代码)

### Eureka的作用

![image-20191010223419321](assets/image-20191010223419321.png)

1. 服务提供者启动时，定时向EurekaServer注册自己的服务信息(服务名，IP，端口等)。
2. 服务消费者启动时，后台定时拉取Eureka-Server中存储的服务信息。

### 如何集成Eureka

1. 启动服务端。

   spring cloud集成式的方法，程序入口添加注解：@EnableEurekaServer。

2. 客户端集成。

   * 引入SpringCloud中Eureka相关依赖。
   * 增加springcloud相关配置。
   * 通过@EnableDiscoveryClient注解，开启服务注册与发现功能。

![image-20191011224038836](assets/image-20191011224038836.png)

![image-20191011224056691](assets/image-20191011224056691.png)

### 服务注册流程

![image-20191012195250301](assets/image-20191012195250301.png)

启动时通过后台任务，注册到EurekaServer，内容包含有：服务名，ip，端口号。

![image-20191012195422321](assets/image-20191012195422321.png)

在服务启动时通过扫描eureka client包下的spring.factories中的自动装配配置，根据配置文件中的相关实例配置，生成InstanceInfo对象，代表一个服务实例信息。

![image-20191012200525979](assets/image-20191012200525979.png)

根据配置文件中的client相关配置，创建EurekaClient对象。

![image-20191012211642607](assets/image-20191012211642607.png)

最终调用DiscoverClient的构造方法，因为有@Inject注解，所以构造函数中依赖的对象都由Spring容器提供相应的实例。

![image-20191012212103063](assets/image-20191012212103063.png)

在父类的构造函数中启动了心跳线程任务。

![image-20191012212333733](assets/image-20191012212333733.png)

初始化定时任务。

![image-20191012212637083](assets/image-20191012212637083.png)

启动心跳定时任务。

![image-20191012212719505](assets/image-20191012212719505.png)

在HeartbeatThread线程任务重注册服务。

![image-20191012212757128](assets/image-20191012212757128.png)

再向服务端发送心跳检测时，在父端查询是否有自己的服务信息，如果有则返回成功，没有则注册。

![image-20191012212901361](assets/image-20191012212901361.png)

注册服务信息。

![image-20191012213004407](assets/image-20191012213004407.png)

### 服务端如何保存这些信息

![image-20191012213302763](assets/image-20191012213302763.png)

官方API地址：https://github.com/Netflix/eureka/wiki/Eureka-REST-operations

客户端启动后回去调http，将服务实例放在Server内部的一个Map对象中存储，获取时直接去拿。

![image-20191012213450451](assets/image-20191012213450451.png)

在AbstractJerseyEurekaHttpClient中通过Jersey框架向服务端发送http请求，注册服务。

![image-20191012214959816](assets/image-20191012214959816.png)

发送心跳的请求的url路径为：

![image-20191012215145932](assets/image-20191012215145932.png)

注册服务的请求url路径为：

![image-20191012224820712](assets/image-20191012224820712.png)

那么Eureka的服务端一定在某个地方接收了这个注册请求。

在Eureka服务端启动自动时会在EurekaServerAutoConfiguration类中自动装配一个JerseyFilter。此时我们貌似也看不出哪能够处理这个请求。所以我们在服务端开启debug日志调试，从调试信息中查看是否有相关请求。

![image-20191012220626667](assets/image-20191012220626667.png)

在服务端日志中我们看到了客户端的获取当前已注册服务的请求。处理这个类的是InstanceResource，所以我们看InstanceResource是从哪里得到的。

![image-20191012221112776](assets/image-20191012221112776.png)

InstanceResource是ApplicationResource中创建的。

![image-20191012224128190](assets/image-20191012224128190.png)

而ApplicationResource又是ApplicationsResource创建的。

![image-20191012224227241](assets/image-20191012224227241.png)

这是倒推，然后我们正向梳理一下，当心跳请求PUT /eureka/v2/apps/**appID**/**instanceID**和重新注册请求POST /eureka/v2/apps/**appID**到服务端后，JerseyFilter拦截到此请求后，根据映射关系将请求转发到处理/eureka/v2/apps/的类ApplicationsResource中，然后ApplicationsResource根据appId将请求转发到ApplicationResource中，然后AppliationResource根据请求类型和路径将心跳请求转发给InstanceResource的renewLease方法，将重新注册服务的请求转发给ApplicationResource的addInstance方法。

![image-20191012224452393](assets/image-20191012224452393.png)

![image-20191012225946466](assets/image-20191012225946466.png)

![image-20191012230024463](assets/image-20191012230024463.png)

在addInstance方法中调用InstanceRegistry的register完成注册。

![image-20191012230302685](assets/image-20191012230302685.png)

![image-20191012230421530](assets/image-20191012230421530.png)

再调用父类PeerAwareInstanceRegistryImpl的register方法。

![image-20191012230453084](assets/image-20191012230453084.png)

再调用父类AbstractInstanceRegistry的register方法完成注册。

![image-20191012230623507](assets/image-20191012230623507.png)

其中存储服务信息的是一个名为registry的CurrentHashMap变量，其结构如下：

![image-20191012222050356](assets/image-20191012222050356.png)

### 消费者服务发现

![image-20191012230919937](assets/image-20191012230919937.png)

启动时通过后台定时任务，定期从EurekaServer拉取服务信息，缓存到消费者本地内存中。 

在客户端DiscoverClient中会定时刷新本地服务缓存。

![image-20191012232455705](assets/image-20191012232455705.png)

### 高可用集群

![image-20191012232541985](assets/image-20191012232541985.png)

在eureka的高可用状态下，这些注册中心是对等的，他们会互相将注册在自己的实例同步给其他的注册中心。

在服务端启动时，自动装配类EurekaServerConfiguration中会实例化一个DefaultEurekaServerContext类。

![image-20191012234044418](assets/image-20191012234044418.png)

这个类初始构造完后会调用peerEurekaNodes的start方法。

![image-20191012234121179](assets/image-20191012234121179.png)

在此方法中启动一个定时任务，定时去更新可用的euraka集群信息。

![image-20191012234251035](assets/image-20191012234251035.png)

reslovePeerUrls方法不断去获取新加入的节点。

![image-20191012234402141](assets/image-20191012234402141.png)

如何防止重复新增实例消息重复传播。

回到服务端接收注册请求的ApplicationResource类中，会调用PeerAwareInstanceRegistryImpl的register方法。

![image-20191012235137658](assets/image-20191012235137658.png)

在此方法中会根据isReplication是否为true，也就是注册请求是否由客户端实例而不是由服务端实例发送而来确保不会出现重复注册。

![image-20191012235341429](assets/image-20191012235341429.png)

### 心跳和服务剔除机制

![image-20191013001101097](assets/image-20191013001101097.png)

心跳：客户端定期发送心跳包到EurekaServer。

一旦出现心跳长时间没有发送，那么Eureka会采用剔除机制，将服务实例改为Down状态。

在服务端启动时，自动装配类EurekaServerConfiguration中会实例化一个EurekaServerBootstrap类。

![image-20191013003325812](assets/image-20191013003325812.png)

此类中有一个初始化上下文的方法，初始化了EurekaServerContext。

![image-20191013003434705](assets/image-20191013003434705.png)

在initEurekaServerContext方法中调用了PeerAwareInstanceRegistryImpl类的openForTraffic方法。

![image-20191013003520030](assets/image-20191013003520030.png)

在openForTraffic中调用了updateRenewsPerMinThreadshold方法，来更新希望每分钟客户端发送心跳的数量的阈值。

![image-20191013003716037](assets/image-20191013003716037.png)

![image-20191013003732921](assets/image-20191013003732921.png)

在postInit方法中会起一个剔除下线服务的任务。

![image-20191013003958486](assets/image-20191013003958486.png)

首先判断，如果开启了自我保护机制，或者没有下线的服务，则直接返回，否则组装要下线的服务，随机选择一个下线。

![image-20191013004144406](assets/image-20191013004144406.png)

![image-20191013004111248](assets/image-20191013004111248.png)

线上Eureka开启自我保护机制的原因是，宁可让一些下线的服务保留在Eureka上，也不能将某些因为网络抖动原因而被误任务下线的服务剔除。Eureka牺牲了可用性。