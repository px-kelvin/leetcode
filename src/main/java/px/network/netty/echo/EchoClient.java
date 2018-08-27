package px.network.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import px.network.netty.TimeClientHandler;

public class EchoClient {

    public void connect(int port,String host){
        try(EventLoopGroup group=new NioEventLoopGroup()) {

            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,Boolean.TRUE)

                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf byteBuf= Unpooled.copiedBuffer("$_".getBytes());
                            //根据$_去去除粘包的问题
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,byteBuf));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture future=bootstrap.connect(host,port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        EchoClient echoClient=new EchoClient();
        echoClient.connect(8888,"127.0.0.1");
    }
}
