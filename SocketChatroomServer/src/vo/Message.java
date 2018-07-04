package vo;
/*
 序列化就是将一个对象的状态（各个属性量）保存起来，然后在适当的时候再获得。

序列化分为两大部分：序列化和反序列化。序列化是这个过程的第一部分，将数据分解成字节流，以便存储在文件中或在网络上传输。
反序列化就是打开字节流并重构对象。对象序列化不仅要将基本数据类型转换成字节表示，有时还要恢复数据
 */
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
