# Kafka入门

### 简介

Kafka是linkedin使用Scala编写的具有高水平扩展和高吞吐量的分布式消息系统。Kafka对消息保存时根据Topic进行归类，发送消息者称为Producer，消息接受者称为Consumer，此外Kafka集群有多个Kafka实例组成，每个实例称为一个broker。无论是Kafka集群，还是producer和consumer都依赖zookeeper来保证系统可用性，为集群保存一些meta信息。

### 主流MQ对比

![1560783194742](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560783194742.png)

吞吐量：Kafka > RabbitMq > ActiveMq

准备性：RabbitMq > ActiveMq > Kafka

### kafka主要特性

1. kafka是一个流处理平台，流平台需如下特性：
   * 可发布和订阅流数据，类似消息队列或者企业级消息系统。
   * 以容错的方式**存储**流数据。
   * 可以在流数据产生是就进行处理，基于Stream API。
2. kafka适合什么样的场景。
   * 基于kafka，构造实时流数据管道，让系统或应用之间可靠地获取数据。
   * 构建实时流式应用程序，处理流数据或基于数据做出反应。

### AMQP协议

AMQP(Advanced Message Queuing Protocol)，是一个提供统一消息服务的标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件而设计。

![1560783606105](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560783606105.png)

server：AMQP服务端，接受客户端连接，实现AMQP消息队列和路由功能的进程。

producer：生产者，向broker发布消息的客户端应用程序。

consumer：消费者，向消息队列请求消息的客户端应用程序。

### 相关概念

1. Topic，是数据主题，是kafka中用来代表一个数据流的抽象。发布数据时，可用topic对数据进行分类，也作为订阅数据的主题。一个Topic同时可有多个producer，consumer。
2. Partition，每个partition是一个**顺序**的，不可变的record序列，partition中的record会被分配一个自增长的id，我们称之为offset。
3. Record，每条记录都有key，value，timestamp三个信息。

![1560783915143](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560783915143.png)

4. Replication，每个partition还会被复制到其它服务器作为replication，这是一种冗余备份策略。

   ![1560784050263](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560784050263.png)

   * 同一个partition的多个replication不允许在同一个broker上。
   * 每个partition的replication中，有一个leader，零或多个follower。
   * leader处理此分区的所有的读写操作，follower仅仅被动的复制数据。
   * leader宕机后，会从follower中选举新的leader。

### kafka核心API

1. Producer API，允许一个应用程序发布一串流式的数据到一个或者多个kafka topic。
2. Consumer API，允许一个应用程序订阅一个或多个topic，并且对发布给他们的流式数据进行处理。
3. Stream API，允许一个应用程序作为一个流处理器，消费一个或者多个topic产生的输入流，然后生产一个输出流到一个或多个topic中去，在输入输出流中进行有效地转换。
4. Connector API，允许构建并运行可重用的生产者或消费者，将kafka topics连接到已存在的应用程序或者数据系统。比如，连接到一个关系型数据库，捕捉表的所有变更内容。

在kafka中，客户端和服务器之间的通信时通过简单，高性能，语言无关的TCP协议完成的。此协议已版本化并保持与旧版本的向后兼容性。kafka提供多种语言客户端。

![1560784529553](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560784529553.png)

### producer

![1560784561823](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560784561823.png)

1. producer会为每个partition维护一个缓冲，用来记录还没有发送的数据，每个缓冲区大小用batch.size指定，默认值为16k。
2. linger.ms为buffer中的数据在到达batch.size前，需要等待的时间。
3. acks用来配置请求成功的标准，0为客户端发出去就认为成功，1为leader写成功就认为成功，all为所有follower写成功就认为成功。

### consumer

1. Simple Consumer位于kafka.javaapi.consumer包中，不提供负载均衡，容错的特性，每次获取数据都要指定topic，partition，offset，fetchSize。

2. High-level Consumer，该客户端透明地处理kafka broker异常，透明地切换consumer的partition，通过和broker交互来实现consumer group级别的负载均衡。

   ![1560784886551](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560784886551.png)

   

### kafka整体架构

![1560784956365](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560784956365.png)

![1560784986790](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560784986790.png)

### 使用场景

1. 消息系统。消息系统被用于各种场景，如解耦数据生产者，缓存未处理的消息。kafka可作为传统的消息系统的替代者，与传统消息系统相比，kafka有更好的吞吐量，更好的可用性，这有利于处理大规模的消息。

   根据经验，通常消息传递堆吞吐量要求较低，但可能要求较低的端到端延迟，并经常依赖kafka可靠的durable机制。

   在这方面，kafka可以与传统的消息传递系统(ActivceMQ和RabbitMQ)向媲美。

2. 存储系统。写入到kafka中的数据是落到了磁盘上，并且有冗余备份，kafka允许producer等待确认，通过配置，可实现直到所有的replication完成复制才算写入成功，这样可保证数据的可用性。

   kafka认真对待存储，并允许client自行控制读取位置，你可以认为kafka是一种特殊的文件系统，它能够提供高性能，低延迟，高可用的日志提交存储。

3. 日志聚合。日志系统一般需要如下功能：日志的收集，清洗，聚合，存储，展示。

   kafka常用来替代其他日志聚合解决方案。和Scribe，Flume相比，kafka提供同样好的性能，更健壮的堆积保障，更低的端到端延迟。日志会落地，导致kafka做日志聚合更昂贵。

   kafka可实现日志的清洗(需要编码)，聚合(可靠但昂贵)，存储。

   ELK是现在比较流行的日志系统。在kafka的配合下才是更成熟的方案，kafka在ELK技术栈中，主要起到buffer的作用，必要时可进行日志的汇流。

4. 跟踪网站活动。kafka的最初是作用就是，将用户行为跟踪管道重构为一组实时发布-订阅源。把网站活动(浏览网页，搜索或其他的用户操作)发布到中心topics中，每种活动类型对应一个topic。基于这些订阅源，能够实现一系列用例，如实时处理，实时监控，批量地将kafka的数据加载到Hadoop或离线数据仓库系统，进行离线数据处理并生成报告。

   每个用户浏览网页时都生成了许多活动信息，因此活动跟踪的数据量通常非常大。

5. 流处理。kafka社区认为仅仅提供数据生产，消费机制是不够的，他们还要提供流数据实时处理机制，从0.10.0.0开始，kafka通过提供Stream API来提供轻量，但功能强大的流处理。实际上就是Streams API帮助解决流引用中一些棘手的问题，比如：处理无序数据，代码变化后再次处理数据，进行有状态的流式计算。

   Streams API的流处理包含多个阶段，从input topics消费数据，做各种处理，将结果写入到目标tipic，Stream API基于kafka提供的核心原语构建，它使用kafka consumer，producer来输入，输出，用kafka来做状态存储。

   流处理框架，flink，spark streaming，storm，samza才是正统的流处理框架，kafka在流处理中更多的是扮演流存储的角色。