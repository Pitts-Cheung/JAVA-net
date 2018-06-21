import java.util.Vector;

public class ClientMannager {

    private ClientMannager() {
    }

    public static Vector<ChatSocket> sockets = new Vector<>();

    //向其他客户端发送数据
    public static void sendAll(ChatSocket chatSocket, String send) {
        for (ChatSocket socket : sockets) {
            if (!chatSocket.equals(socket)) {
                socket.send(send);
            }
        }
    }
}