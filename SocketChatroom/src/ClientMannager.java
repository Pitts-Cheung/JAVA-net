import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientMannager {

    private ClientMannager() {
    }

    public static CopyOnWriteArrayList<ChatSocket> sockets = new CopyOnWriteArrayList<>();

    //向其他客户端发送数据
    public static void sendAll(ChatSocket chatSocket, String send) {
        for (ChatSocket socket : sockets) {
            if (!chatSocket.equals(socket)) {
                socket.send(send);
            }
        }
    }
}