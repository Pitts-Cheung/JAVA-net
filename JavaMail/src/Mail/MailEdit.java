package Mail;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

public class MailEdit {
    private String userNameStr;
    private String userPasswordStr;
    private String recipientNameStr;
    private JFrame mailEditFrame;
    private JTextField recipientAddrField;
    private JTextField mailSubjectField;
    private JLabel recipientAddrLabel;
    private JLabel mailSubjectLabel;
    private JLabel mailContentLabel;
    private JTextArea mailContentArea;
    private JScrollPane mailContentScrollPane;
    private JButton sendingBtn;
    private JButton attachmentBtn;
    private JLabel attachmentLabel;
    private final String sendingHost = "smtp.qq.com";
    private final String sendingProtocol = "smtp";
    private Properties sendingProperty;
    private Session mailSendingSession;
    private String filePathAndName;
    private Message sendingMsg;

    private void initUI() {
        mailEditFrame = new JFrame();
        recipientAddrField = new JTextField();
        recipientAddrLabel = new JLabel("收件人：");
        mailSubjectField = new JTextField();
        mailSubjectLabel = new JLabel("标题：");
        mailContentArea = new JTextArea();
        mailContentLabel = new JLabel("正文：");
        mailContentScrollPane = new JScrollPane(mailContentArea);
        sendingBtn = new JButton("发送");
        attachmentBtn = new JButton("选择附件");
        attachmentLabel = new JLabel("附件：");

        mailEditFrame.setBounds(0, 0, 500, 400);
        mailEditFrame.setLayout(null);
        mailEditFrame.setResizable(false);
        mailEditFrame.setTitle("发送邮件");

        mailEditFrame.add(recipientAddrField);
        recipientAddrField.setBounds(70, 20, 400, 26);

        mailEditFrame.add(recipientAddrLabel);
        recipientAddrLabel.setBounds(10, 20, 55, 26);

        mailEditFrame.add(mailSubjectField);
        mailSubjectField.setBounds(70, 50, 400, 26);

        mailEditFrame.add(mailSubjectLabel);
        mailSubjectLabel.setBounds(20, 50, 45, 26);

        mailEditFrame.add(mailContentScrollPane);
        mailContentScrollPane.setBounds(70, 80, 400, 200);
        mailContentArea.setLineWrap(true);
        mailContentArea.setWrapStyleWord(true);

        mailEditFrame.add(mailContentLabel);
        mailContentLabel.setBounds(20, 80, 45, 26);

        mailEditFrame.add(sendingBtn);
        sendingBtn.setBounds(400, 290, 70, 20);

        mailEditFrame.add(attachmentBtn);
        attachmentBtn.setBounds(20, 290, 70, 20);

        mailEditFrame.add(attachmentLabel);
        attachmentLabel.setBounds(20, 310, 200, 20);

        mailEditFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                mailEditFrame.setVisible(false);
            }
        });
        attachmentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(); // 创建文件对话框
                int returnValue = fileChooser.showOpenDialog(null);// 打开文件选择对话框
                if (returnValue == JFileChooser.APPROVE_OPTION) { // 判断是否选择了文件
                    File file = fileChooser.getSelectedFile(); // 获得文件对象
                    if (file.length() / 1024.0 / 1024 > 50.0) {
                        JOptionPane.showMessageDialog(null, "请选择小于等于50MB的文件。");
                        return;
                    }
                    filePathAndName = file.getAbsolutePath();// 获得文件的完整路径和文件名
                    attachmentLabel.setText("附件：" + file.getName());// 显示附件文件的名称
                }
            }
        });
        sendingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recipientNameStr = recipientAddrField.getText().trim();
                if(recipientNameStr == null || recipientNameStr.equals("")) {
                    JOptionPane.showMessageDialog(null, "收件人不能为空");
                    return;
                }
                try {
                    sendMimeMessage();
                }catch (Exception excpt){
                    excpt.printStackTrace();
                }
            }
        });
    }

    private void newSendingSession(){
        try {
            sendingProperty = new Properties();
            sendingProperty.setProperty("mail.transport.protocol", sendingProtocol);
            sendingProperty.setProperty("mail.smtp.host", sendingHost);
            sendingProperty.setProperty("mail.smtp.auth", "true");
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            sendingProperty.put("mail.smtp.ssl.enable", "true");
            sendingProperty.put("mail.smtp.ssl.socketFactory", sf);
            mailSendingSession = Session.getInstance(sendingProperty);
            mailSendingSession.setDebug(true); // 控制台输出调试信息
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
        }
    }

    private void sendMimeMessage() throws Exception {
        // 建立MimeMessage
        MimeMessage msg = new MimeMessage(mailSendingSession);
        // 填写基本信息
//        InternetAddress[] toAddrs = InternetAddress.parse(recipientNameStr, false);
        String[] toAddrs = recipientNameStr.split(";");
//        msg.setRecipients(Message.RecipientType.TO, toAddrs);
        for(int i = 0; i < toAddrs.length; i++){
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddrs[i]));
        }
        msg.setFrom(new InternetAddress(userNameStr));
        msg.setSubject(mailSubjectField.getText().trim());
        msg.setSentDate(new Date());
        // 建立multipart
        Multipart multipart = new MimeMultipart();
        MimeBodyPart mimeBodyPartText = new MimeBodyPart();
        mimeBodyPartText.setText(mailContentArea.getText());
        multipart.addBodyPart(mimeBodyPartText);
        // 添加附件
        if(filePathAndName != null && !filePathAndName.equals("")){
            MimeBodyPart mimeBodyPartAdjunct = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(filePathAndName);
            mimeBodyPartAdjunct.setDataHandler(new DataHandler(fileDataSource));
            mimeBodyPartAdjunct.setDisposition(Part.ATTACHMENT);
            String name = fileDataSource.getName();
            mimeBodyPartAdjunct.setFileName(MimeUtility.encodeText(name, "GBK", null));
            multipart.addBodyPart(mimeBodyPartAdjunct);
        }
        msg.setContent(multipart);
        // 建立Transport
        Transport transport = mailSendingSession.getTransport();
        transport.connect(sendingHost, userNameStr, userPasswordStr);
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
        filePathAndName = null;
        JOptionPane.showMessageDialog(null, "邮件已发送");
        // codes to be written.
        recipientAddrField.setText("");
        mailSubjectField.setText("");
        mailContentArea.setText("");
        attachmentBtn.setText("附件：");
        mailEditFrame.setVisible(false);
    }

    // 构造方法
    public MailEdit(String userNameStr, String userPasswordStr) {
        this.userNameStr = userNameStr;
        this.userPasswordStr = userPasswordStr;
        this.recipientNameStr = "";
        newSendingSession();
        initUI();
    }

    public void setMailEditFrameVisible(boolean visible) {
        mailEditFrame.setVisible(visible);
    }

    public void setRecipientNameStr(String[] recipientNameStrArr){
//        this.recipientNameStr = recipientNameStr;
//        this.recipientAddrField.setText(this.recipientNameStr);
        for(String recipientNameStr : recipientNameStrArr){
            this.recipientNameStr += recipientNameStr + ';';
        }
        this.recipientAddrField.setText(this.recipientNameStr);
    }

    public boolean isMailEditFrameVisible(){
        return mailEditFrame.isVisible();
    }
}
