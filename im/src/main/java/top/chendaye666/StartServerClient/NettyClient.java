package top.chendaye666.StartServerClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 首先，与服务端的启动一样，我们需要给它指定线程模型，驱动着连接的数据读写，
 * 这个线程的概念可以和第一小节Netty是什么中的 IOClient.java 创建的线程联系起来
 * 然后，我们指定 IO 模型为 NioSocketChannel，表示 IO 模型为 NIO，当然，你可以可以设置 IO 模型为 OioSocketChannel，
 * 但是通常不会这么做，因为 Netty 的优势在于 NIO
 * 接着，给引导类指定一个 handler，这里主要就是定义连接的业务处理逻辑，不理解没关系，在后面我们会详细分析
 * 配置完线程模型、IO 模型、业务处理逻辑之后，调用 connect 方法进行连接，可以看到 connect 方法有两个参数，
 * 第一个参数可以填写 IP 或者域名，第二个参数填写的是端口号，由于 connect 方法返回的是一个 Future，也就是说这个方是异步的，
 * 我们通过 addListener 方法可以监听到连接是否成功，进而打印出连接信息
 */
public class NettyClient {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        // 客户端启动的引导类是 Bootstrap，负责启动客户端以及连接服务端
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)  //指定线程模型
                .channel(NioSocketChannel.class) //指定IO类为NIO
                .handler(new ChannelInitializer<SocketChannel>() { // IO处理逻辑
                    @Override
                    public void initChannel(SocketChannel ch){

                    }
                });
        // 4.建立连接
        connect(bootstrap, "localhost", 80);


        // attr() 方法
        // attr() 方法可以给客户端 Channel，也就是NioSocketChannel绑定自定义属性，
        // 然后我们可以通过channel.attr()取出这个属性，比如，上面的代码我们指定我们客户端 Channel 的一个clientName属性，
        // 属性值为nettyClient，其实说白了就是给NioSocketChannel维护一个 map 而已，
        // 后续在这个 NioSocketChannel 通过参数传来传去的时候，就可以通过他来取出设置的属性，非常方便。
        bootstrap.attr(AttributeKey.newInstance("clientName"), "nettyClient");

        // option() 方法
        // option() 方法可以给连接设置一些 TCP 底层相关的属性，比如上面，我们设置了三种 TCP 属性，其中
        //ChannelOption.CONNECT_TIMEOUT_MILLIS 表示连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
        //ChannelOption.SO_KEEPALIVE 表示是否开启 TCP 底层心跳机制，true 为开启
        //ChannelOption.TCP_NODELAY 表示是否开始 Nagle 算法，true 表示关闭，false 表示开启，
        // 通俗地说，如果要求高实时性，有数据发送时就马上发送，就设置为 true 关闭，如果需要减少发送次数减少网络交互，就设置为 false 开启
        bootstrap
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);

    }


    // 重连逻辑
    private static void connect(Bootstrap bootstrap, String host, int port) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else {
                System.err.println("连接失败，开始重连");
                connect(bootstrap, host, port);
            }
        });
    }

    private static final int MAX_RETRY = 434; // 重连次数
    // 每隔 1 秒、2 秒、4 秒、8 秒，以 2 的幂次来建立连接，然后到达一定次数之后就放弃连接
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                // 定时任务是调用 bootstrap.config().group().schedule(), 其中 bootstrap.config()
                // 这个方法返回的是 BootstrapConfig，他是对 Bootstrap 配置参数的抽象，然后 bootstrap.config().group()
                // 返回的就是我们在一开始的时候配置的线程模型 workerGroup，调 workerGroup 的 schedule 方法即可实现定时任务逻辑
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }
}
