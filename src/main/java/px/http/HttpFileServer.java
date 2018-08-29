package px.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {


    private static final String DEFAULT_URL="/src/main/java/px/http";

    public void run(int port,String url){
        try(EventLoopGroup bossGroup=new NioEventLoopGroup();
            EventLoopGroup workerGroup =new NioEventLoopGroup()){
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //http请求消息解析器 它负责把字节解码成Http请求。
                            socketChannel.pipeline().addLast("http-decoder",new HttpRequestDecoder());
                            //将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
                            socketChannel.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            //对相应消息进行编码
                            socketChannel.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            //支持异步发送大的字节码
                            socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(DEFAULT_URL));

                        }
                    });
            ChannelFuture sync = serverBootstrap.bind("127.0.0.1", port).sync();
            System.out.println("已启动");
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        HttpFileServer httpFileServer=new HttpFileServer();
        httpFileServer.run(8888,"127.0.0.1");
    }
}
