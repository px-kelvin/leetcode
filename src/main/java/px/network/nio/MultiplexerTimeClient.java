package px.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeClient extends Thread {

    private Selector selector;

    private SocketChannel socketChannel;

    private volatile boolean stop;

    private String host;

    private int port;


    public MultiplexerTimeClient(String host,int port) throws IOException {
        selector=Selector.open();
        socketChannel=SocketChannel.open();
        socketChannel.configureBlocking(false);
        this.host=host;
        this.port=port;
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey  key=null;
                while (iterator.hasNext()){
                    key=iterator.next();
                    iterator.remove();
                    handlerInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerInput(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isValid()){
            SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
            if(selectionKey.isConnectable()){
                if(socketChannel.finishConnect()){
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    doWrite(socketChannel);
                }else{
                    System.exit(1);
                }
            }
            if(selectionKey.isReadable()){
                ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                int read = socketChannel.read(byteBuffer);
                if(read>0){
                    byteBuffer.flip();
                    byte[] bytes=new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body=new String(bytes,"UTF-8");
                    System.out.println("Now is "+body);
                    this.stop=true;
                }else if(read<0){
                    selectionKey.cancel();
                    socketChannel.close();
                }else{

                }
            }
        }
    }

    private void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        byte [] req="HELLO YOU".getBytes();
        ByteBuffer byteBuffer=ByteBuffer.allocate(req.length);
        byteBuffer.put(req);
        byteBuffer.flip();
        sc.write(byteBuffer);
        if(!byteBuffer.hasRemaining()){
            System.out.println("send msg success");
        }
    }
}
