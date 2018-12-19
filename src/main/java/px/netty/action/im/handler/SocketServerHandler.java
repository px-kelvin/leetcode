package px.netty.action.im.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import px.netty.action.im.service.HttpService;
import px.netty.action.im.service.WebSocketService;

import java.util.logging.Logger;


public class SocketServerHandler extends SimpleChannelInboundHandler<Object> {


    private HttpService httpService;

    private WebSocketService webSocketService;

    //save connected channel
    private static ChannelGroup channels=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final Logger LOGGER=Logger.getLogger("SocketServerHandler");


    public SocketServerHandler(HttpService httpService,WebSocketService webSocketService){
        super();
        this.httpService=httpService;
        this.webSocketService=webSocketService;
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        channels.writeAndFlush(new TextWebSocketFrame(ctx.channel()+"上线了"));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        channels.writeAndFlush(new TextWebSocketFrame(ctx.channel()+"下线了"));
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            httpService.handlerHttpRequest(ctx, (FullHttpRequest) msg);
        }else if (msg instanceof WebSocketFrame){
            webSocketService.handlerSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        channels.remove(ctx.channel());
        ctx.close();
        LOGGER.info("error"+cause.getMessage());
    }
}
