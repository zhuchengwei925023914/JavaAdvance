# ActiveMQ持久化原理及事务机制

## ActiveMQ持久化方案介绍

### ActiveMQ持久化机制

ActiveMQ的消息持久化机制有JDBC，AMQ，KhaDB和LevelDb，无论使用哪种持久化方式，消息的存储逻辑都是一致的。

1. Queue类型的持久化机制

   ![1560666644050](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560666644050.png)

2. Topic类型的持久化机制

   ![1560666849621](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560666849621.png)

### JDBC方式

![1560666947081](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560666947081.png)

### AMQ方式

基于文件的存储方式，它具有写入速度快和容易恢复的特点，但是由于其重建索引时间过长，而且索引文件占用磁盘空间过大，所以已经不推荐使用。

![1560671131911](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560671131911.png)



### KahaDB方式

从ActiveMQ 5.4开始默认的持久化方式，KahaDB回复时间远远小于其前身AMQ并且使用更少的数据文件，所以完全可以替代AMQ。

### LevelDB方式

LevelDB是Google开发的一套用于持久化数据的高性能类库。LevelDB并不是一种服务，用户需要自行实现Server。是单进程的服务，能够处理十亿级别规模Key-Value型数据，占用内存小。

特点：

![1560675969550](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560675969550.png)

结构：

![1560675999512](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560675999512.png)

配置：

![1560676036579](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560676036579.png)

## 事务机制

### ActiveMQ事务实现机制

![1560676079172](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560676079172.png)

![1560676096303](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560676096303.png)

开启事务：

![1560676119304](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1560676119304.png)



