[TOC]

# Starter快速集成机制

### Starter介绍

1. 作用。

   启动器包含许多依赖项，这些依赖项是使项目快速启动和运行所需的依赖项。

   例如：通过配置spring-boot-starter-data-redis，可以快捷地使用Spring对Redis进行数据访问。

2. 命名规范。

   官方开发的starter遵循类似的命名模式：spring-boot-starter-*。

   第三方starter命名应当遵循thirdpartyproject-spring-boot-starter。

3. 常用的starter。

   * spring-boot-starter-jdbc
   * spring-boot-starter-redis
   * spring-boot-starter-web
   * spring-boot-starter-actuator

### Web开发示例

1. 引入spring-boot-starter-web实现快速引入和启动，无需再进行繁杂的xml配置。

2. 默认基于Tomcat容器运行，可通过修改pom.xml指定运行的容器。

   ![image-20191008213346241](assets/image-20191008213346241.png)

### 自研Starter的步骤

1. 建工程。
2. 引入spring-boot-starter，spring-boot-autoconfigure，第三方jar。
3. 如需要生成配置元信息，加入spring-boot-configuration-processor依赖。
4. 编写自动配置类。
5. 配置发现配置文件：META-INF/spring.factories。
6. 打包发布。