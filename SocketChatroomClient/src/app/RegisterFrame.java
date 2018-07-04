package app;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;
import main.Main;
import util.*;
import vo.Customer;
import vo.Message;

public class RegisterFrame extends JFrame implements ActionListener{
	private JLabel lbAccount=new JLabel("请您输入账号");
	private JTextField tfAccount=new JTextField(10);
	private JLabel lbPassword1=new JLabel("请您输入密码");
	private JPasswordField pfPassword1=new JPasswordField(10);
	private JLabel lbPassword2=new JLabel("输入确认密码");
	private JPasswordField pfPassword2=new JPasswordField(10);
	private JLabel lbName=new JLabel("请您输入姓名");
	private JTextField tfName=new JTextField(10);
	//private JLabel lbDept=new JLabel("请您选择部门");
	//private JComboBox cbDept=new JComboBox();
	private JButton btLogin=new JButton("登录");
	private JButton btRegister=new JButton("注册");
	private JButton btExit=new JButton("退出");
	private Socket socket=null;
	private ObjectInputStream ois=null;
	private ObjectOutputStream oos=null;
	
	public RegisterFrame() {
		super("注册");
		this.setLayout(new FlowLayout());
		this.add(lbAccount);
		this.add(tfAccount);
		this.add(lbPassword1);
		this.add(pfPassword1);
		this.add(lbPassword2);
		this.add(pfPassword2);
		this.add(lbName);
		this.add(tfName);
		//this.add(lbDept);
		//this.add(cbDept);
		//cbDept.addItem("财务部");
		//cbDept.addItem("行政部");
		//cbDept.addItem("客户服务部");
		//cbDept.addItem("销售部");
		this.add(btRegister);
		this.add(btLogin);
		this.add(btExit);
		this.setSize(240,200);
		GUIUtil.toCenter(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		
		//增加监听
		btLogin.addActionListener(this);
		btRegister.addActionListener(this);
		btExit.addActionListener(this);
	}
	
	public void register() {
		Customer cus=new Customer();
		cus.setAccount(tfAccount.getText());
		cus.setPassword(new String(pfPassword1.getPassword()));
		cus.setName(tfName.getText());
		//cus.setDept((String)cbDept.getSelectedItem());
		Message msg=new Message();
		msg.setType(Conf.REGISTER);
		msg.setContent(cus);
		try {
			socket=new Socket(Main.serverIP,Main.port);
			//以下两句有顺序要求
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
			Message receiveMsg=null;
			oos.writeObject(msg);
			receiveMsg=(Message)ois.readObject();
			String type=receiveMsg.getType();
			if(type.equals(Conf.REGISTERFALL)) {
				JOptionPane.showMessageDialog(this, "注册失败");
			}else {
				JOptionPane.showMessageDialog(this, "注册成功");
			}
			socket.close();
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, "网络连接异常");
			System.exit(-1);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btRegister) {
			String password1=new String(pfPassword1.getPassword());
			String password2=new String(pfPassword2.getPassword());
			if(!password1.equals(password2)) {
				JOptionPane.showMessageDialog(this, "输入密码与确认密码不一致");
				return;
			}
			
			//连接到服务器并发送注册信息
			this.register();
		}else if(e.getSource()==btLogin) {
			this.dispose();
			new LoginFrame();
		}else {
			JOptionPane.showMessageDialog(this, "感谢使用");
			System.exit(0);
		}
	}
}
