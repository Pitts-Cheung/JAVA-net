package Mail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JFrame loginFrame;
    private JLabel userNameLabel;
    private JTextField userName;
    private JLabel userPasswordLabel;
    private JPasswordField userPassword;
    private JButton confirm;
    private String userNameStr;
    private String userPasswordStr;

    public Login() {
        loginFrame = new JFrame("µÇÂ½ÓÊÏä¿Í»§¶Ë");
        userNameLabel = new JLabel("ÓÊÏä");
        userNameLabel.setFont(new Font("ºÚÌå",Font.BOLD,30));
        userName = new JTextField();
        userName.setFont(new Font("ºÚÌå",Font.BOLD,30));
        userPasswordLabel = new JLabel("ÃÜÂë");
        userPasswordLabel.setFont(new Font("ºÚÌå",Font.BOLD,30));
        userPassword = new JPasswordField();
        userPassword.setFont(new Font("Arial",Font.BOLD,30));
        confirm = new JButton("µÇÂ¼");
        confirm.setFont(new Font("ºÚÌå",Font.BOLD,30));

        loginFrame.setLayout(null);

        loginFrame.add(userNameLabel);
        loginFrame.add(userName);
        loginFrame.add(userPasswordLabel);
        loginFrame.add(userPassword);
        loginFrame.add(confirm);

        loginFrame.setBounds(0, 0, 600, 400);
        loginFrame.setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 280/2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 170/2
        );
        userNameLabel.setBounds(80, 40, 120, 40);
        userName.setBounds(200, 40, 300, 40);
        userPasswordLabel.setBounds(80, 100, 120, 40);
        userPassword.setBounds(200, 100, 300, 40);
        confirm.setBounds(210, 200, 180, 60);

        loginFrame.setVisible(true);

        loginFrame.getRootPane().setDefaultButton(confirm);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userNameStr = userName.getText();
                userPasswordStr = String.valueOf(userPassword.getPassword());
                System.out.println("user name: " + userNameStr);
//                System.out.println("password: " + userPasswordStr);
                System.out.print("password: ");
                for(int i = 0; i < userPasswordStr.length(); i++){
                    System.out.print('*');
                }
                System.out.print('\n');
                loginFrame.setVisible(false);
                new MainFrame(userNameStr, userPasswordStr);
            }
        });
    }
}
