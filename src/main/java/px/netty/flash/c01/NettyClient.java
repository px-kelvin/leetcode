package px.netty.flash.c01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.Date;

public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //pipeline()返回的是和这条连接有关的责任链
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        bootstrap.connect("127.0.0.1",8888).addListener(future -> {
           if(future.isSuccess()){
               System.out.println("连接成功");
           } else{
               System.out.println("连接失败");
           }
        }).sync().channel().closeFuture().sync();
    }


}
//逻辑处理器
class ClientHandler extends ChannelInboundHandlerAdapter{
    /**
     * 连接成功建立后 调用的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date()+":客户端写出数据");
        //获取一个ByteBuffer的内存管理器，再分配buffer
        ByteBuf buffer = ctx.alloc().buffer();
        byte[] bytes = "hello".getBytes("UTF-8");
        buffer.writeBytes(bytes);
        ctx.channel().writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf= (ByteBuf) msg;
        System.out.println("read it : "+buf.toString(Charset.forName("utf-8")));
    }
}