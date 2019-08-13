[TOC]

# Docker运行Java程序

### 定制镜像

1. 准备一个没有第三方依赖的java web项目。
2. 把该war包上传到安装有docker软件的服务器上宿主目录下。在同级目录下创建Dockerfile。
3. 根据Dockerfile定制镜像命令来编写Dockerfile文件内容。

### Java程序Dockerfile

1. Dockerfile文件内容如下。

   ```java
   # 基础镜像使用tomcat:7.0.88-jre8
   FROM tomcat:7.0.88-jre8
   # 作者
   MAINTAINER allen <allen@163.com>
   # 定义环境变量
   ENV TOMCAT_BASE /usr/local/tomcat
   # 复制war包
   COPY ./session-web.war $TOMCAT_BASE/webapps/
   ```

2. 执行构建。

   ```java
   docker build -t session-web:latest .
   ```

3. 如果构建成功，则会显示构建的分层信息及结果。

4. 构建成功后使用docker_images命令查看本地是否有该镜像。

### 运行镜像

1. 镜像制作好之后我们就需要把它运行起来了。

   ```java
docker run --name session-web -d -p 8888:8080 session-web:latest
   ```

2. 启动后使用netstat -na | grep 8888验证端口是否在监听状态。

3. 浏览器中访问http://ip:8888/session-web/user/login