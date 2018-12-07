package px.netty.flash.c02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * 这个demo会出现 正常现象，黏包，拆包现象
 */
public class NianBaoServer {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap=new ServerBootstrap();
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();

        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NianBaoHandler());
                    }
                });
        ChannelFuture sync = bootstrap.bind(8080).sync();
        sync.channel().closeFuture().sync();
    }

}
class NianBaoHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println(new Date()+":读到数据->"+byteBuf.toString(Charset.forName("utf-8")));
    }
}
