# 高性能Nginx最佳实践

### Nginx监听端口

1. 基本语法：listen address:port

2. 默认：listen 80

3. 作用：listen参数决定Nginx服务器如何监听端口。在listen后可加IP地址，端口和主机名，非常灵活。

4. 例如：

   * listen 127.0.0.1:8080;
   * listen 127.0.0.1;默认80端口。
   * listen 8000;// listen *:8000;// listen localhost:800;

5. 扩展语法：listen address:port [default(deprecated in 0.8.21) | default_server | backlog=num | rcvbuf = size | snbuf = size | accept_filter = filter | deferred | bind | ipv6only = [on | off] | ssl]

6. 监听端口示例

   * 创建nginx_port.conf配置文件。

     ```java
     user nobody;
     worker_processes auto;
     # pid /run/nginx.pid;
     
     events {
     	use epoll;
     	worker_connections 65535;
     }
     
     http {
     
     	server {		
     		listen 80;
     		location / {
     			default_type text/html;
     			return 200 "Hello! I am working on 80!\n";
     		}
     	}
     	
     	server {
     		listen 8080;
     		location / {
     			default_type text/html;
     			return 200 "Hello! I am working on 8080!\n";
     		}
     	}	
     	
     }
     ```

   * 启动nginx， /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx_port.conf。

   * 用curl命令分别调用，curl -i 127.0.0.1:80，curl -i 127.0.0.1:8080，结果如下图所示。

     ![1562343489353](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562343489353.png)
   
7. upstream配置虚拟主机示例。

   * 创建nginx_upstream.conf配置文件。

     ```java
     user nobody;
     worker_processes auto;
     # pid /run/nginx.pid;
     
     events {
     	use epoll;
     	worker_connections 65535;
     }
     
     http {
     
     	upstream spark {
     		server 127.0.0.1:8080;
     	}
     
     	server {		
     		listen 80;
     		server_name spark;
     		location / {
     			proxy_pass http://spark;
     		}
     	}
     	
     	server {
     		listen 8080;
     		location / {
     			default_type text/html;
     			return 200 "Hello! I am working on 8080!\n";
     		}
     	}	
     	
     }
     ```

   * 启动nginx， /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx_upstream.conf。

   * 用curl命令调用，curl -i http://spark:80结果如下图所示。

     ![1562343884471](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562343884471.png)

### Nginx虚拟主机

### Nginx配置location

1. 语法：location[=|~*|^~|@]/uri/{...}

2. 配置块：server。

3. 详情：location会尝试根据用户请求中 的uri来匹配上面的uri表达式，如果可以匹配，就选择location块中的配置来处理用户请求。
   * =/uri表示完全匹配。
   * ~/uri表示匹配URI时大小写敏感的。
   * ~*/uri表示匹配URI时忽略大小写。
   * ^~/uri表示匹配URI时只需要其前半部分匹配即可。
   * /uri不带任何修饰符，也表示前缀匹配，但是在正则匹配之后。
   * /通用匹配，任何未匹配到其它location的请求都会匹配到，相当于switch中的default。
   
4. location配置示例。

   * 创建nginx_wildcard.conf配置文件。

     ```java
     user nobody;
     worker_processes auto;
     # pid /run/nginx.pid;
     
     events {
     	use epoll;
     	worker_connections 65535;
     }
     
     http {
     	server {
     		listen 80;
     
     		location / {
     			default_type text/html;
     			return 200 "Hello! I am working on 8080!\n";
     		}
     
     		location = /goodjob {
     			default_type text/html;
     			return 200 "location = /goodjob\n";
     		}
     
     		location ~ /GoodJob {
     			default_type text/html;
     			return 200 "location ~/GoodJob\n";
     		}
     		location ~* /goodjob {
     			default_type text/html;
     			return 200 "location ~* /goodjob";
     		}
     	}	
     	
     }
     ```

   * 启动nginx， /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx_wildcard.conf。

   * 用curl命令分别调用。

     通用匹配。

     ![1562344527888](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562344527888.png)

     

     完全匹配。

     ![1562344551886](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562344551886.png)

     大小写敏感。

     ![1562344566958](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562344566958.png)

     大小写不敏感。

     ![1562344584198](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562344584198.png)

     

### Nginx常规配置

1. Nginx worker进程运行的用户和用户组。
   * 语法：user username [groupname];
   * 默认：user nobody nobody;
   * 作用：master进程fork出的进程在哪个用户和用户组下。
2. 指定Nginx worker进程可以打开的最大句柄描述符的个数。
   * 语法：worker_rlimit_nofile_limit;
   * 作用：设置一个worker可以打开的最大句柄数。
3. 限制信号队列。
   * 语法：worker_rlimit_sigpending_limit;
   * 作用：设置每个用户发往Nginx的信号队列的大小。也就是说，当某个用户的信号队列满了，这个用户再发送的信号量就会被丢掉。

### Nginx高性能配置

1. Nginx worker进程个数。

   * 语法：worker_processes number;
   * 默认：worker_processed 1;
   * 作用：在master_worker运行方式下，定义worker进程的个数。worker进程的数量会直接影响性能。每个worker都是单线程的进程，它会调用各个模块来实现各种功能。如果确定这些模块是不会出现阻塞式调用，那么进程数可以和CPU核心数一样；反之，则稍小一些。

2. 绑定Nginx worker进程到指定的CPU内核。

   * 语法：worker_cpu_affinity cpumask [cpumask...]。

   * 作用：假设每个worker都是很繁忙的，如果多个进程都在抢同一个CPU，那么就会出现同步问题。反之，如果每个worker进程独享一个CPU，就实现了完全的并发。

     worker_processes 4;

     worker_cpu_affinity 1000 0100 0010 0001;

3. SSL硬件加速。

   * 语法：ssl_engine device;
   * 作用：如果服务器上有SSL硬件加速设备，那么就可以进行配置以加快SSL协议的处理速度。用户可以用OpenSSL提供的命令来查看是否有SSL硬件加速设备：openssl engine -t。

4. Nginx worker进程优先级设置。

   * 语法：worker_priority nice;
   * 默认：worker_priority 0;
   * 作用：在Linux和Unix中，当许多进程都可以处于可执行状态时，按照优先级来决定本次内核选择哪一个进程执行。进程分配的CPU时间片大小也与优先级有关，优先级越高，时间片越长(例如，在默认情况下，最小时间片是5ms，最大则有800ms)。优先级由静态优先级和内核根据京城的执行情况所做的冬天调整(目前只有+-5的调整)共同决定。nice是进程的优先级，它的取值范围是-20~+19，-20是最高优先级，+19是最低优先级。不建议把nice的值设为比内核进程(通常为-5)还要小。

### Nginx事件配置

1. 是否打开accept锁。
   * 语法：accept_mutex [on|off];
   * 默认：accept_mutex on;
   * 作用：accept_mutex是Nginx的负载均衡锁。这把锁可以让多个worker进程轮流的，序列化的与新的客户端建立TCP连接。accept锁默认是打开的，如果关闭它，那么建立TCP连接的耗时会更短，但不利于负载均衡，因此不建议关闭。
2. 使用accept锁定后到真正建立连接之间的延迟时间。
   * 语法：accept_mutex_delay Nms;
   * 默认：accept_mutex_delay 500ms;
   * 作用：在使用accept锁后，同一时间只有一个worker进程能够取到accept锁。这个accept锁不是阻塞锁，如果取不到会立刻返回。如果只有一个worker进程试图取锁而没有取到，他至少要等待accept_mutex_delay定义的时间才能再次尝试获取锁。
3. 批量建立新连接。
   * 语法：multi_accept [on|off];
   * 默认：multi_accept off;
   * 作用：当时间模型有新连接时，尽可能的对本次调度中客户端发起的所有TCP请求都建立连接。
4. 选择时间模型。
   * 语法：use [kqueue|resig|epoll|/dev/poll|select|poll|eventport];
   * 默认：Nginx会选出最合适的事件模型。
   * 作用：对于Linux系统，可供选择的事件驱动模型有：poll，select，epool三种，一般来说，epoll是性能最高的。
5. 每个worker的最大连接数。
   * 语法：worker_connections number;
   * 作用：定义每个worker进程可以同时处理的最大连接数。

### Nginx事件模型

1. epoll是Linux内核为处理大批量文件描述符而作了改进的poll，是Linux下多路复用IO接口select/poll的增强版本，它能显著提高程序在大量并发连接中只有少量活页的情况下的系统CPU利用率。

2. 优点：

   * 支持一个进程打开大数目的socket描述符。
   * IO效率不随FD数目增加而线性下降。
   * 使用mmap加速内核与用户空间的消息传递。

   ![1561820880000](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820880000.png)

### Nginx配置实例

1. user：进程运行的用户和用户组。
2. worker_connections：每个worker最大连接数。
3. pid：保存master进行ID的pid文件存放路径。
4. use：选择事件模型。
5. upstream：配置负载均衡。
6. ip_hash：基于IP_HASH的负载均衡。
7. listen：监听端口。
8. server_name：配置虚拟主机名。
9. location：匹配用户请求。
10. proxy_pass：配置反向代理。

![1561820944887](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820944887.png)

