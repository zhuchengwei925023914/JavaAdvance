[TOC]

#  ZooKeeper典型应用场景

#### ZooKeeper实现配置中心

1. 何为配置中心。

   ![image-20191002094056183](assets/image-20191002094056183.png)
   
2. 用ZooKeeper实现配置中心。
  
  * znode能存储数据。
  * watch能够监听数据的变化。
  
  可以把原来的一个配置项或者一个配置文件作为一个znode来构成一个配置中心。

#### ZooKeeper实现命名服务

1. 何为命名服务。

   ![image-20191002094708163](assets/image-20191002094708163.png)

#### ZooKeeper实现master选举

   1. 何为Master选举。

      ![image-20191002094854792](assets/image-20191002094854792.png)

   2. ZooKeeper如何实现Master选举。

      ![image-20191002095343658](assets/image-20191002095343658.png)

#### ZooKeeper实现分布式队列

   1. ZooKeeper实现分布式队列。

      ![image-20191002095528275](assets/image-20191002095528275.png)

#### ZooKeeper实现分布式锁

   1. ZooKeeper实现分布式锁方式一：

      ![image-20191002100117498](assets/image-20191002100117498.png)

      ![image-20191002100126691](assets/image-20191002100126691.png)

      2. ZooKeeper实现分布式锁方式二：

         ![image-20191002100509394](assets/image-20191002100509394.png)

         ![image-20191002100521797](assets/image-20191002100521797.png)

         

