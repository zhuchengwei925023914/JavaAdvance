[TOC]

# Docker命令

### Docker镜像操作

Docker运行容器前需要本地存在对应的镜像，如果本地不存在该镜像，Docker会从镜像仓库下载该镜像。

### 获取镜像

1. 从Docker镜像仓库获取镜像的命令是docker pull。其命令格式为：

   Docker pull [选项] [Docker Registry地址[:端口号]/]仓库名[:标签]

2. 具体的选项可以通过docker pull —help命令看到。Docker镜像仓库地址：地址的格式一般是<域名/IP>[:端口号]。默认地址是Docker Hub。仓库名：如之前所说，这里的仓库名是两段式名称，即<用户名>/<软件名>。对于Docker Hub，如果不给出用户名，则默认为library，也就是官方镜像。

   docker pull ubuntu:16.04

3. 上面的命令没有给出Docker镜像仓库地址，因此将会从Docker Hub获取镜像。而镜像名称是ubuntu:16.04，因此将会获取官方镜像library/ubuntu仓库中标签为16.04的镜像。

### 运行镜像

1. 有了镜像之后，我们就能够以这个镜像为基础启动并运行一个容器。以上面的ubuntu:16.04为例，如果我们打算启动里面的bash并且进行交互式操作的话，可以执行下面的命令。

   docker run -it —rm ubuntu:16.04 bash

2. -it：这是两个参数，一个是-i：交互式操作，一个是-t终端。

   —rm：这个参数是说容器退出后随之将其删除。

   Ubuntu:16.04：这是指用ubuntu:16.04镜像为基础来启动容器。

   bash：放在镜像名后的是命令，这里我们希望有个交互式shell，因此用的是bash。

   最后通过exit退出这个容器。

   ```java
   zhudeMacBook-Pro:~ zhu$ docker run -it --rm ubuntu bash
   Unable to find image 'ubuntu:latest' locally
   latest: Pulling from library/ubuntu
   [DEPRECATION NOTICE] registry v2 schema1 support will be removed in an upcoming release. Please contact admins of the docker.io registry NOW to avoid future disruption.
   7413c47ba209: Pull complete 
   0fe7e7cbb2e8: Pull complete 
   1d425c982345: Pull complete 
   344da5c95cec: Pull complete 
   Digest: sha256:d417542e2bd7f46f701b55075af41a6b47a736979896c28c0eafa1bafba2e500
   Status: Downloaded newer image for ubuntu:latest
   root@28d9a88f16e7:/# ls
   bin  boot  dev  etc  home  lib  lib64  media  mnt  opt  proc  root  run  sbin  srv  sys  tmp  usr  var
   root@28d9a88f16e7:/# exit
   exit
   zhudeMacBook-Pro:
   ```

### 列出镜像

1. 想要列出已经下载下来的镜像，可以使用docker image ls命令。列表包含了仓库名，标签，镜像ID，创建时间以及所占用的空间。

   docker image ls

   ```java
   zhudeMacBook-Pro:~ zhu$ docker image ls
   REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
   ubuntu              16.04               5e13f8dd4c1a        2 weeks ago         120MB
   ubuntu              latest              3556258649b2        2 weeks ago         64.2MB
   hello-world         latest              fce289e99eb9        7 months ago        1.84kB
   zhudeMacBook-Pro:~ zhu$ 
   ```

2. 查看镜像，容器，数据卷所占用的空间。

   docker system df

   ```java
   zhudeMacBook-Pro:~ zhu$ docker system df
   TYPE                TOTAL               ACTIVE              SIZE                RECLAIMABLE
   Images              3                   1                   184.5MB             184.5MB (99%)
   Containers          1                   0                   0B                  0B
   Local Volumes       0                   0                   0B                  0B
   Build Cache         0                   0                   0B                  0B
   ```

3. 仓库名，标签均为<none>的镜像称为虚悬镜像(dangling image)，显示这类镜像：

   docker image ls -f dangling=true

   制作虚悬镜像。

   ```java
   vim dockerfile
   
   FROM ubuntu:16.04 # 基础镜像
   CMD echo "Hello World!"
     
   zhudeMacBook-Pro:test zhu$ ls
   dockerfile
   zhudeMacBook-Pro:test zhu$ docker build .
   Sending build context to Docker daemon  2.048kB
   Step 1/2 : FROM ubuntu:16.04
    ---> 5e13f8dd4c1a
   Step 2/2 : CMD echo "Hello World!"
    ---> Running in d417bb91b721
   Removing intermediate container d417bb91b721
    ---> c9599aa748a4
   Successfully built c9599aa748a4
   zhudeMacBook-Pro:test zhu$ docker images
   REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
   <none>              <none>              c9599aa748a4        6 seconds ago       120MB
   ubuntu              16.04               5e13f8dd4c1a        2 weeks ago         120MB
   ubuntu              latest              3556258649b2        2 weeks ago         64.2MB
   hello-world         latest              fce289e99eb9        7 months ago        1.84kB
   zhudeMacBook-Pro:test zhu$ 
   # 查看虚玄镜像
   zhudeMacBook-Pro:test zhu$ docker image ls -f dangling=true
   REPOSITORY          TAG                 IMAGE ID            CREATED              SIZE
   <none>              <none>              c9599aa748a4        About a minute ago   120MB
   zhudeMacBook-Pro:test zhu$ 
   ```

4. 一般来说，虚悬镜像已经失去了存在的价值，是可以随意删除的，可用下面命令删除。

   docker image prune

### 删除本地镜像

1. 如果要删除本地的镜像，可以使用docker image rm命令，其格式为：

   docker image rm [选项] <镜像1> [<镜像2>...]

   其中，<镜像>可以是镜像短ID，镜像长ID，镜像名或者镜像摘要。

2. 使用docker image ls -q来配合docker image rm，这样可以批量删除希望删除的镜像。

   docker image rm $(docker image ls -q ubuntu) #删除所有仓库名为ubuntu的镜像

3. 或者删除所有在ubuntu:16.04之前的镜像：

   docker image rm $(docker image ls -q -f before=ubuntu:16.04)

### 容器操作

容器是独立运行的一个或一组应用，以及它们的运行态环境。对应的，虚拟机可以理解为模拟运行的一整套操作系统(提供了运行态环境和其他系统环境)和跑在上面的应用。

### 启动容器

启动容器有两种方式，一种是基于镜像新建一个容器并启动，另外一个是将在终止态的容器重新启动。因为Docker的容器是轻量级的，用户可以随时删除和新创建容器。

1. 新建并启动。

   docker run

   ```java
   zhudeMacBook-Pro:test zhu$ docker run ubuntu:16.04 bin/echo 'Hello World!'
   Hello World!
   zhudeMacBook-Pro:test zhu$ 
   ```

2. 启动已经终止的容器。

   * docker container start或者docker start

   * 启动一个bash终端，允许用户进行交互。

      docker run -t -i ubuntu:16.04 /bin/bash

   * -t让docker分配一个伪终端并绑定到容器的标准输入上，-i则让容器的标准输入保持打开。当利用docker run 来创建容器时，docker在后台运行的标准操作包括：

     检查本地是否存在指定的镜像，不存在就从公有仓库下载。

     利用镜像创建并启动一个容器。

     分配一个文件系统，并在只读的镜像层外面挂一层可读写层。

     从宿主机配置的网桥接口中桥接一个虚拟接口到容器中去。

     从地址池分配一个ip地址给容器。

     执行用户指定的应用程序。

     执行完毕后容器被终止。

     ```java
     zhudeMacBook-Pro:test zhu$ docker run -it ubuntu:16.04 /bin/bash
     root@9e08fc85de49:/# ls
     bin  boot  dev  etc  home  lib  lib64  media  mnt  opt  proc  root  run  sbin  srv  sys  tmp  usr  var
     root@9e08fc85de49:/# 
     ```

### 后台运行

很多时候，需要让Docker在后台运行而不是直接把执行命令的结果输出在当前宿主机下。此时，可以通过添加-d参数来实现。

如果不是用-d参数运行容器，比如docker run hello-world会把日志打印在控制台。

如果使用了-d参数运行容器，比如docker run -d hello-world不会输出日志，只会打印容器id(输出结果可以用docker logs查看)。

注：容器是否会长久运行，是和docker run指定的命令有关，和-d参数无关。

```java
zhudeMacBook-Pro:test zhu$ docker run -d hello-world
9e19ae8d9fb86303896eb4fa7d905f2621fdc9f9386997bee85129718df4ec19
zhudeMacBook-Pro:test zhu$ docker logs 9e19ae8d9fb86303896eb4fa7d905f2621fdc9f9386997bee85129718df4ec19

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/get-started/
```

### 停止运行的容器

可以使用docker container stop来终止一个运行的容器。终止状态可以用docker container ls -a命令看到。处于终止状态的容器，可以通过docker container start命令来重新启动。此外，docker container restart命令会将一个运行态的容器终止，然后再重新启动它。

```java
zhudeMacBook-Pro:test zhu$ docker ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
9e19ae8d9fb8        hello-world         "/hello"                 4 minutes ago       Exited (0) 4 minutes ago                        hardcore_ganguly
9e08fc85de49        ubuntu:16.04        "/bin/bash"              8 minutes ago       Exited (0) 8 minutes ago                        epic_turing
f4b439f8beef        ubuntu:16.04        "/bin/bash"              8 minutes ago       Exited (0) 8 minutes ago                        sharp_grothendieck
333eb0179073        ubuntu:16.04        "bin/echo 'Hello Wor…"   16 minutes ago      Exited (0) 16 minutes ago                       laughing_proskuriakova
c3889a997cc6        hello-world         "/hello"                 24 hours ago        Exited (0) 24 hours ago                         heuristic_elbakyan
zhudeMacBook-Pro:test zhu$ docker container start 9e08fc85de49
9e08fc85de49
zhudeMacBook-Pro:test zhu$ docker ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
9e19ae8d9fb8        hello-world         "/hello"                 5 minutes ago       Exited (0) 5 minutes ago                        hardcore_ganguly
9e08fc85de49        ubuntu:16.04        "/bin/bash"              9 minutes ago       Up 3 seconds                                    epic_turing
f4b439f8beef        ubuntu:16.04        "/bin/bash"              9 minutes ago       Exited (0) 9 minutes ago                        sharp_grothendieck
333eb0179073        ubuntu:16.04        "bin/echo 'Hello Wor…"   16 minutes ago      Exited (0) 16 minutes ago                       laughing_proskuriakova
c3889a997cc6        hello-world         "/hello"                 24 hours ago        Exited (0) 24 hours ago                         heuristic_elbakyan
zhudeMacBook-Pro:test zhu$ 
```

### 进入容器

1. 在使用-d参数时，容器启动后会进入后台，某些时候需要进入容器进行操作，使用docker exec命令进入运行中。

2. exec命令-i -t参数。

   docker exec后边可以跟多个参数，只用-i参数时，由于没有分配伪终端，界面没有我们熟悉的Linux命令提示符，但命令执行结果仍然可以返回。当-i，-t参数一起使用时，则可以看到我们熟悉的Linux命令提示符。

   docker exec -it 容器id /bin/bash

   ```java
   zhudeMacBook-Pro:test zhu$ docker ps -a
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
   9e19ae8d9fb8        hello-world         "/hello"                 6 minutes ago       Exited (0) 6 minutes ago                        hardcore_ganguly
   9e08fc85de49        ubuntu:16.04        "/bin/bash"              10 minutes ago      Exited (0) 3 seconds ago                        epic_turing
   f4b439f8beef        ubuntu:16.04        "/bin/bash"              10 minutes ago      Exited (0) 10 minutes ago                       sharp_grothendieck
   333eb0179073        ubuntu:16.04        "bin/echo 'Hello Wor…"   18 minutes ago      Exited (0) 18 minutes ago                       laughing_proskuriakova
   c3889a997cc6        hello-world         "/hello"                 24 hours ago        Exited (0) 24 hours ago                         heuristic_elbakyan
   zhudeMacBook-Pro:test zhu$ docker container start 9e08fc85de49
   9e08fc85de49
   zhudeMacBook-Pro:test zhu$ docker ps -a
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
   9e19ae8d9fb8        hello-world         "/hello"                 11 minutes ago      Exited (0) 11 minutes ago                       hardcore_ganguly
   9e08fc85de49        ubuntu:16.04        "/bin/bash"              15 minutes ago      Up 3 seconds                                    epic_turing
   f4b439f8beef        ubuntu:16.04        "/bin/bash"              15 minutes ago      Exited (0) 15 minutes ago                       sharp_grothendieck
   333eb0179073        ubuntu:16.04        "bin/echo 'Hello Wor…"   22 minutes ago      Exited (0) 22 minutes ago                       laughing_proskuriakova
   c3889a997cc6        hello-world         "/hello"                 24 hours ago        Exited (0) 24 hours ago                         heuristic_elbakyan
   zhudeMacBook-Pro:test zhu$ docker exec -it 9e08fc85de49 /bin/bash
   root@9e08fc85de49:/# ls
   bin  boot  dev  etc  home  lib  lib64  media  mnt  opt  proc  root  run  sbin  srv  sys  tmp  usr  var
   root@9e08fc85de49:/#
   ```

### 导出导入容器

1. 导出容器。

   如果要导出本地某个容器，可以使用docker export命令。

   docker export 容器ID > 导出文件名.zip

2. 导入容器。

   * 可以使用docker import从容器快照文件中再倒入为镜像。cat 导出文件名.tar | docker import - 镜像用户/镜像名:镜像版本

   * 也可以通过制定URL或某个目录来导入。

     docker import http://study.163.com/image.tgz example/imagerepo

   ```java
   zhudeMacBook-Pro:~ zhu$ docker ps -a
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
   9e19ae8d9fb8        hello-world         "/hello"                 17 minutes ago      Exited (0) 17 minutes ago                       hardcore_ganguly
   9e08fc85de49        ubuntu:16.04        "/bin/bash"              21 minutes ago      Up 6 minutes                                    epic_turing
   f4b439f8beef        ubuntu:16.04        "/bin/bash"              21 minutes ago      Exited (0) 21 minutes ago                       sharp_grothendieck
   333eb0179073        ubuntu:16.04        "bin/echo 'Hello Wor…"   29 minutes ago      Exited (0) 29 minutes ago                       laughing_proskuriakova
   c3889a997cc6        hello-world         "/hello"                 24 hours ago        Exited (0) 24 hours ago                         heuristic_elbakyan
   zhudeMacBook-Pro:~ zhu$ docker export 9e08fc85de49 > aaa.zip
   zhudeMacBook-Pro:~ zhu$ ls
   AnacondaProjects	Documents		Library			Music			Projects		aaa.zip			go			test
   Desktop			Downloads		Movies			Pictures		Public			dump.rdb		jupyter
   zhudeMacBook-Pro:~ zhu$ cat aaa.zip | docker import - study/ubuntu:1.0
   sha256:906d7caea3f0c228c8146530803a051a7a49d6b679781ba629d9f91aa450fa28
   zhudeMacBook-Pro:~ zhu$ docker image ls -a
   REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
   study/ubuntu        1.0                 906d7caea3f0        5 seconds ago       86.1MB
   ubuntu              16.04               5e13f8dd4c1a        2 weeks ago         120MB
   ubuntu              latest              3556258649b2        2 weeks ago         64.2MB
   hello-world         latest              fce289e99eb9        7 months ago        1.84kB
   zhudeMacBook-Pro:~ zhu$ 
   ```

### 删除容器

1. 删除容器。

   可以使用docker container rm来删除一个处于终止状态的容器。

   docker container rm ubuntu:16.04

   如果要删除一个运行中的容器，可以添加-f参数，Docker会发送SIGKILL信号给容器。

2. 清理所有处于终止状态的容器。

   用docker container ls -a命令可以查看所有已经创建的包括终止状态的容器，如果数量太多要一个个删除可能会很麻烦，用下面的命令可以清理所有处于终止状体的容器。

   docker container prune

   ```java
   zhudeMacBook-Pro:~ zhu$ docker container ls -a
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
   9e19ae8d9fb8        hello-world         "/hello"                 23 minutes ago      Exited (0) 23 minutes ago                       hardcore_ganguly
   9e08fc85de49        ubuntu:16.04        "/bin/bash"              26 minutes ago      Up 11 minutes                                   epic_turing
   f4b439f8beef        ubuntu:16.04        "/bin/bash"              27 minutes ago      Exited (0) 27 minutes ago                       sharp_grothendieck
   333eb0179073        ubuntu:16.04        "bin/echo 'Hello Wor…"   34 minutes ago      Exited (0) 34 minutes ago                       laughing_proskuriakova
   c3889a997cc6        hello-world         "/hello"                 25 hours ago        Exited (0) 25 hours ago                         heuristic_elbakyan
   zhudeMacBook-Pro:~ zhu$ docker container prune
   WARNING! This will remove all stopped containers.
   Are you sure you want to continue? [y/N] y
   Deleted Containers:
   9e19ae8d9fb86303896eb4fa7d905f2621fdc9f9386997bee85129718df4ec19
   f4b439f8beeff0983c823011062ef1b98f0263710279ccd863138efffe0722a6
   333eb017907351731dd6ed5693d25126a3a485d9aafa26473645c14cd3ed4030
   c3889a997cc6c782f1cb40b56479a82b8a3673337e0d72698b587f6f216cf2c3
   
   Total reclaimed space: 0B
   zhudeMacBook-Pro:~ zhu$ docker ps -a
   CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
   9e08fc85de49        ubuntu:16.04        "/bin/bash"         27 minutes ago      Up 12 minutes                           epic_turing
   zhudeMacBook-Pro:~ zhu$ 
   ```

   