package px.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

    public void bind(int port) {
        //配置服务端的NIO线程组,它包含了一组NIO线程，专门用于网络事件的处理，实际上就是reactor线程组
        //一个用于服务端接收客户端的连接，一个用于SocketChannel的网络读写
        try( EventLoopGroup bossGroup=new NioEventLoopGroup();
             EventLoopGroup workGroup=new NioEventLoopGroup();) {
            //启动NIO服务端的辅助启动类
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChildChannelHandler());
            //创建端口，同步等待成功
            ChannelFuture f=serverBootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) {
        TimeServer timeServer=new TimeServer();
        timeServer.bind(8080);
    }

}
