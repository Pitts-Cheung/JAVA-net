package vo;
/*
 ���л����ǽ�һ�������״̬������������������������Ȼ�����ʵ���ʱ���ٻ�á�

���л���Ϊ���󲿷֣����л��ͷ����л������л���������̵ĵ�һ���֣������ݷֽ���ֽ������Ա�洢���ļ��л��������ϴ��䡣
�����л����Ǵ��ֽ������ع����󡣶������л�����Ҫ��������������ת�����ֽڱ�ʾ����ʱ��Ҫ�ָ�����
 */
import java.io.Serializable;

public class Message implements Serializable{
	private String type;
	//��Ϣ����
	private Object content;
	//���շ� ����������ˣ�����Ϊ��ALL��
	private String to;
	//���ͷ� 
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
