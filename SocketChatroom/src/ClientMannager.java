import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientMannager {

    private ClientMannager() {
    }

    public static CopyOnWriteArrayList<ChatSocket> sockets = new CopyOnWriteArrayList<>();

    //�������ͻ��˷�������
    public static void sendAll(ChatSocket chatSocket, String send) {
        for (ChatSocket socket : sockets) {
            if (!chatSocket.equals(socket)) {
                socket.send(send);
            }
        }
    }
}