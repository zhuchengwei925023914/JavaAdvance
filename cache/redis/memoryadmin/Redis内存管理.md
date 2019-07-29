[TOC]

# Redis内存管理

### 内存分配

1. 不同数据类型的大小限制。
   * Strings类型：一个String类型的value最大可以存储512M。
   * Lists类型：list的元素个数最多为2^32 - 1个，也就是4294967295个。
   * Sets类型：元素个数最多为2^32 - 1个，也就是4294967295个。
   * Hashes类型：键值对个数最多为2^32 - 1个，也就是4294967295个。
2. 最大内存控制
   * maxmemory 最大内存阈值。
   * maxmemory-policy 到达阈值的执行策略。

### 内存压缩

### 内存压缩

1. 各种数据结果内存压缩配置。

   * hash-max-zipmap-entries 512 配置hash元素最多是512个。
   * hash-max-zipmap-value 64 配置value最大为64字节。
   * list-max-ziplist-entires 512 配置元素个数最多是512个。
   * list-max-ziplist-value 64 配置value最大为64字节。
   * set-max-intset-entires 512 配置元素个数最多为512个。
   * zset-max-ziplist-entries 128 配置元素最多是128个。
   * zset-max-ziplist-value 64 配置value最大为64字节。

   大小超出压缩范围，溢出后Redis将自动将其转换为正常大小。

### 过期数据的处理策略

1. 主动处理，redis主动触发检测key是否过期，每秒执行10次。过程如下：
   * 从具有相关过期的密钥集中测试20个随机密钥。
   * 删除找到的所有已过期的密钥。
   * 如果超过25%的密钥已过期，请从步骤一重新开始。
2. 被动处理
   * 每次访问key的时候，发现超时后被动过期，清理掉。

### 数据恢复阶段过期数据的处理策略

1. RDB方式
   * 过期的key不会被持久化到文件中。
   * 载入时过期的key，会通过redis的主动和被动方式清理掉。
2. AOF方式
   * 当redis使用AOF方式持久化时，每次遇到过期的key，redis都会追加一条DEL命令到AOF文件，也就是说只要我们顺序载入执行AOF命令文件就会删除过期的键。

过期数据的计算和计算机本身的时间是有直接联系的。

### Redis内存回收策略

1. 配置文件中设置：maxmemory-policy noeviction。
2. 动态调整：config set maxmemory-policy noeviction。

|    回收策略     |                         说明                          |
| :-------------: | :---------------------------------------------------: |
|   noeviction    |    客户端尝试执行会让更多内存被使用的命令直接报错     |
|   allkeys-lru   |                在所有key里执行LRU算法                 |
|  volatile-lru   |           在所有已经过期的key里执行LRU算法            |
|  volatile-lfu   |              使用过期的key里执行LFU算法               |
|   allkeys-lfu   |                使用近似LFU驱逐任何key                 |
| allkeys-random  |                  在所有key里随机回收                  |
| volatile-random |               再已经过期的key里随机回收               |
|  volatile-ttl   | 回收已经过期的key，并且优先回收存活时间(TTL)较短的key |

### LRU算法

LRU(Least recently used，最近最少使用)：根据数据的历史访问记录来进行淘汰数据。

1. 核心思想：如果数据最近被访问过，那么将来被访问的几率也更高。
2. 注意：Redis的LRU算法并非完整的实现，完整的LRU需要太多的内存。
3. 方法：通过对少量keys进行取样(50%)，然后回收其中的一个最少访问的key。

### LFU算法

LFU(Least Frequently Used)：根据历史的数据访问频率来淘汰数据。

1. 核心思想：如果数据过去被访问多次，那么将来被访问的频率也会更高。
2. Redis的实现是近似的，每次对key进行访问时，用基于频率的对数计数器来记录访问次数，同时这个计数器会随时间推移而减小。
3. 启用LFU算法后，可以使用热点数据分析功能。(redis-cli --hotkeys)。