package px.network.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter {

    private int count=0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf= (ByteBuf) msg;
        byte [] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body= new String(req,"UTF-8");
        ++count;
        System.out.println("The time server receive order:"+body+"count"+count);
        String returnBody="Hello";
        ByteBuf resp = Unpooled.copiedBuffer(returnBody.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
