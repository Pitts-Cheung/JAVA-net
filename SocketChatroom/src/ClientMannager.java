import java.util.Vector;

public class ClientMannager {

    private ClientMannager() {
    }

    public static Vector<ChatSocket> sockets = new Vector<>();

    //�������ͻ��˷�������
    public static void sendAll(ChatSocket chatSocket, String send) {
        for (ChatSocket socket : sockets) {
            if (!chatSocket.equals(socket)) {
                socket.send(send);
            }
        }
    }
}