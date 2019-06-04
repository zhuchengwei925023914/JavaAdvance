# Tomcat网络处理线程模型

### BIO+同步Servlet

一个请求，一个工作线程，CPU利用率低。新版本中不再使用。

![1559399174497](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1559399174497.png)

### APR+异步Servlet

apr(Apache Portable Runtime/Apache可移植运行库)，是Apache HTTP服务器的支持库。JNI的形式调用Apache HTTP服务器的核心动态链接库来处理文件读取或网络传输操作。Tomcat默认监听指定路径，如果有apr安装，则自动启用。

![1559399353220](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1559399353220.png)

### NIO+异步Servlet

Tomcat8开始，默认NIO方式。非阻塞读取请求信息，非阻塞处理下一个请求，完全异步。

![1559399464946](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1559399464946.png)

### NIO处理流程

1. 接受器接受套接字。
2. 接收器从缓存中检索nioChannel对象。
3. PollerThread将nioChannel注册到它的选择器IO事件。
4. 轮询器将nioChannel分配给一个work线程来处理请求。
5. SocketProcessor完成对请求的处理和返回。

![1559399600154](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1559399600154.png)









