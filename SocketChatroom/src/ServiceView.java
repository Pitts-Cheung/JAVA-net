import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class ServiceView extends JFrame implements ActionListener{
    private JButton btnOpen, btnStop;
    private JLabel label;
    private Service service = null;
    public static ArrayList<ClientView> clientViews = new ArrayList<>();
    private static ServiceView view;

    public static ServiceView getView() {
        return view;
    }
    public static void main(String[] args) {
        view = new ServiceView();
    }

    public ServiceView() {
        initView();
    }

    private void initView() {
        btnOpen = new JButton("�򿪷�����");
        btnOpen.setFont(new Font("����",Font.BOLD,20));
        btnStop = new JButton("�رշ�����");
        btnStop.setFont(new Font("����",Font.BOLD,20));
        btnStop.setEnabled(false);
        btnOpen.addActionListener(this);
        btnStop.addActionListener(this);
        label = new JLabel("������ֹͣ����");
        label.setFont(new Font("����",Font.BOLD,20));
        add(label);
        add(btnOpen);
        add(btnStop);
        setTitle("������");
        setLayout(new GridLayout(3, 1, 0, 10));
        setSize(300, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOpen) {
            open();
        } else {
            stop();
        }
    }

    public void open() { //����������
        service = new Service();
        Thread thread = new Thread(service);
        thread.start();
        label.setText("��������������");
        btnOpen.setEnabled(false);
        btnStop.setEnabled(true);
    }

    public void stop() { //�رշ�����
        label.setText("�������ѹر�");
        btnOpen.setEnabled(true);
        btnStop.setEnabled(false);
        try {
            synchronized (ClientMannager.sockets) { //�رո�������
                for (ChatSocket socket : ClientMannager.sockets) {
                    socket.getInputStream().close();
                    socket.getOutputStream().close();
                }
                for (ChatSocket socket : ClientMannager.sockets) 
                	remove(this);
            }


            for (ClientView view : clientViews) {
                view.getInputStream().close();
                view.getOutputStream().close();
            }

            service.getServerSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}