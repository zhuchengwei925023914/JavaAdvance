# ActiveMQ高可用集群方案

### ActiveMQ有哪些集群部署方式

1. Master-Slave部署方式。
2. Broker-Cluster部署方式。
3. Master-Slave与Broker-Cluster相结合的部署方式。

## Master-Slave部署方式

### Shared filesystem Master-Slave部署方式

![1560660006747](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560660006747.png)

### Shared database Master-Slave部署方式

![1560663390689](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560663390689.png)

**遗留问题：自己的failover实验没有成功，一个broker服务挂掉后，没有重新连接到另一个broker。**

已解决，是因为数据库连接有问题，没有将集群数据同步到数据库，所以无法重连成功。

```java
INFO | Successfully connected to tcp://192.168.1.120:61616
收到文本消息：Hello world!
收到文本消息：Hello world!
 WARN | Transport (tcp://192.168.1.120:61616) failed , attempting to automatically reconnect: {}
java.io.EOFException
	at java.io.DataInputStream.readInt(DataInputStream.java:392)
	at org.apache.activemq.openwire.OpenWireFormat.unmarshal(OpenWireFormat.java:268)
	at org.apache.activemq.transport.tcp.TcpTransport.readCommand(TcpTransport.java:240)
	at org.apache.activemq.transport.tcp.TcpTransport.doRun(TcpTransport.java:232)
	at org.apache.activemq.transport.tcp.TcpTransport.run(TcpTransport.java:215)
	at java.lang.Thread.run(Thread.java:745)
 INFO | Successfully reconnected to tcp://192.168.1.122:61616
收到文本消息：Hello world!
```



### Replicated LevelDB Store方式(弃用)

![1560663435439](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560663435439.png)

## Broker-Cluster部署

### Broker-Cluster部署方式

![1560664124655](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664124655.png)



### Static Broker-Cluster部署配置

![1560664183953](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664183953.png)

![1560664204918](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664204918.png)

## Master-Slave与Broker-Cluster相结合的部署方式

#### Master-Slave与Broker-Cluster结合

![1560664275900](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664275900.png)

![1560664599780](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664599780.png)

### 结合部署配置

![1560664329850](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664329850.png)

## 网络连接器配置说明

### 配置属性说明

![1560664430723](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664430723.png)

![1560664459313](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560664459313.png)











