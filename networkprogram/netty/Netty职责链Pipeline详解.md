# Netty职责链Pipeline详解

### 设计模式-责任链模式

责任链模式为请求创建了一个处理对象的链。发起请求和具体请求的过程进行解耦。职责链上的处理者负责处理请求，客户端只需要将请求发送到职责链上即可，无需关心请求的处理细节和请求的传递。

![1558448248895](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558448248895.png)

### 实现责任链模式

实现责任链模式4个要素：处理器抽象类，具体的处理器实现类，保存处理器信息，处理执行

![1558448332660](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558448332660.png)

![1558448342604](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558448342604.png)

```java
public class PipelineDemo {

    /**
     * 初始化时需要构造一个头作为责任链的开始,没有具体处理过程只是将请求进行传播
     */
    public HandlerChainContext head = new HandlerChainContext(new AbstractHandler() {
        @Override
        void doHandler(HandlerChainContext handlerChainContext, Object arg0) {
            handlerChainContext.runNext(arg0);
        }
    });

    public void requestProcess(Object arg0) {
        this.head.handler(arg0);
    }

    public void addLast(AbstractHandler handler) {
        HandlerChainContext context = head;
        while (context.next != null) {
            context = context.next;
        }
        context.next = new HandlerChainContext(handler);
    }

    public static void main(String[] args) {
        PipelineDemo pipelineDemo = new PipelineDemo();
        pipelineDemo.addLast(new HandlerOne());
        pipelineDemo.addLast(new HandlerTwo());
        pipelineDemo.addLast(new HandlerOne());
        pipelineDemo.requestProcess("开始做蛋糕");
    }
}

/**
 * 责任链上下文,负责维护链
 */
class HandlerChainContext {
    HandlerChainContext next;
    AbstractHandler handler;

    public HandlerChainContext(AbstractHandler handler) {
        this.handler = handler;
    }

    void handler(Object arg0) {
        this.handler.doHandler(this, arg0);
    }

    void runNext(Object arg0) {
        if (null != this.next) {
            this.next.handler(arg0);
        }
    }
}

/**
 * 抽象处理器
 */
abstract class AbstractHandler {
    abstract void doHandler(HandlerChainContext handlerChainContext, Object arg0);
}

class HandlerOne extends AbstractHandler {
    @Override
    void doHandler(HandlerChainContext handlerChainContext, Object arg0) {
        arg0 = arg0.toString() + " 和面";
        System.out.println("hanlerOne handle: " + arg0);
        handlerChainContext.runNext(arg0);
    }
}

class HandlerTwo extends AbstractHandler {
    @Override
    void doHandler(HandlerChainContext handlerChainContext, Object arg0) {
        arg0 = arg0.toString() + " 加奶油";
        System.out.println("handlerTwo handle: " + arg0);
        handlerChainContext.runNext(arg0);
    }
}
```

### Netty中的ChannelPipeline责任链

Pipeline管道中保存了通道所有的处理器信息。创建新Channel时自动创建一个专有的pipeline。入站事件和出站操作会调用Pipeline上的处理器。

![1558449515517](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558449515517.png)

### 入站事件和出站事件

1. 入站事件：通常指I/O线程生成了入站数据。通俗点说就是从socket底层自己往上往上冒出来的事件都是入站事件。比如EventLoop收到selector的OP_READ事件，入站处理器调用socketChannel.read(ByteBuffer)接收到数据后，这将导致通道的ChannelPipeline中包含下一个钟的channelRead方法被调用。
2. 出站事件：经常指I/O线程执行实际的输出操作。通俗点说就是想主动往socket底层操作的事件都是出站事件。比如bind方法用意是请求server socket绑定到给定的SocketAddress，这将导致通道的ChannelPipeline中包含的下一个出站处理器中的bind方法被调用。

### Netty中事件定义

![1558450424213](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558450424213.png)

![1558450459692](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558450459692.png)

### Pipeline中的handler是什么

1. ChannelHandler：用于处理I/O事件或拦截I/O操作，并转发到ChannelPipeline中的下一个处理器。这个顶级接口定义功能很弱，实际使用时会去实现下面两大子接口：处理入站I/O事件的ChannelInboundHandler，处理出站I/O操作的ChannelOutboundHandler。
2. 适配器类：为了开发方便，避免所有handler去实现一遍接口方法，Netty提供了简单的实现类：ChannelInboundHandlerAdapter处理入站I/O事件，ChannelOutboundHandlerAdapter来处理出站I/O事件，ChannelDuplexHandler来支持同时处理入站和出站事件。
3. ChannelHandlerContext：实际存储在Pipeline中的对象并非ChannelHandler，而是上下文对象。将handler包裹在上下文对象中，通过上下文对象与它所属的ChannelPipeline交互，向上或向下传递事件或者修改pipeline都是通过上下文对象进行的。

### 维护Pipeline中的handler

ChannelPipeline是线程安全的，ChannelHandler可以在任何时候添加或删除。例如，你可以在即将交换敏感信息时插入加密处理程序，并在交换完后删除它。

![1558451021170](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558451021170.png)

### handler的执行分析

![1558451557241](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558451557241.png)

### 分析registered入站事件的处理

![1558453118261](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558453118261.png)

### 分析bind出站事件的处理

![1558535275656](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558535275656.png)

### 分析accept入站事件的处理

![1558536432164](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558536432164.png)

### 分析read入站事件的处理

![1558537361978](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558537361978.png)

## 小结

![1558537424922](C:\Users\zhu\AppData\Roaming\Typora\typora-user-images\1558537424922.png)



