package app;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Conf;
import util.FileOpe;
import vo.Customer;
import vo.Message;

//为某个客户端服务，负责接受、发送信息
public class ChatThread extends Thread{
	private Socket socket=null;
	private ObjectInputStream ois=null;
	private ObjectOutputStream oos=null;
	private Customer customer=null;
	private Server server;
	private boolean canRun=true;
	public ChatThread(Socket socket,Server server)throws Exception{
		this.socket=socket;
		this.server=server;
		oos=new ObjectOutputStream(socket.getOutputStream());
		ois=new ObjectInputStream(socket.getInputStream());
	}
	
	public void run() {
		try {
			while(canRun) {
				Message msg=(Message)ois.readObject();
				//分析之后转发
				String type=msg.getType();
				if(type.equals(Conf.LOGIN)) {
					this.handleLogin(msg);
				}else if(type.equals(Conf.REGISTER)) {
					this.handleRegister(msg);
				}else if(type.equals(Conf.MESSAGE)) {
					this.handleMessage(msg);
				}
			}
		}catch(Exception ex) {
			this.handleLogout();
		}
	}
	
	//处理登录信息
	public void handleLogin(Message msg)throws Exception{
		Customer loginCustomer=(Customer)msg.getContent();
		String account=loginCustomer.getAccount();
		String password=loginCustomer.getPassword();
		Customer cus=FileOpe.getCustomerByAccount(account);
		Message newMsg=new Message();
		if(cus==null||!cus.getPassword().equals(password)) {
			newMsg.setType(util.Conf.LOGINFALL);
			oos.writeObject(newMsg);
			canRun=false;
			socket.close();
			return;
		}
		this.customer=cus;
		//将该线程放入clients集合
		server.getClients().add(this);
		//将customer加入到userList中
		server.getUserList().add(this.customer);
		//注意，应该是将所有的在线用户都要转发给客户端
		newMsg.setType(util.Conf.USERLIST);
		newMsg.setContent(server.getUserList().clone());
		//将该用户登录的信息发个所有用户
		this.sendMessage(newMsg,Conf.ALL);
		server.setTitle("当前在线:"+server.getClients().size()+"人");
	}
	
	//将msg里面的内容以聊天信息形式转发
	public void handleRegister(Message msg)throws Exception{
		Customer registerCustomer=(Customer)msg.getContent();
		String account=registerCustomer.getAccount();
		Customer cus=FileOpe.getCustomerByAccount(account);
		Message newMsg=new Message();
		if(cus!=null) {
			newMsg.setType(Conf.REGISTERFALL);
		}else {
			String password=registerCustomer.getPassword();
			String name=registerCustomer.getName();
			//String dept=registerCustomer.getDept();
			FileOpe.insertCustomer(account,password,name);
			newMsg.setType(Conf.REGISTERSUCCESS);
			//发给注册用户
			oos.writeObject(newMsg);
		}
		//发给注册用户
		oos.writeObject(newMsg);
		canRun=false;
		socket.close();
	}
	
	//将msg里面的内容以聊天信息形式转发
	public void handleMessage(Message msg)throws Exception{
		String to=msg.getTo();
		sendMessage(msg,to);
	}
	
	//向所有其他客户端发送一个该客户下线的信息
	public void handleLogout() {
		Message logoutMessage=new Message();
		logoutMessage.setType(Conf.LOGOUT);
		logoutMessage.setContent(this.customer);
		//将它自己从clients中去掉
		server.getClients().remove(this);
		server.getUserList().remove(this.customer);
		try {
			sendMessage(logoutMessage,Conf.ALL);
			canRun=false;
			socket.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		server.setTitle("当前在线:"+server.getClients().size()+"人");
	}
	
	//将信息发给耨某个客户端
	public void sendMessage(Message msg,String to)throws Exception{
		for(ChatThread ct:server.getClients()) {
			if(ct.customer.getAccount().equals(to)||to.equals(Conf.ALL)) {
				ct.oos.writeObject(msg);
			}
		}
	}
}
