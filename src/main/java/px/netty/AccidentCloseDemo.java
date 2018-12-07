package px.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.Signal;

/**
 * 这个demo会在启动后过一秒左右就退出
 * 首先要清楚daemon线程
 * 如果jvm中只有守护线程在执行，那么会退出虚拟机，如果存在非守护线程，那么就不会退出
 *
 * 在netty中调用绑定服务端端口，并不是在调用方的线程，而是在NioEventLoop线程执行，通过分析NioEventLoop源码
 */
public class AccidentCloseDemo {

    public static void main(String[] args) {
        //TCP连接接入线程池
        try(EventLoopGroup bossGroup=new NioEventLoopGroup();
            //网络IO读写的工作线程池
            EventLoopGroup workGroup=new NioEventLoopGroup()) {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });
            //同步阻塞，等待端口的绑定结果，接着执行finally关闭NioEventLoop
            ChannelFuture sync = bootstrap.bind(18080).sync();
          /*TODO 解决方案1：加上这行代码，使用visualVm分析可以发现，java.lang.Thread.State: WAITING (on object monitor)
          locked <0x0000000781f07ba0> (a io.netty.channel.AbstractChannel$CloseFuture)
          main函数被阻塞在关闭接口closeFuture(),等待channel关闭*/
          //  sync.channel().closeFuture().sync();
            sync.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    /**
                     * TODO 解决方案2:将bossGroup和workGroup的shutdownGracefully()加到这里 最好是这么做
                     */
                    System.out.println(future.channel().toString()+"close");
                    bossGroup.shutdownGracefully();
                }
            });
        }catch (Exception e){

        }
        //
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Shutdown execute start...");
        }));


    }

}
