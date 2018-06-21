import java.io.*;
import java.net.Socket;

public class ChatSocket implements Runnable{
    private Socket socket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public ChatSocket(Socket socket) {
        this.socket = socket;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void send(String send) { //��ͻ��˷�������
        try {
            outputStream.writeUTF(send);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() { //ѭ����ȡ�ͻ��˷���������
        String accept = null;

        while (true) {
            try {
                accept = inputStream.readUTF();

                ClientMannager.sendAll(this, accept);
            } catch (IOException e) {
                ClientMannager.sockets.remove(this);
            }
        }
    }
}