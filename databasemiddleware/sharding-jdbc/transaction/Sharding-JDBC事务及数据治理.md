[TOC]

# Sharding-JDBC事务及数据治理

### 分布式事务应用

![image-20190806001653956](assets/image-20190806001653956.png)

![image-20190806001712650](assets/image-20190806001712650.png)

### 数据治理-配置中心

1. 配置中心化：越来越多的运行时实例，使得散落的配置难于管理，配置不同步导致的问题十分严重。将配置集中于配置中心，可以更加有效进行管理。
2. 配置动态化：配置修改后的分法，是配置中心可以提供的另一个重要能力。它可支持数据源，表与分片及读写分离策略的动态切换。