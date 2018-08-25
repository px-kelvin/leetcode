package px.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     * @param port
     */
    public MultiplexerTimeServer(int port){
        try{
            selector  = Selector.open();
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Time server start :"+port);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void stop(){
        this.stop=true;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                //休眠时间为1秒，每隔1秒被唤醒一次
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()){
                    key=iterator.next();
                    iterator.remove();
                    handleInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isValid()){
            if(selectionKey.isAcceptable()){
                ServerSocketChannel scc= (ServerSocketChannel) selectionKey.channel();
                SocketChannel accept = scc.accept();
                accept.configureBlocking(false);
                accept.register(selector,SelectionKey.OP_READ);
            }
            if(selectionKey.isReadable()){
                //read the data
                SocketChannel scc= (SocketChannel) selectionKey.channel();
                //先开启1M的缓冲区
                ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                //channel会把自身的内容读取到byteBuffer中
                int read = scc.read(byteBuffer);
                if(read>0){
                    //将缓冲区当前的limit设置为position
                    byteBuffer.flip();
                    byte[] bytes=new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body=new String(bytes,"UTF-8");
                    System.out.println("receive body "+body);
                    doWrite(scc);
                }
            }
        }
    }
    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte [] bytes="Hello".getBytes();
        ByteBuffer byteBuffer=ByteBuffer.allocate(bytes.length);
        //将直接数组复制到缓冲区中
        byteBuffer.put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }
}
