# Nginx负载均衡

### Nginx简介

1. Nginx是一款轻量级的Web服务器/反向代理服务器及电子邮件(IMAP/POP3)代理服务器，并在一个BSD协议下发行，可在UNIX，GNU/Linux，BSD，Mac OS X，Solaris，以及Microsoft Windows等操作系统中运行。
2. Nginx由俄罗斯的程序设计师lgor Sysoev所开发，最初供俄国大型的入口网站及搜索引擎Rambler使用。其特点是占有内存少，并发能力强(用于解决C10K问题)，事实上Nginx的并发能力确实在同类型网页服务器中表现较好。
3. Nginx做为一个强大的Web服务器软件，具有高性能，高并发和低内存占用的特点。此外，其也能够提供强大的反向代理功能。俄罗斯大约有超过20%的虚拟主机采用Nginx作为反向代理服务器，在国内也有腾讯，新浪，网易等多家网站在使用Nginx作为反向代理服务器。据Netcraft统计，世界上最繁忙的网站中有11.48%使用Nginx作为其服务器或代理服务器。基于反向代理的功能，Nginx作为负载均衡主要有以下几点理由：
   * 高并发连接
   * 内存消耗少
   * 配置文件非常简单
   * 成本低廉
   * 支持Rewrite重写规则
   * 内置的健康检查功能
   * 节省带宽
   * 稳定性高

### 正向代理和反向代理

1. 正向代理：正向代理类似一个跳板机，代理访问外部资源。

2. 反向代理：实际运行方式是指以代理服务器来接受Internet上的连接请求，然后将请求转发给内部网络上的服务器，并将从服务器上得到的结果返回给Internet上请求连接的客户端，此时代理服务器对外就表现为一个服务器。

3. 反向代理的作用

   * 保证内网的安全，可以使用反向代理提供WAF功能，组织web攻击。大型网站，通常将反向代理作为公网访问地址，Web服务器是内网。
   * 负载均衡：通过反向代理服务器来优化网站的负载。

   ![1561649832921](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561649832921.png)

   ![1561649862136](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561649862136.png)

   

### 负载均衡原理

负载均衡，单从字面上的意思来理解就是N台服务器平均分担负载，不会因为某台服务器负载高宕机和某台服务器闲置的情况。那么负载均衡的前提就是要2台以上的服务器才能实现。Nginx负载均衡有四种方案配置：

1. 轮询：轮询即Round Robin，根据Nginx配置文件中的顺序，依次把客户端的Web请求发送到不同的后端服务器上。
2. 最少连接least_conn：Web请求会被转发到连接数最少的服务器上。
3. IP地址哈希ip_hash：前述的两种负载均衡方案中，统一客户端连续的Web请求可能会被分发到不同的后端服务器进行处理，因此如果涉及到会话Session，那么会话会比较复杂。常见的是基于数据库的会话持久化。要克服上面的难题，可以使用基于IP地址哈希的负载均衡方案。这样的话，统一客户端连续的Web请求都会被分发到同一服务器进行处理。
4. 基于权重weight：基于权重的负载均衡即Weighted Load Balancing，这种方式下，我们可以配置Nginx把请求更多的分发到高配置的后端服务器上，把相对较少的请求分发到低配服务器。

### 负载均衡配置

1. 配置基于Round Robin轮询的负载均衡。

   * 缺省的配置就是轮询策略。
   * nginx负载均衡支持http和https协议，只需要修改proxy_pass后协议即可。
   * nginx支持FastCGI，uwsgi，SCGI，memcache的负载均衡，只需要将proxy_pass改为fastcgi_pass，uwsgi_pass，scgi_pass，memcached_pass即可。
   * 此策略配合服务器配置相当，无状态且短平快的服务使用。

   ![1561649912217](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561649912217.png)

   

2. 配置基于ip_hash的负载均衡。

   * ip哈希负载均衡使用ip_hash指定定义。
   * nginx使用请求客户端的ip地址进行哈希计算，确保使同一个服务器响应请求。
   * 此策略适合有状态服务，比如session。

   ![1561649957150](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561649957150.png)

   

3. 配置基于least_conn的负载均衡。

   * 最少连接负载均衡通过least_conn指令定义。
   * 此负载均衡策略适合处理时间长短不一致造成服务过载的情况。

   ![1561649981301](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561649981301.png)

   

4. 配置基于权重的负载均衡。

   * 权重负载均衡需要使用weight指定定义。
   * 权重越高分配到需要处理的请求越多。
   * 此策略可以与最少链接负载和ip哈希策略结合使用。
   * 此策略比较适合服务器硬件配置差别比较大的情况。

   ![1561650014485](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561650014485.png)

   ### Centos6 环境下nginx负载均衡效果演示

   1. 安装nginx，可以参考https://www.cnblogs.com/liujuncm5/p/6713784.html。

   2. 我们使用node快速模拟http服务，安装node参考https://www.jianshu.com/p/73515a3a15e6。

   3. 配置nginx。

      ```java
      user nobody;
      worker_processes auto;
      pid logs/nginx.pid;
      
      
      events {
          use epoll;
          worker_connections  1024;
      }
      
      
      http {
          include       mime.types;
          default_type  application/octet-stream;
      
          # 这里的主机名是在/etc/host中配置的
          upstream spark {
          	server 192.168.1.120:8080;
              server 192.168.1.122:8080;
              server 192.168.1.125:8080;
          }
      
          server {
          	listen 8081;
      	server_name spark;
      
      	location / {
              	proxy_pass http://spark;
                      proxy_set_header Host $host;
      		proxy_set_header X-Real-IP $remote_addr;
      		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
              }
          }
      }
      ```

   4. 编写模型http服务。

      ```java
      // 需要在三台机器上都部署一份，用node xxx.js启动
      var http = require("http");
      http.createServer(function(request, response) { 
      	response.writeHead(200, {"Content-Type":"text/plain"});
      	response.write("Hello, I am working on spark.\n");
      	response.end();
      }).listen(8080);
      ```

   5. 默认是轮询方式。

      ![1561993664686](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1561993664686.png)

      