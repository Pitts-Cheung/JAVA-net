package app;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;
import main.Main;
import util.Conf;
import util.GUIUtil;
import vo.Customer;
import vo.Message;

public class LoginFrame extends JFrame implements ActionListener{
	//定义各控件
	private Icon welcomeIcon=new ImageIcon("welcome.png");
	private JLabel lbWelcome=new JLabel(welcomeIcon);
	private JLabel lbAccount=new JLabel("请您输入账号");
	private JTextField tfAccount=new JTextField(10);
	private JLabel lbPassword=new JLabel("请您输入密码");
	private JPasswordField pfPassword=new JPasswordField(10);
	private JButton btLogin=new JButton("登录");
	private JButton btRegister=new JButton("注册");
	private JButton btExit=new JButton("退出");
	private Socket socket=null;
	private ObjectInputStream ois=null;
	private ObjectOutputStream oos=null;
	
	public LoginFrame() {
		//界面初始化
		super("登录");
		this.setLayout(new FlowLayout());
		this.add(lbWelcome);
		this.add(lbAccount);
		this.add(tfAccount);
		this.add(lbPassword);
		this.add(pfPassword);
		this.add(btLogin);
		this.add(btRegister);
		this.add(btExit);
		this.setSize(280,200);
		GUIUtil.toCenter(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		//增加监听
		btLogin.addActionListener(this);
		btRegister.addActionListener(this);
		btExit.addActionListener(this);
	}
	
	public void login() {
		String account=tfAccount.getText();
		Customer cus=new Customer();
		cus.setAccount(account);
		cus.setPassword(new String(pfPassword.getPassword()));
		Message msg=new Message();
		msg.setType(Conf.LOGIN);
		msg.setContent(cus);
		try {
			socket=new Socket(Main.serverIP,Main.port);
			//以下两句有顺序要求
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
			oos.writeObject(msg);
			Message receiveMsg=(Message)ois.readObject();
			String type=receiveMsg.getType();
			if(type.equals(Conf.LOGINFALL)) {
				JOptionPane.showMessageDialog(this, "登录失败");
				socket.close();
				return;
			}
			JOptionPane.showMessageDialog(this, "登录成功");
			new ChatFrame(ois,oos,receiveMsg,account);
			this.dispose();
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, "网络连接异常");
			System.exit(-1);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btLogin) {
			this.login();
		}else if(e.getSource()==btRegister) {
			this.dispose();
			new RegisterFrame();
		}else {
			JOptionPane.showMessageDialog(this, "感谢使用");
			System.exit(0);
		}
	}
}
