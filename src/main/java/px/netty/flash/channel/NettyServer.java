package px.netty.flash.channel;

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
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //处理逻辑从A->B-C
                        ch.pipeline().addLast(new ServerHandlerA());
                        ch.pipeline().addLast(new ServerHandlerB());
                        ch.pipeline().addLast(new ServerHandlerC());
                        //处理逻辑从F-
                        ch.pipeline().addLast(new ServerHandlerD());
                        ch.pipeline().addLast(new ServerHandlerE());
                        ch.pipeline().addLast(new ServerHandlerF());
                    }
                });
        ChannelFuture sync = serverBootstrap.bind(8888).sync();
        sync.channel().closeFuture().sync();
    }

}

class ServerHandlerA extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println("A read it :"+byteBuf.toString(Charset.forName("utf-8")));
        super.channelRead(ctx,msg);
    }
}
class ServerHandlerB extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println("B read it :"+byteBuf.toString(Charset.forName("utf-8")));
        super.channelRead(ctx,msg);
    }
}
class ServerHandlerC extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println("C read it :"+byteBuf.toString(Charset.forName("utf-8")));
        ctx.channel().writeAndFlush("");
    }
}
class ServerHandlerD extends ChannelOutboundHandlerAdapter{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("D write it :");
        super.write(ctx, msg, promise);
    }

}
class ServerHandlerE extends ChannelOutboundHandlerAdapter{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("E write it :");
        super.write(ctx, msg, promise);
    }
}
class ServerHandlerF extends ChannelOutboundHandlerAdapter{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("F write it :");
        super.write(ctx, msg, promise);
    }
}
