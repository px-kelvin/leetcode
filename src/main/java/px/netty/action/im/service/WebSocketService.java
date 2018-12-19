package px.netty.action.im.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketService {



    //handler websocket frame
    void handlerSocketFrame(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame);

}
