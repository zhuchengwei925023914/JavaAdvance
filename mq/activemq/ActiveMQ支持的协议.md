# ActiveMQ支持的协议

### ActiveMQ支持哪些协议

1. ActiveMQ支持多种协议传输和传输方式，允许客户端使用多种协议连接。
2. ActiveMQ支持的协议：AUTO，OpenWire，AMQP，Stomp，MQTT等。
3. ActiveMQ支持的基础传输方式：VM，TCP，SSL，UDP，Peer，Multicast，HTTP(s)等，以及更高级的Failover，Fanout，Discovery，ZeroConf方式。

### ActiveMQ的协议连接配置

在${ACTIVEMQ_HOME}/conf/activemq.xml中，通过配置<transportConnectors>就可以使用多种传输方式。

![1560608686783](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560608686783.png)

### ActiveMQ常用的传输方式及配置

1. TCP，由于TCP具有可靠传输的特性，它在ActiveMQ中也是最常使用的一种协议。在默认的配置中，TCP连接的端口为61616。其配置格式为：tcp://hostname:port?key=value。

   TCP配置参数说明：在服务器端配置时，参数要以“transport.”开头，在客户端连接时，参数省略"transport."前缀。

   服务器端配置示例：

   ![1560608953407](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560608953407.png)

   客户端配置示例：

   ![1560608977241](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560608977241.png)

   TCP配置参数说明：

   ![1560609021487](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609021487.png)

   ![1560609152480](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609152480.png)

   ![1560609219265](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609219265.png)

2. SSL，需要一个安全连接的时候可以考虑使用SSL，适用于client和broker在公网的情况。其配置格式为：

   ssl://localhost:61616。

   SSL客户端配置：JMS客户端需要使用ActiveMQSslConnectionFactory类创建连接，brokerUrl以ssl://开头，下面是Spring配置示例：

   ![1560609489593](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609489593.png)

   SSL主机名验证：从ActiveMQ5.15.6开始，ActiveMQ开始支持TLS主机名验证，默认情况下客户端启动了该验证，而服务端没有启用。

   服务端配置：

   ![1560609592590](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609592590.png)

   客户端端配置：

   ![1560609623569](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609623569.png)

3. NIO，使用Java的NIO方式对连接进行改进，因为NIO使用线程池，可以复用线程，所以可以使用更少的线程维持更多的连接。如果有大量的客户端，或者性能瓶颈在网络传输上，可以考虑使用NIO的连接方式。其配置格式为：nio://hostname:port?key=value。NIO是OpenWire协议的传输方式，其他协议，像AMQP，MQTT，Stomp，也有NIO的实现，通常在协议前缀中加"+nio"来区分。比如：mqtt+nio://localhost:1883。

   NIO配置传输线程：

   ![1560609933788](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560609933788.png)

   NIO传输线程使用情况配置：属性可以在${ACTIVEMQ_HOME}/bin/env中配置，示例：

   ![1560610029526](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560610029526.png)

4. NIO SSL，从ActiveMQ 5.6版本开始，NIO可以支持和SSL搭配使用。其配置方式为：nio+ssl://0.0.0.0:61616。

5. UDP，与面向连接，可靠的字节流服务的TCP不同，UDP是一个面向数据的简单传输连接，没有TCP的三次握手，说以性能大大强于TCP，但是一牺牲可靠性为前提。使用与丢失也无所谓的消息。其配置格式为：udp://localhost:8123。

   UDP配置参数：

   ![1560610262213](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560610262213.png)

6. HTTP(s)，需要穿越防火墙，可以考虑使用HTTP(s)，但是由于HTTP(s)是短连接，每次连接的成本比较高，所以性能最差。通过XML传输数据。其配置格式为：http://localhost:8080?param1=val1&param2=val2，https://localhost:8080?param1=val1&param2=val2。

7. VM，虚拟机协议(方法直调)，使用场景是client和broker在同一个Java虚拟机内嵌的情况，无需网络通信的开销。其配置格式为：vm://borkerName?marshal=false&broker.persistent=false。

   VM配置参数说明：

   ![1560610559860](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560610559860.png)

### OpenWire协议如何使用

1. OpenWire支持TCP，SSL，NIO，UDP，VM等传输方式，直接配置这些连接，就是使用的OpenWire协议，OpenWire有自己的配置参数，客户端和服务端配置的参数名都通过前缀"wireFormat."表示。

   示例：

   ![1560610742943](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560610742943.png)

   配置参数：

   ![1560610779801](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560610779801.png)

   ![1560610824595](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560610824595.png)

   ### MQTT协议

   1. MQTT协议的结构简单，相对于其它消息协议，它更加轻量级。适合在计算能力有限，低带宽，不可靠的网络环境中使用。

   2. MQTT的发布订阅模型：

      ![1560612225653](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612225653.png)
      
   3. 服务质量
   
      ![1560612309269](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612309269.png)
   
      ![1560612336517](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612336517.png)
   
      ![1560612357463](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612357463.png)
   
      ![1560612468247](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612468247.png)
   
      
   
      ![1560612377659](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612377659.png)
   
      
   
      ![1560612507435](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612507435.png)
   
   4. 服务器端配置
   
      ![1560612579509](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612579509.png)
   
      ![1560612919279](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612919279.png)
   

![1560612982838](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560612982838.png)

### 使用AUTO协议

​     ![1560613185799](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560613185799.png)

![1560613135496](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560613135496.png)





   

