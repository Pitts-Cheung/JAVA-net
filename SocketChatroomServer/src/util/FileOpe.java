package util;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import vo.Customer;

public class FileOpe {
	private static String fileName="cus.inc";
	private static Properties pps;
	static {
		pps=new Properties();
		FileReader reader=null;
		try {
			reader=new FileReader(fileName);
			pps.load(reader);
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "文件操作异常");
			System.exit(0);
		}finally {
			try {
				reader.close();
			}catch(Exception ex) {}
		}
	}
	
	private static void listInfo() {
		PrintStream ps=null;
		try {
			ps= new PrintStream(fileName);
			pps.list(ps);
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "文件操作异常");
			System.exit(0);
		}finally {
			try {
				ps.close();
			}catch(Exception ex) {}
		}
	}
	
	public static Customer getCustomerByAccount(String account) {
		Customer cus=null;
		String cusInfo=pps.getProperty(account);
		if(cusInfo!=null) {
			String[] infos=cusInfo.split("#");
			cus=new Customer();
			cus.setAccount(account);
			cus.setPassword(infos[0]);
			cus.setName(infos[1]);
			//cus.setDept(infos[2]);
		}
		return cus;
	}
	
	public static void insertCustomer(String account,String password,String name) {
		pps.setProperty(account, password+"#"+name);
		listInfo();
	}
}
