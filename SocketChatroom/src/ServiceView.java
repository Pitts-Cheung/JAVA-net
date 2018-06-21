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
        btnOpen = new JButton("打开服务器");
        btnStop = new JButton("关闭服务器");
        btnStop.setEnabled(false);
        btnOpen.addActionListener(this);
        btnStop.addActionListener(this);
        label = new JLabel("服务器停止工作");
        add(label);
        add(btnOpen);
        add(btnStop);
        setTitle("服务器");
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

    public void open() { //开启服务器
        service = new Service();
        Thread thread = new Thread(service);
        thread.start();
        label.setText("服务器正在运行");
        btnOpen.setEnabled(false);
        btnStop.setEnabled(true);
    }

    public void stop() { //关闭服务器
        label.setText("服务器已关闭");
        btnOpen.setEnabled(true);
        btnStop.setEnabled(false);
        try {
            synchronized (ClientMannager.sockets) { //关闭各个连接
                for (ChatSocket socket : ClientMannager.sockets) {
                    socket.getInputStream().close();
                    socket.getOutputStream().close();
                }
                ClientMannager.sockets.removeAllElements();
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