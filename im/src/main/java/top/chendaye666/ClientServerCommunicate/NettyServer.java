package top.chendaye666.ClientServerCommunicate;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * todo: NIO 三要素
 *      - selector (select、poll、epoll)
 *      - channel （连接、文件描述符）
 *      - buffer  （缓冲区）
 */
public class NettyServer extends ChannelInboundHandlerAdapter {
    // 向buffer中写数据
    private ByteBuf getByteBuf(ChannelHandlerContext ctx){
        byte[] bytes = "这边是服务端数据!".getBytes(StandardCharsets.UTF_8);

        ByteBuf buffer = ctx.alloc().buffer(); // 分配 buffer

        buffer.writeBytes(bytes); // 写buffer

        return buffer;  // 返回 buffer
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 服务端获取的客户端数据
        ByteBuf byteBuf = (ByteBuf) msg;

        System.out.println(new Date() + ": 服务端读到数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));

        // 回复数据到客户端
        System.out.println(new Date() + ": 服务端写出数据");
        ByteBuf out = getByteBuf(ctx); // 要回复客户端的内容
        ctx.channel().writeAndFlush(out);
    }
}
