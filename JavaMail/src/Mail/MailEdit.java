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
        recipientAddrLabel = new JLabel("�ռ��ˣ�");
        mailSubjectField = new JTextField();
        mailSubjectLabel = new JLabel("���⣺");
        mailContentArea = new JTextArea();
        mailContentLabel = new JLabel("���ģ�");
        mailContentScrollPane = new JScrollPane(mailContentArea);
        sendingBtn = new JButton("����");
        attachmentBtn = new JButton("ѡ�񸽼�");
        attachmentLabel = new JLabel("������");

        mailEditFrame.setBounds(0, 0, 500, 400);
        mailEditFrame.setLayout(null);
        mailEditFrame.setResizable(false);
        mailEditFrame.setTitle("�����ʼ�");

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
                JFileChooser fileChooser = new JFileChooser(); // �����ļ��Ի���
                int returnValue = fileChooser.showOpenDialog(null);// ���ļ�ѡ��Ի���
                if (returnValue == JFileChooser.APPROVE_OPTION) { // �ж��Ƿ�ѡ�����ļ�
                    File file = fileChooser.getSelectedFile(); // ����ļ�����
                    if (file.length() / 1024.0 / 1024 > 50.0) {
                        JOptionPane.showMessageDialog(null, "��ѡ��С�ڵ���50MB���ļ���");
                        return;
                    }
                    filePathAndName = file.getAbsolutePath();// ����ļ�������·�����ļ���
                    attachmentLabel.setText("������" + file.getName());// ��ʾ�����ļ�������
                }
            }
        });
        sendingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recipientNameStr = recipientAddrField.getText().trim();
                if(recipientNameStr == null || recipientNameStr.equals("")) {
                    JOptionPane.showMessageDialog(null, "�ռ��˲���Ϊ��");
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
            mailSendingSession.setDebug(true); // ����̨���������Ϣ
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
        }
    }

    private void sendMimeMessage() throws Exception {
        // ����MimeMessage
        MimeMessage msg = new MimeMessage(mailSendingSession);
        // ��д������Ϣ
//        InternetAddress[] toAddrs = InternetAddress.parse(recipientNameStr, false);
        String[] toAddrs = recipientNameStr.split(";");
//        msg.setRecipients(Message.RecipientType.TO, toAddrs);
        for(int i = 0; i < toAddrs.length; i++){
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddrs[i]));
        }
        msg.setFrom(new InternetAddress(userNameStr));
        msg.setSubject(mailSubjectField.getText().trim());
        msg.setSentDate(new Date());
        // ����multipart
        Multipart multipart = new MimeMultipart();
        MimeBodyPart mimeBodyPartText = new MimeBodyPart();
        mimeBodyPartText.setText(mailContentArea.getText());
        multipart.addBodyPart(mimeBodyPartText);
        // ��Ӹ���
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
        // ����Transport
        Transport transport = mailSendingSession.getTransport();
        transport.connect(sendingHost, userNameStr, userPasswordStr);
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
        filePathAndName = null;
        JOptionPane.showMessageDialog(null, "�ʼ��ѷ���");
        // codes to be written.
        recipientAddrField.setText("");
        mailSubjectField.setText("");
        mailContentArea.setText("");
        attachmentBtn.setText("������");
        mailEditFrame.setVisible(false);
    }

    // ���췽��
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
