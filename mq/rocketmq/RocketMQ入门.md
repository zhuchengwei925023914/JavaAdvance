# RocketMQ入门

### RocketMQ是什么

1. RocketMQ是由阿里捐赠给Apache的一款分布式，队列模型的开源消息中间件，经历了淘宝双十一的洗礼。

### RocketMQ的发展史

![1561273838379](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561273838379.png)

### RocketMQ的特性

![1561273878486](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561273878486.png)

### RocketMQ基本概念

1. Producer：消息生产者，负责生产消息，一般由业务系统负责产生消息。
2. Consumer：消息消费者，负责消费消息，一般是后台系统负责异步消费。
3. Push Consumer：封装消息拉取，消费进程和内部。
4. Pull Consumer：主动拉取消息，一旦拉取到消息，应用的消费进程进行初始化。
5. Producer Group：一类Producer的集合名称，这类Producer通常发送一类消息，且发送逻辑一致。
6. Consumer Group：一类Consumer的集合名称，这类Consumer通常消费一类消息，且消费逻辑一致。
7. Broker：消息中转角色，负责存储消息，转发消息，这里就是RocketMQ Server。
8. Topic：消息的主题，用于定义并在服务端配置，消费者可以按照主题进行订阅，也就是消息分类，通常一个系统一个Topic。
9. Message：在生产者，消费者，服务器之间传递的消息，一个message必须属于一个Topic。
10. NameSrv：一个无状态的名称服务，可以集群部署，每一个broker启动的时候都会向名称服务器注册，主要是接收broker的注册，接收客户端的路由请求并返回路由信息。、
11. Offset：偏移量，消费者拉取消息时需要知道上一次消费到了什么位置，这一次从哪里开始。
12. Partition：分区，Topic物理上的分组，一个Topic可以分为多个分区，每个分区是一个有序的队列。分区中的每条消息都会分配一个有序的ID，也就是偏移量。
13. Tag：用于对消息进行过滤，理解为message的标记，统一业务不同目的的message可以用相同的topic但不同的tag来区分。
14. Key：消息的key字段是为了唯一表示消息的，方便查问题，不是说必须设置，只是说设置为了方便开发和运维定位问题。比如：这个Key可以是订单ID。