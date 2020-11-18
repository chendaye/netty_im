package top.chendaye666.ClientServerCommunicate;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class NettyClient extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println(new Date() + "Client 写数据");

        // 1.获取数据
        ByteBuf buffer = getByteBuf(ctx);

        //写数据
        ctx.channel().writeAndFlush(buffer);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx){
        byte[] bytes = "你好 chendaye666".getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer(); // 分配缓冲区
        buffer.writeBytes(bytes); // 向缓冲区写byte
        return buffer; // 返回缓冲区
    }
}
