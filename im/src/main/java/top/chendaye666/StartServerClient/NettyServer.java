package top.chendaye666.StartServerClient;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 首先看到，我们创建了两个NioEventLoopGroup，这两个对象可以看做是传统IO编程模型的两大线程组，
 * bossGroup表示监听端口，accept 新连接的线程组，workerGroup表示处理每一条连接的数据读写的线程组，
 *
 * 接下来 我们创建了一个引导类 ServerBootstrap，这个类将引导我们进行服务端的启动工作，直接new出来开搞。
 * 我们通过.group(bossGroup, workerGroup)给引导类配置两大线程组，这个引导类的线程模型也就定型了。
 * 然后，我们指定我们服务端的 IO 模型为NIO，我们通过.channel(NioServerSocketChannel.class)来指定 IO 模型，
 *
 * 接着，我们调用childHandler()方法，给这个引导类创建一个ChannelInitializer，这里主要就是定义后续每条连接的数据读写，业务处理逻辑，
 * ChannelInitializer这个类中，我们注意到有一个泛型参数NioSocketChannel，
 * 这个类呢，就是 Netty 对 NIO 类型的连接的抽象，而我们前面NioServerSocketChannel也是对 NIO 类型的连接的抽象，
 * NioServerSocketChannel和NioSocketChannel的概念可以和 BIO 编程模型中的ServerSocket以及Socket两个概念对应上
 */
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                    }
                });

//        serverBootstrap.bind(8000);
        bind(serverBootstrap, 8000);


        //其他服务端启动方式
        // handler() 方法
        //handler()方法呢，可以和我们前面分析的childHandler()方法对应起来，
        // childHandler()用于指定处理新连接数据的读写处理逻辑，handler()用于指定在服务端启动过程中的一些逻辑，
        // 通常情况下呢，我们用不着这个方法。
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            protected void initChannel(NioServerSocketChannel ch){
                System.out.println("服务端启动中");
            }
        });

        // attr() 方法
        // attr()方法可以给服务端的 channel，也就是NioServerSocketChannel指定一些自定义属性，
        // 然后我们可以通过channel.attr()取出这个属性，比如，上面的代码我们指定我们服务端channel的一个serverName属性，
        // 属性值为nettyServer，其实说白了就是给NioServerSocketChannel维护一个map而已，通常情况下，我们也用不上这个方法。
        serverBootstrap.attr(AttributeKey.newInstance("serverName"), "nettyServer");

        // childAttr() 方法
        // childAttr可以给每一条连接指定自定义属性，然后后续我们可以通过channel.attr()取出该属性。
        serverBootstrap.childAttr(AttributeKey.newInstance("clientKey"), "clientValue");

        // childOption() 方法
        // childOption()可以给每条连接设置一些TCP底层相关的属性，比如上面，我们设置了两种TCP属性，其中
        //ChannelOption.SO_KEEPALIVE表示是否开启TCP底层心跳机制，true为开启
        //ChannelOption.TCP_NODELAY表示是否开启Nagle算法，true表示关闭，false表示开启，
        // 通俗地说，如果要求高实时性，有数据发送时就马上发送，就关闭，如果需要减少发送次数减少网络交互，就开启。
        serverBootstrap
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        // option() 方法
        // 除了给每个连接设置这一系列属性之外，我们还可以给服务端channel设置一些属性，最常见的就是so_backlog，如下设置
        // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);

    }

    /**
     * 绑定端接直到成功
     * 运行之后可以看到效果，最终会发现，端口成功绑定了在1024，从 1000 开始到 1023，端口均绑定失败了，
     * 这是因为在我的 MAC 系统下，1023 以下的端口号都是被系统保留了，需要 ROOT 权限才能绑定
     * @param bootstrap
     * @param port
     */
    public static void bind(final ServerBootstrap bootstrap, final int port){
        bootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()){
                    System.out.println("绑定端口："+port+"成功！");
                }else{
                    System.out.println("绑定端口："+port+"失败！尝试绑定端口"+(port+1));
                    bind(bootstrap, port + 1);
                }
            }
        });
    }
}
