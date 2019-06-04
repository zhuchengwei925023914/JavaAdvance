# Netty线程模型

### Netty简介

Netty时一个高性能，高可扩展的异步事件驱动的网络应用程序框架，它极大地简化了TCP和UDP客户端和服务器开发等网络编程。

1. Reactor线程模型：一种高性能的多线程程序设计思路。
2. Netty中自己定义的Channel概念：增强版的通道概念。
3. ChannelPipeline职责链设计模式：事件处理机制。
4. 内存管理：增强的ByteBuf缓冲区。

### Netty线程模型

为了让NIO处理更好的利用多线程特性，Netty实现了Reactor线程模型。Reactor模型中有四个核心概念：

1. Resources资源(请求/任务)。
2. Synchronouts Event Demultiplexer同步事件复用器。
3. Dispatcher分配器。
4. Request Handler请求处理器。

![1558273941050](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558273941050.png)

### EventLoopGroup初始化过程

![1558275036383](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558275036383.png)

### EventLoop的启动

![1558275117829](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558275117829.png)

### Bind绑定端口过程

![1558275188887](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558275188887.png)

### Channel概念

![1558275286198](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558275286198.png)











