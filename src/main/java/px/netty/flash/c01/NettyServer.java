package px.netty.flash.c01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class NettyServer {


    public static void main(String[] args) throws InterruptedException {
        //负责接收连接
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        //负责处理连接
        EventLoopGroup workGroup=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workGroup)
                //指定服务端的服务模块为Nio
                .channel(NioServerSocketChannel.class)
                //对NIO类型连接的抽象,handler方法用于指定启动中的一些逻辑
//                .handler(new ChannelInitializer<NioServerSocketChannel>() {
//                    @Override
//                    protected void initChannel(NioServerSocketChannel ch) throws Exception {
//                        System.out.println("服务端启动中");
//                    }
//                })
                //指定新连接的读写逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                       ch.pipeline().addLast(new ServerHandler());
                    }
                });
        ChannelFuture sync = serverBootstrap.bind(8888).sync();
        sync.channel().closeFuture().sync();
    }

}

class ServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println("read it :"+byteBuf.toString(Charset.forName("utf-8")));
        ByteBuf buffer = ctx.alloc().buffer();
        byte[] bytes = "give you something".getBytes();
        buffer.writeBytes(bytes);
        ctx.writeAndFlush(buffer);
    }
}
