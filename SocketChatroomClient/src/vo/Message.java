package vo;
import java.io.Serializable;

public class Message implements Serializable{
	private String type;
	//消息内容
	private Object content;
	//接收方 如果是所有人，定义为“ALL”
	private String to;
	//发送方 
	private String from;
	public void setType(String type) {
		this.type=type;
	}
	public void setContent(Object content) {
		this.content=content;
	}
	public void setTo(String to) {
		this.to=to;
	}
	public void setFrom(String from) {
		this.from=from;
	}
	public String getType() {
		return this.type;
	}
	public Object getContent() {
		return this.content;
	}
	public String getTo() {
		return this.to;
	}
	public String getFrom() {
		return this.from;
	}
}