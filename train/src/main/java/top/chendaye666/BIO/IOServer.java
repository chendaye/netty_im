package top.chendaye666.BIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *客户端每隔两秒发送一个带有时间戳的 "hello world" 给服务端，服务端收到之后打印。
 *
 * Server 端首先创建了一个serverSocket来监听 8000 端口，然后创建一个线程，
 * 线程里面不断调用阻塞方法 serversocket.accept();获取新的连接，
 * 见(1)，当获取到新的连接之后，给每条连接创建一个新的线程，这个线程负责从该连接中读取数据，
 * 见(2)，然后读取数据是以字节流的方式，见(3)。
 */
public class IOServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8082);
        //线程+死循环
        new Thread(() -> {
            while (true){
                try {
                    // 阻塞方法获取新的连接
                    Socket socket = serverSocket.accept();
                    // 起一个新的线程来处理请求
                    new Thread(() ->{
                        try {
                            int len;
                            byte[] data = new byte[1024]; // 1M的数据容器
                            // 按字节方式读取数据
                            InputStream inputStream = socket.getInputStream();
                            while ((len = inputStream.read(data)) != -1){
                                System.out.println(new String(data, 0, len));
                            }
                        }catch (IOException e){

                        }
                    }).start();
                }catch (IOException e){
                    e.getMessage();
                }
            }
        }).start();
    }
}
