package px.netty.action.im.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImHandler extends SimpleChannelInboundHandler<Object> {


    public static  ChannelGroup channels=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String,Channel> users=new ConcurrentHashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
       channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
    }


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object requestInfo) throws Exception {
        if(requestInfo instanceof WebSocketFrame){
            TextWebSocketFrame msg= (TextWebSocketFrame) requestInfo;
            String content=msg.text();
            JSONObject contentJson =null;
            try {
                contentJson=JSON.parseObject(content);
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(contentJson==null){
                writeError(ctx);
                return;
            }
            String userId = contentJson.getString("userId");
            String type = contentJson.getString("type");
            if(type==null){
                writeError(ctx);
            }
            if(type.equals("connect")){
                users.put("userId",ctx.channel());
            }
            if(type.equals("msg")){
                String pUserId = contentJson.getString("pUserId");
                String sendMsg=contentJson.getString("sendMsg");
                Channel pChannel = users.get(pUserId);
                if(pChannel!=null){
                    pChannel.writeAndFlush(new TextWebSocketFrame(sendMsg));
                    System.out.println(String.format("%s success to send msg %s to %s",userId,sendMsg,pUserId));
                }else{
                    System.out.println("user not online");
                    //todo save message to database
                }
            }
        }

    }

    private void writeError(ChannelHandlerContext ctx) {
        ByteBufAllocator alloc = ctx.alloc();
        ByteBuf buffer = alloc.buffer();
        buffer.writeBytes(new String("error").getBytes());
        ctx.channel().writeAndFlush(buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
        channels.remove(ctx.channel());
    }
}
