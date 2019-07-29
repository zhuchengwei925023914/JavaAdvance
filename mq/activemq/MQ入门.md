# MQ入门

### ActiveMQ是什么

ActiveMQ是Apache出品，最流行的，能力强劲的开源消息总线。它是一个完全支持JMS1.1和J2EE1.4规范的JMS Provider实现，尽管JMS规范出台已经是很久的事情了，但是JMS在当今的J2EE应用中间仍然扮演着特殊的地位。

### JMS是什么

Java消息服务(Java Message Service，即JMS)应用程序接口是一个Java平台中关于面向消息中间件(MOM)的API，用在两个应用程序之间，或分布式系统中发送消息，进行异步通信。Java消息服务是一个与具体平台无关的API。

### JMS的对象模型

![1560436481922](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436481922.png)

### JMS的消息模型

![1560436535008](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436535008.png)

![1560436556500](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436556500.png)

### JMS的消息结构

![1560436599098](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436599098.png)

1. 消息头

![1560436618406](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436618406.png)

2. 消息属性

![1560436676099](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436676099.png)

3. 消息体

   ![1560436734099](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436734099.png)

   ### ActiveMQ的特性

   ![1560436777039](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560436777039.png)

   ### ActiveMQ如何发送分组消息和关闭分组消息

   1. 分组消息是通过producer在message中设置对应的JMSXGroupID。然后broker去寻找包含该ID的消费者中的一位。只要该消费者还存活着，则这个分组里面的消息就会一直发送给它。
   
      message.setStringProperty("JMSXGroupID","GroupA")
   
   2. 关闭分组，只需要在序列中设置结束符号-1即可。
   
      message.setIntProperty("JMSXGroupID",-1)
   
   
   
   









