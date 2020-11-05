package top.chendaye666.BIO;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * 客户端的代码相对简单，连接上服务端 8000 端口之后，每隔 2 秒，我们向服务端写一个带有时间戳的 "hello world"。
 *
 *IO 编程模型在客户端较少的情况下运行良好，但是对于客户端比较多的业务来说，单机服务端可能需要支撑成千上万的连接，
 * IO 模型可能就不太合适了，我们来分析一下原因。
 *
 * 上面的 demo，从服务端代码中我们可以看到，在传统的 IO 模型中，每个连接创建成功之后都需要一个线程来维护，
 * 每个线程包含一个 while 死循环，那么 1w 个连接对应 1w 个线程，继而 1w 个 while 死循环，这就带来如下几个问题：
 *
 * 线程资源受限：线程是操作系统中非常宝贵的资源，同一时刻有大量的线程处于阻塞状态是非常严重的资源浪费，操作系统耗不起
 * 线程切换效率低下：单机 CPU 核数固定，线程爆炸之后操作系统频繁进行线程切换，应用性能急剧下降。
 * 除了以上两个问题，IO 编程中，我们看到数据读写是以字节流为单位。
 * 为了解决这三个问题，JDK 在 1.4 之后提出了 NIO。
 */
public class IOClient {
    public static void main(String[] args){
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8082);
                while (true) {
                    try {
                        socket.getOutputStream().write((new Date() + ": hello world").getBytes());
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            } catch (IOException e) {
            }
        }).start();
    }
}
