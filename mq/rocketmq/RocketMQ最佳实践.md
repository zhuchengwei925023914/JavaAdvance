# RocketMQ最佳实践

### Producer

1. 一个应用尽可能用一个Topic，消息子类型用tags来标识，tags可以由应用自由设置。只有发送消息设置了tags，消费方在订阅消息时，才可以利用tags在broker做消息过滤。message.setTags("1")。
2. 每个消息在业务层面的唯一标识码，要设置到 keys字段，方便将来定位消息丢失问题。服务器会为每个消息创建索引，应用可以通过topic，key来查询这条消息内容，以及消息被谁消费。由于是哈希索引，请务必保证key尽可能的唯一，这样可以避免潜在的哈希冲突。message.setKeys(orderId)。
3. 如有可靠性需要，消息发送成功或失败，要打印消息日志(sendresult和key信息)。
4. 如果相同性质的消息量大，使用批量消息，可以提升性能。
5. 建议消息大小不超过512KB。
6. send(msg)会阻塞，如果有性能要求，可以使用异步的方式：send(msg，callback)。
7. 如果在一个JVM中，由多个生产者进行大数据处理，建议少数生产者使用异步发送方式(3到5个就够了)，通过setInstanceName方法，给每个生产者设置一个实例名。
8. send消息方法，只要不抛出异常，就代表发送成功。但是发送成功会有多个状态，在sendResult里定义：
   * SEND_OK：消息发送成功。
   * FLUSH_DISK_TIMEOUT：消息发送成功，但是服务器刷盘超时，消息已经进入服务器队列，只有此时服务器宕机，消息才会重新发送。
   * FLUSH_SLAVE_TIEMOUT：消息发送成功，但是服务器同步到Slave时超时，消息已经进入服务器队列，只有此时服务器宕机，消息才会丢失。
   * SLAVE_NOT_AVAILABLE：消息发送成功，但是此时slave不可用，消息已经进入服务器队列，只有此时服务器宕机，消息才会丢失。
   * 如果状态时FLUSH_DISK_TIMEOUT或FLUSH_SLAVE_TIMEOUT，并且Boker正好关闭，此时可以丢弃这条消息，或者重发。但建议最好重发，有消费端去重。
   * Producer向Broker发送请求会等待响应，但如果达到最大等待时间，未得到响应，则客户端将抛出RemotingTimeoutException。
   * 默认等待时间是3秒，如果使用send(msg，timeout)，则可以自己设置超时时间，但超时时间不能设置太小，因为Broker需要一些时间来刷新磁盘或与从属设备同步。
   * 如果该值超过syncFlushTimeout，则该值可能影响不大，因为Boker可能会在超时之前返回FLUSH_SLAVE_TIMEOUT或FLUSH_SLAVE_TIMEOUT的响应。
9. 对于消息不可丢失的应用，务必要有消息重发机制。
   * 最多重试3次。
   * 如果发送失败，则轮转到下一个Broker。
   * 这个方法的总耗时时间不超过sendMsgTimeout设置的值，默认10s。所以，如果本身向Broker发送消息产生超时异常，就不会再做重试。
   * 以上策略仍然不能保证消息一定发送成功，为保证消息一定发送成功，建议将消息存储到db，有后台线程定时重试，保证消息一定到达Broker。

### Consumer

1. 消费者组和订阅：不同的消费群体可以独立地消费同样的主题，并且每个消费者都有自己的消费偏移量(offsets)。确保同一组中的每个消费者订阅相同的主题。
2. 消费状态：对于MessageListenerConcurrently，可以返回RECONSUME_LATER告诉消费者，当前不能消费它并且希望以后重新消费。然后可以继续使用其他消息。对于MessageListenerOrderly，如果关心顺序，就不能跳过消息，可以返回SUSPEND_CURRENT_QUEUE_A_MOMENT来告诉消费者等待片刻。
3. 阻塞：不建议阻塞Listener，因为它会阻塞线程池，最终可能会停止消费程序。
4. 线程数：消费者使用一个ThreadPoolExecutor来处理内部的消息，因此可以通过设置setConsumeThreadMin或setConsumeThreadMax来更改它。
5. 从何处开始消费：
   * 当建立一个新的Consumer Group时，需要决定是否需要消费Broker中已经存在的消息。
   * CONSUME_FROM_LAST_OFFSET将忽略历史消息，并消费伺候生成的任何内容。
   * CONSUME_FROM_FIRST_OFFSET将消耗Broker中存在的所有消息。还可以使用CONSUME_FROM_TIMESTAMP来消费在指定时间戳之后生成的消息。
6. 重复：RocketMQ无法避免消息重复，如果业务对重复消费非常敏感，务必在业务层面做去重。通过 记录消息唯一键进行去重。使用业务层面的状态机制去重。

### NameServer

1. 在Apache RocketMQ中，NameServer用户协调分布式系统的每个组件，主要通过管理主题路由信息来实现协调。管理有两部分组成：

   * Brokers定期更新保存在每个名称服务器中的元数据。
   * 名称服务器视为客户端提供最新的路由信息服务的，包括生产者，消费者和命令行客户端。

   因此，在启动brokers和clients之前，我们需要告诉他们如何通过给他们提供一个名称服务器地址列表来访问名称服务器。在Apache RocketMQ中，可以用四种方式完成。

   * 编程的方式

     ![1561387912202](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561387912202.png)

     ![1561387930882](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561387930882.png)

   * Java参数：NameServer的地址列表也可以通过java参数rocketmq.namesrv.addr，在启动之前指定。

   * 环境变量：可以设置NAME_ADDR环境变量。如果设置了，Broker和client将检查并使用其值。
   
   * HTTP端点：
   
     ![1561388123331](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561388123331.png)
   
2. 优先级：编程方式>Java参数>环境变量>HTTP方式

### JVM与Linux内核配置

1. JVM配置

   ![1561388279278](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561388279278.png)

   

   ![1561388301239](C:\Users\zhu\Desktop\SeniorJava\Java\resource\JavaAdvance\mq\1561388301239.png)

![1561388351991](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561388351991.png)

2. Linux内核配置：
   * 在bin目录中，有一个os.sh脚本列出了许多内核参数，只需要稍微的修改，就可以用于生产环境。
   * 参考https://www.kernel.org/doc/Documentation/sysctl/vm.txt



