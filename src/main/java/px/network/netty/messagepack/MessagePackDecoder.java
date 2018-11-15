package px.network.netty.messagepack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int i = byteBuf.readableBytes();
        final byte[] array=new byte[i];
        byteBuf.getBytes(byteBuf.readerIndex(),array,0,i);
        MessagePack messagePack=new MessagePack();
        list.add(messagePack.read(array));
    }
}
