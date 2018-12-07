package px.netty.flash.c02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class NianBaoClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap=new Bootstrap();
        EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {


                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NianBaoClientHandler());
                    }
                });
        ChannelFuture sync = bootstrap.connect("127.0.0.1", 8080).sync();
        sync.channel().closeFuture().sync();
    }
}
class NianBaoClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=0;i<1000;i++){
            byte[] bytes="你好，鸟大厦大火烧开的哈萨克号地块哈萨克电话卡是空的啊哈斯达康哈萨克电话卡sad很快撒谎地块哈萨克电话卡圣诞卡航空大厦客户端".getBytes(Charset.forName("utf8"));
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes(bytes);
            ctx.channel().writeAndFlush(buffer);
        }

    }




}
