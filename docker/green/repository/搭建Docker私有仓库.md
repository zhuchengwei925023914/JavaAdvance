[TOC]

# 搭建Docker私有仓库

### Docker Hub

1. 目前Docker官方维护了一个公共仓库Docker Hub，其中已经包括了数量超过15000的镜像。大部分需求都可以通过在Docker Hub中直接下载镜像来实现。注册Docker Hub账号。

2. 拉取镜像。

   可以通过docker search命令来查找官方仓库的镜像，并利用docker pull命令来将它下载到本地。

3. 推送镜像。

   用户也可以在登录后通过docker push命令将自己的镜像推送到Docker Hub。

### 私有仓库

1. 有时候使用Docker Hub这样的公共仓库可能不方便，用户可以创建一个本地仓库供私人使用。比如，基于公司内部项目构建的镜像。docker-registry是官方提供的工具，可以用于构建私有的镜像仓库。

2. 安装运行docker-registry。

   可以通过获取官方registry镜像来运行。默认情况下，仓库会被创建在容器的/var/lib/registry目录下。可以通过-v参数来将镜像文件存放在本地指定路径。

   ```java
   docker run --name registry -d -p 5000:5000 --restart=always -v /learnRes/data/registry:/var/lib/registry registry
   
   # 创建私有仓库
   zhudeMacBook-Pro:seniorjava zhu$ docker run --name registry -d -p 5000:5000 --restart=always -v /learnRes/data/registry:/var/lib/registry registry
   4085a293ac9ec92989b6cac48e52485a39056afee21f26552677193135116381
   zhudeMacBook-Pro:seniorjava zhu$ docker ps -a
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                        PORTS                    NAMES
   4085a293ac9e        registry            "/entrypoint.sh /etc…"   6 seconds ago       Up 4 seconds                  0.0.0.0:5000->5000/tcp   registry
   936cad6f0fc6        f32a97de94e1        "/entrypoint.sh /etc…"   3 minutes ago       Exited (2) 3 minutes ago                               crazy_nightingale
   f0598e3b9db2        nginx:v4            "nginx -g 'daemon of…"   34 hours ago        Exited (255) 41 seconds ago   0.0.0.0:80->80/tcp       mynginxv4
   9e08fc85de49        ubuntu:16.04        "/bin/bash"              2 days ago          Exited (255) 41 seconds ago                            epic_turing
   zhudeMacBook-Pro:seniorjava zhu$ 
   ```

3. 在私有仓库上传，搜索，下载镜像。

   创建好私有仓库后，就可以使用docker tag来标记一个镜像，然后推送它到仓库。

4. 使用docker tag将本地hello-world:latest这个镜像标记为127.0.0.1:5000/hello-world:latest。格式为

   ```java
   docker tag image[:tag][registry_host][port]/repository[:tag]
   docker tag hello-world:latest 127.0.0.1:5000/hello-world:latest
   ```

5. 使用docker push上传标记的镜像。

   ```java
   docker push 127.0.0.1:5000/hello-world:latest
   ```

   ```java
   # 重新取名
   zhudeMacBook-Pro:seniorjava zhu$ docker tag hello-world:latest 127.0.0.1:5000/hello-world:latest
   zhudeMacBook-Pro:seniorjava zhu$ docker images
   REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
   nginx                        v4                  588c88ea7cf5        35 hours ago        126MB
   nginx                        v3                  b7d82a41b7cd        35 hours ago        126MB
   study/ubuntu                 1.0                 906d7caea3f0        2 days ago          86.1MB
   nginx                        latest              e445ab08b2be        2 weeks ago         126MB
   ubuntu                       16.04               5e13f8dd4c1a        2 weeks ago         120MB
   ubuntu                       latest              3556258649b2        2 weeks ago         64.2MB
   registry                     latest              f32a97de94e1        5 months ago        25.8MB
   127.0.0.1:5000/hello-world   latest              fce289e99eb9        7 months ago        1.84kB
   hello-world                  latest              fce289e99eb9        7 months ago        1.84kB
   # 推送到仓库
   zhudeMacBook-Pro:seniorjava zhu$ docker push 127.0.0.1:5000/hello-world:latest
   The push refers to repository [127.0.0.1:5000/hello-world]
   af0b15c8625b: Pushed 
   latest: digest: sha256:92c7f9c92844bbbb5d0a101b22f7c2a7949e40f8ea90c8b3bc396879d95e899a size: 524
   zhudeMacBook-Pro:seniorjava zhu$ 
   ```

6. 用curl查看仓库中的镜像。

   cur 127.0.0.1:5000/v2/_catalog

   ```java
   # 推送成功
   zhudeMacBook-Pro:seniorjava zhu$ curl 127.0.0.1:5000/v2/_catalog
   {"repositories":["hello-world"]}
   zhudeMacBook-Pro:seniorjava zhu$ 
   ```

7. 先删除已有镜像，再尝试从私有仓库中下载这个镜像。

   ```java
   docker image rm 127.0.0.1:5000/hello-world
   docker pull 127.0.0.1:5000/hello-world
   ```

   ```java
   zhudeMacBook-Pro:seniorjava zhu$ docker image rm 127.0.0.1:5000/hello-world
   Untagged: 127.0.0.1:5000/hello-world:latest
   Untagged: 127.0.0.1:5000/hello-world@sha256:92c7f9c92844bbbb5d0a101b22f7c2a7949e40f8ea90c8b3bc396879d95e899a
   zhudeMacBook-Pro:seniorjava zhu$ docker images
   REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
   nginx               v4                  588c88ea7cf5        35 hours ago        126MB
   nginx               v3                  b7d82a41b7cd        35 hours ago        126MB
   study/ubuntu        1.0                 906d7caea3f0        2 days ago          86.1MB
   nginx               latest              e445ab08b2be        2 weeks ago         126MB
   ubuntu              16.04               5e13f8dd4c1a        2 weeks ago         120MB
   ubuntu              latest              3556258649b2        2 weeks ago         64.2MB
   registry            latest              f32a97de94e1        5 months ago        25.8MB
   hello-world         latest              fce289e99eb9        7 months ago        1.84kB
   zhudeMacBook-Pro:seniorjava zhu$ docker pull 127.0.0.1:5000/hello-world
   Using default tag: latest
   latest: Pulling from hello-world
   Digest: sha256:92c7f9c92844bbbb5d0a101b22f7c2a7949e40f8ea90c8b3bc396879d95e899a
   Status: Downloaded newer image for 127.0.0.1:5000/hello-world:latest
   127.0.0.1:5000/hello-world:latest
   zhudeMacBook-Pro:seniorjava zhu$ docker images
   REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
   nginx                        v4                  588c88ea7cf5        35 hours ago        126MB
   nginx                        v3                  b7d82a41b7cd        35 hours ago        126MB
   study/ubuntu                 1.0                 906d7caea3f0        2 days ago          86.1MB
   nginx                        latest              e445ab08b2be        2 weeks ago         126MB
   ubuntu                       16.04               5e13f8dd4c1a        2 weeks ago         120MB
   ubuntu                       latest              3556258649b2        2 weeks ago         64.2MB
   registry                     latest              f32a97de94e1        5 months ago        25.8MB
   127.0.0.1:5000/hello-world   latest              fce289e99eb9        7 months ago        1.84kB
   hello-world                  latest              fce289e99eb9        7 months ago        1.84kB
   zhudeMacBook-Pro:seniorjava zhu$
   ```

8. 注意。

   如果你不想使用127.0.0.1作为仓库地址，比如想让本网段的其它主机也能把镜像推送到私有仓库。你就得把例如192.168.1.120:5000这样的内网地址作为私有仓库地址，这时你会发现无法成功推送镜像。

   对于使用systemd的系统，请在/etc/docker/daemon.json中写入如下内容。

   ```json
   {
     "registry-mirror":[
       "https://hub-mirror.c.163.com"
     ],
     "insecure-registries":[
       "192.168.1.120:5000"
     ]
   }
   ```

   