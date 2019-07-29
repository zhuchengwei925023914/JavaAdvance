[TOC]

# Java内存缓存

### 什么是缓存

1. 在计算中，缓存是一个高速数据存储层，其中存储了数据子集，且通常是短暂性存储，这样日后再次请求此数据时，速度要比访问数据的主存位置快。通过缓存，您可以高效地重用之前检索或计算的数据。

### 为什么要用缓存

1. 提升应用程序的性能。
2. 降低数据库成本。
3. 减少后端负载。
4. 可预测的性能。
5. 消除数据库热点。
6. 提高读取吞吐量(IOPS)。

### Java内存缓存

1. 在Java应用中，对于访问频次高，更新少的数据，通常的方案是将这类数据加入缓存中。相对于从数据库中读取来说，读缓存效率会有很大提升。

2. 在集群环境下，常用的分布式缓存有Redis，Memcached等。但在某些业务场景上，可能不需要去搭建一套复杂的分布式缓存系统，在单机环境下，通常是会希望使用内部的缓存。

3. 自己实现一个小缓存。

   ```java
   public class MapCacheDemo {
   
       // 我使用了  ConcurrentHashMap，线程安全的要求。
       //我使用SoftReference <Object>  作为映射值，因为软引用可以保证在抛出OutOfMemory之前，如果缺少内存，将删除引用的对象。
       //在构造函数中，我创建了一个守护程序线程，每5秒扫描一次并清理过期的对象。
       private static final int CLEAN_UP_PERIOD_IN_SEC = 5;
   
       private final ConcurrentHashMap<String, SoftReference<CacheObject>> cache = new ConcurrentHashMap<>();
   
       public MapCacheDemo() {
           Thread cleanerThread = new Thread(() -> {
               while (!Thread.currentThread().isInterrupted()) {
                   try {
                       Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * 1000);
                       cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get).map(CacheObject::isExpired).orElse(false));
                   } catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                   }
               }
           });
           cleanerThread.setDaemon(true);
           cleanerThread.start();
       }
   
       public void add(String key, Object value, long periodInMillis) {
           if (key == null) {
               return;
           }
           if (value == null) {
               cache.remove(key);
           } else {
               long expiryTime = System.currentTimeMillis() + periodInMillis;
               cache.put(key, new SoftReference<>(new CacheObject(value, expiryTime)));
           }
       }
   
       public void remove(String key) {
           cache.remove(key);
       }
   
       public Object get(String key) {
           return Optional.ofNullable(cache.get(key)).map(SoftReference::get).filter(cacheObject -> !cacheObject.isExpired()).map(CacheObject::getValue).orElse(null);
       }
   
       public void clear() {
           cache.clear();
       }
   
       public long size() {
           return cache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get).map(cacheObject -> !cacheObject.isExpired()).orElse(false)).count();
       }
   
       // 缓存对象value
       private static class CacheObject {
           private Object value;
           private long expiryTime;
   
           private CacheObject(Object value, long expiryTime) {
               this.value = value;
               this.expiryTime = expiryTime;
           }
   
           boolean isExpired() {
               return System.currentTimeMillis() > expiryTime;
           }
   
           public Object getValue() {
               return value;
           }
   
           public void setValue(Object value) {
               this.value = value;
           }
       }
   }
   ```

   

### Guava Cache

1. Guava Cache是google guaava中的一个内存缓存模块，用于将数据缓存到JVM内存中。实际项目开发中经常将一些常用的数据缓存起来方便快速访问。适用于：

   * 愿意消耗一些内存空间来提升效率。
   * 预料到某些键会被查询一次以上。
   * 缓存中存放的数据总量不会超出内存容量。

2. Guava Cahce是单个应用运行时的本地缓存。它不把数据存放到文件或者外部服务器。如果这符合你的需求，用Memcached或Redis。

3. 使用示例

   ```java
   public class GuavaCacheDemo {
       public static void main(String[] args) throws ExecutionException {
           //缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
           LoadingCache<String, User> userCache
                   //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
                   = CacheBuilder.newBuilder()
                   //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                   .concurrencyLevel(8)
                   //设置写缓存后8秒钟过期
                   .expireAfterWrite(8, TimeUnit.SECONDS)
                   //设置写缓存后1秒钟刷新
                   .refreshAfterWrite(1, TimeUnit.SECONDS)
                   //设置缓存容器的初始容量为10
                   .initialCapacity(10)
                   //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                   .maximumSize(100)
                   //设置要统计缓存的命中率
                   .recordStats()
                   //设置缓存的移除通知
                   .removalListener(new RemovalListener<Object, Object>() {
                       @Override
                       public void onRemoval(RemovalNotification<Object, Object> notification) {
                           System.out.println(notification.getKey() + " 被移除了，原因： " + notification.getCause());
                       }
                   })
                   //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                   .build(
                           new CacheLoader<String, User>() {
                               @Override
                               public User load(String key) throws Exception {
                                   System.out.println("缓存没有时，从数据库加载" + key);
                                   // TODO jdbc的代码~~忽略掉
                                   return new User("tony" + key, key);
                               }
                           }
                   );
   
           // 第一次读取
           for (int i = 0; i < 20; i++) {
               User user = userCache.get("uid" + i);
               System.out.println(user);
           }
   
           // 第二次读取
           for (int i = 0; i < 20; i++) {
               User user = userCache.get("uid" + i);
               System.out.println(user);
           }
           System.out.println("cache stats:");
           //最后打印缓存的命中率等 情况
           System.out.println(userCache.stats().toString());
       }
   }
   ```

   