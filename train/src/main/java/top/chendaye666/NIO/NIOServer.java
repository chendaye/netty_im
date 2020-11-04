package top.chendaye666.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO
 * NIO 模型中通常会有两个线程，每个线程绑定一个轮询器 selector ，
 * 在我们这个例子中serverSelector负责轮询是否有新的连接，clientSelector负责轮询连接是否有数据可读
 * 服务端监测到新的连接之后，不再创建一个新的线程，而是直接将新连接绑定到clientSelector上，
 * 这样就不用 IO 模型中 1w 个 while 循环在死等，参见(1)
 * clientSelector被一个 while 死循环包裹着，如果在某一时刻有多条连接有数据可读，
 * 那么通过 clientSelector.select(1)方法可以轮询出来，进而批量处理，参见(2)
 * 数据的读写面向 Buffer，参见(3)
 *
 * 模型中有2个线程： serverSelector clientSelector
 * serverSelector 负责轮询是否有新的连接；如果有，就把连接绑定到 clientSelector 上
 * clientSelector 负责轮询绑定在自身的连接， 轮询是否有读写请求
 * 数据读写是面向Buffer
 *
 *
 *
 总之，强烈不建议直接基于JDK原生NIO来进行网络开发，下面是我总结的原因

 JDK 的 NIO 编程需要了解很多的概念，编程复杂，对 NIO 入门非常不友好，编程模型不友好，ByteBuffer 的 Api 简直反人类
 对 NIO 编程来说，一个比较合适的线程模型能充分发挥它的优势，而 JDK 没有给你实现，你需要自己实现，
 就连简单的自定义协议拆包都要你自己实现
 JDK 的 NIO 底层由 epoll 实现，该实现饱受诟病的空轮询 bug 会导致 cpu 飙升 100%
 项目庞大之后，自行实现的 NIO 很容易出现各类 bug，维护成本较高，上面这一坨代码我都不能保证没有 bug
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        Selector serverSelector = Selector.open();
        Selector clientSelector = Selector.open();

        new Thread(() -> {
            try {
                //nio服务端启动
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(8000));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true){
                    // 监测是否有新的连接，这里的1指的是阻塞的时间为 1ms
                    if (serverSelector.select(1) > 0){
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()){
                            SelectionKey key = keyIterator.next();

                            if (key.isAcceptable()){
                                try {
                                    // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                }finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            }catch (IOException e){

            }
        }).start();


        new Thread(() -> {
            try {
                while (true){
                    if (clientSelector.select(1) > 0){
                        Set<SelectionKey> keys = clientSelector.selectedKeys();
                        Iterator<SelectionKey> iterator = keys.iterator();
                        while (iterator.hasNext()){
                            SelectionKey key = iterator.next();
                            if (key.isAcceptable()){
                                try {
                                    SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    // (3) 面向 Buffer
                                    clientChannel.read(byteBuffer);
                                    byteBuffer.flip();
                                    System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer)
                                            .toString());
                                }catch (IOException e){

                                }
                            }
                        }
                    }
                }
            }catch (IOException e){

            }
        }).start();
    }
}
