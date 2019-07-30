# RbaaitMQ持久化机制和内存，磁盘控制

## RabbitMQ持久化机制

RabbitMQ的持久化分为队列持久化，消息持久化和交换器持久化。不管是持久化的消息还是非持久化的消息都可以被写入到磁盘。

![image-20190622153717438](assets/image-20190622153717438.png)

![image-20190622153742579](assets/image-20190622153742579.png)

### RabbitMQ队列持久化

1. 队列的持久化是在定义队列时的durable参数来实现的，durable为true时，队列才会持久化。

   ![image-20190622153915525](assets/image-20190622153915525.png)

2. 持久化的队列在管理界面可以看到有个"D"的标识。

   ![image-20190622154006098](assets/image-20190622154006098.png)

### RabbitMQ消息持久化

1. 消息持久化通过消息的属性deliveryMode来设置是否持久化，在发送时通过basicPublish的参数传入。

   ![image-20190622154156858](assets/image-20190622154156858.png)

### RabbitMQ交换器持久化

1. 同队列一样，交换器也需要在定义时设置持久化标识，否则在Broker重启后将丢失。

   ![image-20190622154341016](assets/image-20190622154341016.png)

## RabbitMQ内存控制

### RabbitMQ内存告警

1. 当内存使用超过配置的阈值或者磁盘剩余空间低于配置的阈值时，RabbitMQ会暂时阻塞客户端的连接，并停止接收从客户端发来的消息，以此避免服务崩溃，客户端与服务端心跳检测也会失效。

   ![image-20190622154816245](assets/image-20190622154816245.png)

2. 当出现内存告警时，可以通过管理命令临时调整内存大小。

   rabbitmqctl set_vm_memory_high_watermark <fraction>

   fraction为内存阈值，RabbitMQ默认值为0.4，表示当RabbitMQ使用的内存超过40%时，就会产生告警并阻塞所有生产者连接。通过此命令修改的阈值在Broker重启后将会失效，通过修改配置文件的方式设置的阈值则不会在重启后消失，但需要重启Broker才会生效。

   配置文件地址：/etc/rabbitmq/rabbitmq.conf

   RabbitMQ提供relative或absolute两种配置方式

   * relative相对值，即前面的fraction，建议取值在0.4~0.66之间，不建议超过0.7。
   * absolute绝对值，单位为KB，MB，GB。

   rabbitmactl set_vm_memory_high_watermark absolute <value>

### RabbitMQ内存换页

1. 在某个Broker节点触及内存阻塞生产者之前，它会尝试将队列中的消息换页到磁盘以释放内存空间。持久化和非持久化的消息都会被转储到磁盘中，其中持久化的消息本身就在磁盘中有一份副本，这里会将持久化的消息从内存中清除掉。默认情况下，在内存到达阈值的50%时会进行换页动作。也就是说，在默认的内存阈值为0.4的情况下，当内存超过0.4*0.5=0.2时，会进行换页动作。

2. 可以在配置文件vm_memory_high_watermark_paging_ratio项来修改此值。

   ![image-20190622160146001](assets/image-20190622160146001.png)

   以上配置将会在RabbitMQ内存使用率达到30%时进行换页动作，并在40%时阻塞生产者。当vm_memory_high_watermark_paging_ration的值大于1时，相当于禁用了换页功能。

### RabbitMQ磁盘告警

1. 当磁盘剩余空间低于确定的阈值时，RabbitMQ同样会阻塞生产者，这

   样可以避免因非持久化的消息继续换页而耗尽磁盘空间导致服务崩溃。默认情况下，磁盘阈值为50MB，表示当磁盘剩余空间低于50MB时会阻塞生产者并停止内存中消息的换页动作。这额阈值的设置可以减小，但不能完全消除因磁盘耗尽而导致崩溃的可能性。比如在两次磁盘空间检测期间内，磁盘空间从大于50MB被耗尽到0MB。

   一个相对谨慎的做法是将磁盘阈值设置为与操作系统所显示的内存大小一致。

2. 通过命令可以临时调整磁盘阈值

   rabbitmqctl set_disk_free_limit <disk_limit>

   rabbitmqctl set_disk_free_limit men_relative <fraction>

   disk_limit为固定大小，单位为KB，MB，GB。fraction为相对比值，建议的取值为1.0~2.0之间，对应的配置如下：

   disk_free_limit.relative=2.0

   disk_free_limit.absolute=50MB
