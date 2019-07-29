[TOC]

# 通过Lua扩展Nginx

### Lua模块

1. Nginx模块需要用C语言开发，而且必须符合一系列复杂的规则，最重要的用C开发模块必须要熟悉Nginx的源代码，使得开发者对其望而生畏。
2. ngx_lua模块通过将lua解释器集成进Nginx，可以采用lua脚本实现业务逻辑。该模块具备以下特性：
   * 高并发，非阻塞的处理各种请求。
   * Lua内建协程，这样就可以很好的将异步回调转换成顺序调用的形式。
   * 每个协程都有一个独立地全局环境(变量空间)，继承于全局共享的，只读的"comman data"。
3. 得益于Lua协程的支持，ngx_lua在处理10000个并发请求时只需要很少的内存。根据测试，ngx_lua处理每个请求只需要2KB的内存，如果使用LuaJIT则会更少。ngx_lua非常适合用于实现可扩展的，高并发的服务。

### 协程

协程类似一种多线程，与多线程的区别有：

1. 协程并非OS线程，所以创建，切换开销比线程相对要小。
2. 协程与线程一样有自己的栈，局部变量等，但是协程的栈是在用户进程空间模拟的，所以创建，切换开销很小。
3. 多线程程序是多个线程并发执行，也就是说在一瞬间由多个控制流在执行。而协程强调的是一种多个协程间协作的关系，只有当一个协程主动放弃执行权，另一个协程才能获得执行权，所以在某一瞬间，多个协程只有一个在运行。
4. 由于多个协程只有一个在运行，所以对于临界区的访问不需要加锁，而多线程的情况则必须加锁。
5. 多线程程序由于有多个控制流，所以程序的行为不可控，而多个协程的执行是由开发者定义的所以是可控的。

Nginx的每个worker进程都是在epoll或kqueue这样的事件模型之上，封装成协程，每个请求都有一个协程进行处理。这正好与Lua内建协程的模型是一致的，所以即使ngx_lua需要执行Lua，相对C有一定的开销，但依然能保证高并发能力。

### Nginx进程模型

1. Nginx采用多进程模型，单Master-多Worker，Master进程主要用来管理Worker进程。

2. Worker进程采用单线程，非阻塞的事件模型(Event Loop，事件循环)来实现端口的监听及客户端请求的处理和响应，同时Worker还要处理来自Master的信号。Worker进程的个数一般设置为机器CPU核数。

3. Master进程具体包括如下四个主要功能：

   * 接收来自外界的信号。
   * 向各worker进程发送信号。
   * 监控worker进程的运行状态。
   * 当worker进程退出后(异常情况)，会自动重新启动新的worker进程。

   ![1561820535085](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820535085.png)

### HTTP请求处理

![1561820583227](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820583227.png)

![1561820599090](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820599090.png)

![1561820631747](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820631747.png)

### ngx_lua指令

ngx_lua属于nginx的一部分，它的执行指令都包含在nginx的11个步骤之中了，相应的处理阶段可以做插入式处理，即可插拔式架构，不过ngx_lua并不是所有阶段都会运行的。另外指令可以在http，server，server if，location，location if几个范围进行配置：

![1561820662572](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820662572.png)

![1561820680420](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820680420.png)

### OpenResty

1. 概念：OpenResty是一个基于Nginx与Lua的高性能web平台，其内部集成了大量精良的Lua库，第三方模块以及大多数的依赖项。用于方便地搭建能够处理超高并发，扩展性极高的动态Web应用，Web服务和动态网关。
2. 工作原理：OpenResty通过汇聚各种设计精良的Nginx模块，从而将Nginx有效地变成一个强大的通用Web应用平台。这样，Web开发人员和系统工程师可以使用Lua脚本语言调动Nginx支持的各种C以及Lua模块，快速构造出足以胜任10k乃至1000k以上的单机并发连接的高性能Web应用系统。
3. 目标：OpenResty的目标是让你的Web服务直接跑在Nginx服务内部，充分利用Nginx的非阻塞I/O模型，不仅仅对HTTP客户端请求，甚至对于远程后端诸如MySQL，PostgreSQL，Memcached以及Redis等都进行一致的高性能响应。

### ngx_lua实例

1. content_by_lua：内容处理器，接收请求处理并输出响应。

2. 该指令工作在Nginx处理流程的content阶段，即内容产生阶段，是所有请求处理阶段中最为重要的阶段，因为这个阶段的指令通常是用来生成HTTP响应内容的。

   ![1561820763440](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561820763440.png)

### 基于Centos6示例演示

#### 演示环境搭建

1. 参考官网http://openresty.org/cn/linux-packages.html进行openresty的下载与安装。

2. 安装好后使用whereis openresty可以看到如下两个目录。

   ![1562166683508](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562166683508.png)

3. 其中/usr/local/openresty的目录结构如下图。里面新建的conf目录用来放置我们的配置文件，logs目录来放日志文件。

   ![1562166741739](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562166741739.png)

#### 示例1，Hello World

1. 我们在conf目录下新建nginx_openresty_01.conf配置文件。

   ```java
   worker_processes 1;
   error_log /usr/local/openresty/logs/error.log;
   
   events {
   	worker_connections 1024;
   }
   
   http {
   	server {
   		listen 80;
   		location / {
   			default_type text/plain;
   			content_by_lua_block {
   				ngx.say('Hello, world!')
   			}
   		}
   	}
   }
   ```

2. 使用/usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/conf/nginx_openresty_01.conf命令启动nginx。

3. 用curl命令访问，curl -i 127.0.0.1，结果如图。

   ![1562166955834](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562166955834.png)

#### 示例2，通过lua实现异步逻辑

1. 我们在conf目录下新建nginx_openresty_02.conf配置文件。

   ```java
   worker_processes 1;
   error_log /usr/local/openresty/logs/error.log;
   
   events {
   	worker_connections 1024;
   }
   
   http {
   	server {
   		listen 80;
           # 访问/lua_1后阻塞5秒钟，一起输出Hello, world! @ Time 1!和Hello, world! @ Time 1!
   		location /lua_1 {
   			default_type text/plain;
   			content_by_lua_block {
   				ngx.say('Hello, world! @ Time 1!')
   				ngx.sleep(5)
   				ngx.say('Hello, world! @ Time 1!')
   			}
   		}
   
           # 效果是先输出Hello, world! @ Time 1!，过5秒钟输出Hello, world! @ Time 2!
   		location /lua_2 {
   			default_type text/plain;
   			content_by_lua_block {
   				ngx.say('Hello, world! @ Time 1!')
   				ngx.flush()
   				ngx.sleep(5)
   				ngx.say('Hello, world! @ Time 2!')
   			}
   		}
   	}
   }
   ```

2. 使用/usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/conf/nginx_openresty_02.conf命令启动nginx。

3. 用curl命令访问，curl -i 127.0.0.1/lua_1和curl -i 127.0.0.1/lua_2。结果如图。

   ![1562167620784](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562167620784.png)

#### 示例3，传递参数

1. 我们在conf目录下新建nginx_openresty_03.conf配置文件。

   ```java
   worker_processes 1;
   error_log /usr/local/openresty/logs/error.log;
   
   events {
   	worker_connections 1024;
   }
   
   http {
   	server {
   		listen 80;
   		location / {
   			default_type text/plain;
   			content_by_lua_block {
   				ngx.say(ngx.var.arg_a)
   			}
   		}
   	}
   }
   
   ```

2. 使用/usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/conf/nginx_openresty_03.conf命令启动nginx。

3. 用curl命令访问，curl -i 127.0.0.1?a=hello_world，结果如下图。

   ![1562168029864](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562168029864.png)

#### 示例4，通过lua访问redis

1. 参考https://redis.io/download，下载安装启动redis。

2. 我们在conf目录下新建nginx_openresty_04.conf配置文件。

   ```java
   worker_processes 1;
   error_log /usr/local/openresty/logs/error.log;
   
   events {
   	worker_connections 1024;
   }
   
   http {
   	server {
   		listen 80;
   		location ~/redis_lua/(\d+)$ {
   			default_type text/plain;
   			charset utf-8;
   			lua_code_cache on;
   			content_by_lua_file '/usr/local/openresty/lua/redis.lua';
   		}
   	}
   }
   ```

3. 我们新建/usr/local/openresty/lua/redis.lua文件。

   ```java
   local json = require("cjson")
   local redis = require("resty.redis")
   local red = redis:new();
   local ip = "127.0.0.1"
   local port = 6379
   local ok, err = red:connect(ip, port)
   
   if not ok then
   	ngx.say('connect to redis error: ', err)
   	return ngix.exit(500)
   end
   
   local id = ngx.var[1]
   local value = "value-"..id
   
   red:set(id, value)
   
   local resp, err = red:get(id)
   
   if not resp then
   	ngx.say('get from redis error: ', err)
   	return nginx.exit(500)
   end
   
   red:close()
   
   ngx.say(json.encode({content=resp}))
   ```

4. 使用/usr/local/openresty/nginx/sbin/nginx -c /usr/local/openresty/conf/nginx_openresty_04.conf命令启动nginx。

5. 用curl命令访问，curl -i 127.0.0.1/redis_lua/3，结果如图。

   ![1562170300986](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562170300986.png)

6. redis中已经有key为2，值为value-2的数据。

   ![1562170345891](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1562170345891.png)

   

