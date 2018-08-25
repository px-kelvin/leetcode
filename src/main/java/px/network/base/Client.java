package px.network.base;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket socket=new Socket("localhost",8888)){
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter=new PrintWriter(outputStream);
            printWriter.print("hei");
            printWriter.flush();
            socket.shutdownOutput();

            InputStream inputStream=socket.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String temp=null;
            String info="";
            while ((temp=bufferedReader.readLine())!=null){
                info+=temp;
                System.out.println("接收到消息"+info);
            }
        }

    }
}
