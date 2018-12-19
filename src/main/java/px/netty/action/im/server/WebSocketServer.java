package px.netty.action.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import px.netty.action.im.handler.SocketServerHandler;
import px.netty.action.im.service.HttpService;
import px.netty.action.im.service.WebSocketService;

import java.util.HashMap;
import java.util.Map;

public class WebSocketServer implements HttpService,WebSocketService {

    //params of handshaker
    private static final AttributeKey<WebSocketServerHandshaker> ATTR_HANDER_SHAKER=AttributeKey.newInstance("ATTR_KEY_CHANNEL_ID");

    private static final int MAX_CONTENT_LENGTH = 65536;

    /**
     * 请求类型常量
     */
    private static final String WEBSOCKET_UPGRADE = "websocket";
    private static final String WEBSOCKET_CONNECTION = "Upgrade";
    private static final String WEBSOCKET_URI_ROOT_PATTERN = "ws://%s:%d";

    /**
     * 用户字段
     */
    private String host;
    private int port;

    private Map<ChannelId,Channel> channelMap=new HashMap<>();

    public WebSocketServer(String host,int port){
        this.host=host;
        this.port=port;
    }

    public void start(){
        EventLoopGroup boosGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        channelMap.put(ch.id(),ch);
                        System.out.println(String.format("new channel : %s",ch));
                        ch.closeFuture().addListener((ChannelFutureListener) future -> {
                            System.out.println(String.format("channel close future : %s",future));
                            channelMap.remove(future.channel().id());
                        });
                        //add http codec
                        pipeline.addLast(new HttpServerCodec());
                        //aggregator
                        pipeline.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));

                        pipeline.addLast(new ChunkedWriteHandler());

                        pipeline.addLast(new SocketServerHandler(WebSocketServer.this,WebSocketServer.this));
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(host, port).addListener((ChannelFutureListener) future1 -> {
                if(future1.isSuccess()){
                    System.out.println("webSocket started");
                }
            }).sync();
            future.channel().closeFuture().addListener((ChannelFutureListener) future12 -> System.out.println(String.format("server channel %s closed", future12.channel()))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("error to bind port ");
        }finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
        System.out.println("webSocket shutdown");

    }

    @Override
    public void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {

    }

    @Override
    public void handlerSocketFrame(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {

    }

    private boolean isWebSocketUpgrade(FullHttpRequest request){
        HttpHeaders headers = request.headers();
        return request.method().equals("GET");
    }

}
