package px.netty.action.im.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpService {






    //handler http request
    void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request);

}
