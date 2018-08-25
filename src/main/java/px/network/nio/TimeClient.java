package px.network.nio;

import java.io.IOException;

public class TimeClient {
    public static void main(String[] args) throws IOException {
        MultiplexerTimeClient multiplexerTimeClient=new MultiplexerTimeClient("127.0.0.1",8080);
        new Thread(()->multiplexerTimeClient.run()).start();
    }
}
