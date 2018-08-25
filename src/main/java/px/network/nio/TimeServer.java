package px.network.nio;

public class TimeServer {
    public static void main(String[] args) {
        int port=8080;
        MultiplexerTimeServer multiplexerTimeServer=new MultiplexerTimeServer(port);
        new Thread(()->multiplexerTimeServer.run()).start();
    }
}
