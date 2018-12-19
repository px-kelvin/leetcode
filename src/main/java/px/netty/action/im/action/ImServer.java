package px.netty.action.im.action;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class ImServer {

    private  ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    public ImServer(){
        bootstrap=new ServerBootstrap();
        bossGroup=new NioEventLoopGroup();
        workGroup=new NioEventLoopGroup();
        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(65536));
                        ch.pipeline().addLast(new IdleStateHandler(8,10,12));
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/im"));
                        ch.pipeline().addLast(new ImHandler());
                    }
                });
    }

    public void start(String ip,Integer host){
        try {
            ChannelFuture sync = bootstrap.bind(ip, host).addListener((r)->{
                if(r.isSuccess()){
                    System.out.println("im server started");
                }
            }).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
