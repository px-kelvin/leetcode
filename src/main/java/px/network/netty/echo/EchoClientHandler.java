package px.network.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

    private int count;

    static final String ECHO_REQ="Hi,You.$_";
    public EchoClientHandler(){

    }
    //客户端和服务端TCP建立后，会调用的方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       for(int i=1;i<10;i++)
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));

    }
    //服务器返回的应答消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body= (String) msg;
        System.out.println("now is "+body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
