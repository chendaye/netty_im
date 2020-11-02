package top.chendaye666;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *客户端每隔两秒发送一个带有时间戳的 "hello world" 给服务端，服务端收到之后打印。
 */
public class IOServer {
    public static void main(String[] args) throws Exception {
        final ServerSocket serverSocket = new ServerSocket(8888);
        //线程+死循环
        new Thread(() -> {
            while (true){
                try {
                    // 阻塞方法获取新的连接
                    final Socket socket = serverSocket.accept();
                    // 起一个新的线程来处理请求
                    new Thread(() ->{
                        try {
                            int len;
                            byte[] data = new byte[1024]; // 1M的数据容器
                            // 按字节方式读取数据
                            InputStream inputStream = socket.getInputStream();
                            len = inputStream.read(data);
                            while (len != -1){
                                len = inputStream.read(data)
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
