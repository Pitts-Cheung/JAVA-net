import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientView extends JFrame implements ActionListener, KeyListener, Runnable {
    private JTextArea textArea;
    private JTextField textField, tfName;
    private JButton btnSend, btnId;
    private JLabel label;
    private JPanel jp1, jp2;
    public boolean isConnect = false;
    private Socket socket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private JScrollPane scrollPane;
    private static ClientView view;

    public JTextArea getTextArea() {
        return textArea;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }
    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public static void main(String[] args) {
        view = new ClientView();
        ServiceView.clientViews.add(view);
        Thread thread = new Thread(view);
        thread.start();
    }

    public ClientView() {
        initView();
        try {
            socket = new Socket("localhost", 9090);//连接本地服务器

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        textArea = new JTextArea(20, 20);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setFont(new Font("黑体",Font.PLAIN,20));
        textField = new JTextField(50);
        textField.setFont(new Font("黑体",Font.BOLD,20));
        textField.addKeyListener(this);
        btnSend = new JButton("发送");
        btnSend.setFont(new Font("黑体",Font.BOLD,20));
        btnSend.addActionListener(this);
        label = new JLabel("昵称：");
        label.setFont(new Font("黑体",Font.BOLD,20));
        tfName = new JTextField(8);
        tfName.setFont(new Font("黑体",Font.BOLD,20));
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp1.add(label);
        jp1.add(tfName);
        tfName.setText("");
        jp1.setLayout(new FlowLayout(FlowLayout.CENTER));
        jp2.add(textField);
        jp2.add(btnSend);
        jp2.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(jp1, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        setTitle("聊天室");
        setSize(800, 800);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() { //窗口关闭后断开连接
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (socket != null)
                        socket.close();
                    if (inputStream!= null)
                        inputStream.close();
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSend) {
            sendMsg();
        }
    }

    private void sendMsg() {
        try {
            String s = textField.getText();
            if (!s.equals("")) { //发送数据
                textField.setText("");
                textArea.append("我(" + tfName.getText() +  "):\r\n" + s + "\r\n");
                outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(tfName.getText() + "#" + s); 
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMsg();
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void run() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                String[] s = inputStream.readUTF().split("#");
                textArea.append(s[0] + ":\r\n" + s[1] + "\r\n");
            }
        } catch (IOException e) {
        }

    }
}