import java.io.IOException;
import java.net.*;
import javax.swing.JOptionPane;

public class Service implements Runnable{

    private ServerSocket serverSocket = null;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9090); //创建端口
            while (true) { //循地接收客户端的连接
                Socket socket = serverSocket.accept();
                JOptionPane.showMessageDialog(ServiceView.getView(), "客户端连接端口", "TIP", JOptionPane.INFORMATION_MESSAGE);
                ChatSocket chatSocket = new ChatSocket(socket); //新客户端连接
                ClientMannager.sockets.add(chatSocket); //往客户端管理器里添加客户
                Thread thread = new Thread(chatSocket); //启用线程使服务器开始不断接收客户端信息
                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器关闭");
        }
    }

}