package Mail;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.mail.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class MainFrame {
    private JFrame mainFrame;
    private JTextArea mailContent;          // 邮件正文文本框
    private JList mailList;                 // 邮件列表（inbox）
    private DefaultListModel mailListModel;
    private JScrollPane mailListScrollPane;
    private JScrollPane mailContentScrollPane;
    private JButton deleteSelectedMailBtn;     // 删除键
    private JButton newMailBtn;                // 新建邮件键
    private JButton attachmentBtn;             // 得到附件键
    private JButton replyBtn;                  // 回复键
    private JButton refreshMailListBtn;        // 刷新邮件列表键
    private String userNameStr;
    private String userPasswordStr;
    private Session mailReceivingSession;
    private Store receivingStore;
    private Folder receivingFolder;
    private Message[] receivingMessage;
    private final String receivingHost = "pop.qq.com";
    private final String receivingProtocol = "pop3";
    private Properties receivingProperty;
    private boolean refreshLock = false;
    private String from;

    public MainFrame(String userNameStr, String userPasswordStr){
        try {
            this.userNameStr = userNameStr;
            this.userPasswordStr = userPasswordStr;
            mainFrame = new JFrame(userNameStr + " at-> " + receivingHost);
            mailContent = new JTextArea();
            mailContentScrollPane = new JScrollPane(mailContent);
            mailListModel = new DefaultListModel();
            mailList = new JList();
            mailList.setFont(new Font("黑体",Font.BOLD,20));
            mailListScrollPane = new JScrollPane(mailList);
            newMailBtn = new JButton("新邮件");
            newMailBtn.setFont(new Font("黑体",Font.BOLD,20));
            deleteSelectedMailBtn = new JButton("删除");
            deleteSelectedMailBtn.setFont(new Font("黑体",Font.BOLD,20));
            attachmentBtn = new JButton("下载附件");
            attachmentBtn.setFont(new Font("黑体",Font.BOLD,20));
            replyBtn = new JButton("回复");
            replyBtn.setFont(new Font("黑体",Font.BOLD,20));
            refreshMailListBtn = new JButton("刷新");
            refreshMailListBtn.setFont(new Font("黑体",Font.BOLD,20));
            // 初始化发件窗口
            MailEdit mailEdit = new MailEdit(userNameStr, userPasswordStr);

            mainFrame.setVisible(true);
            mainFrame.setBounds(0, 0, 1200, 900);
            mainFrame.setResizable(false);
            mainFrame.setLayout(null);

            mainFrame.add(mailListScrollPane);
            mailList.setModel(mailListModel);
            mailListScrollPane.setBounds(5,70,390,775);

            mainFrame.add(mailContentScrollPane);
            mailContent.setEditable(false);
            mailContent.setLineWrap(true);
            mailContent.setWrapStyleWord(true);
            mailContentScrollPane.setBounds(400, 70, 790, 775);

            mainFrame.add(deleteSelectedMailBtn);
            deleteSelectedMailBtn.setBounds(5, 5, 80, 60);

            mainFrame.add(refreshMailListBtn);
            refreshMailListBtn.setBounds(95, 5, 80, 60);

            mainFrame.add(newMailBtn);
            newMailBtn.setBounds(185, 5, 120, 60);

            mainFrame.add(replyBtn);
            replyBtn.setBounds(1110, 5, 80, 60);
            replyBtn.setEnabled(false);
            mainFrame.add(attachmentBtn);
            attachmentBtn.setBounds(495, 5, 160, 60);

            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    try {
                        if (receivingFolder != null && receivingFolder.isOpen())
                            receivingFolder.close(true);
                        if (receivingStore != null && receivingStore.isConnected())
                            receivingStore.close();
                    }catch (MessagingException me){
                        me.printStackTrace();
                    }finally {
                        System.exit(0);
                    }
                }
            });
            refreshMailListBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getInbox();
                }
            });
            mailList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    replyBtn.setEnabled(true);
                    displaySelectedMail();
                }
            });
            deleteSelectedMailBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        deleteMail(mailList.getSelectedValuesList());
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                }
            });
            newMailBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mailEdit.setMailEditFrameVisible(true);
                }
            });
            replyBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!mailEdit.isMailEditFrameVisible()) {
                        mailEdit.setRecipientNameStr(getFromArr());
                        mailEdit.setMailEditFrameVisible(true);
                    }
                }
            });

            receivingProperty = new Properties();
            receivingProperty.setProperty("mail.store.protocol", receivingProtocol);
            receivingProperty.setProperty("mail.pop3.host", receivingHost);
            receivingProperty.setProperty("mail.pop3.auth", "true");
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            receivingProperty.put("mail.pop3.ssl.enable", "true");
            receivingProperty.put("mail.pop3.ssl.socketFactory", sf);
            mailReceivingSession = Session.getInstance(receivingProperty);
            receivingStore = mailReceivingSession.getStore(receivingProtocol);
            receivingStore.connect(receivingHost, userNameStr, userPasswordStr);
            receivingFolder = receivingStore.getFolder("inbox");
            getInbox();
        }catch (NoSuchProviderException noSuchProviderException){
            noSuchProviderException.printStackTrace();
        }catch (MessagingException messagingException){
            JOptionPane.showMessageDialog(mainFrame, "登录失败");
            messagingException.printStackTrace();
        }catch (GeneralSecurityException gse){
            gse.printStackTrace();
        }
    }

    private void getInbox(){
        Runnable executeGetInbox = new Runnable() {
            @Override
            public void run() {
                if(!refreshLock) {
                    System.out.println("开始收取邮件");
                    refreshLock = true;
                    try {
                        mailListModel.clear();
                        if (!receivingFolder.isOpen())
                            receivingFolder.open(Folder.READ_WRITE);
                        else{
                            receivingFolder.close(true);
                            receivingFolder.open(Folder.READ_WRITE);
                        }
                        receivingMessage = receivingFolder.getMessages();
                        for (Message messageIter : receivingMessage) {
                            String subject = (messageIter.getSubject().equals("") ? "无标题" : messageIter.getSubject());
                            mailListModel.addElement(subject);
                            System.out.println("已读取：" + subject);
//                Address[] from = messageIter.getFrom();
//                String content = (String)messageIter.getContent();
                        }
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Finish reading inbox");
                        refreshLock = false;
                    }
                }
            }
        };
        Thread executeGetInboxThread = new Thread(executeGetInbox);
        executeGetInboxThread.start();
    }
    public void displaySelectedMail(){
        try {
            int selectedIndex = mailList.getSelectedIndex();
            System.out.println("selected index: " + selectedIndex);
            if(selectedIndex != -1) {
                Message selectedMessage = receivingMessage[selectedIndex];
                mailContent.setText("");
                mailContent.append("标题：" + selectedMessage.getSubject() + '\n');
                mailContent.setFont(new Font("黑体",Font.BOLD,20));
                String fromStr = "来自：";
                from = "";
                for (Address sender : selectedMessage.getFrom())
                    from += sender.toString() + ";";
                fromStr += from;
                mailContent.append(fromStr + '\n');
                mailContent.append("时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        selectedMessage.getSentDate()
                )+ '\n');
                mailContent.append("===========================================================\n");
                mailContent.append(getMailContent((Part)selectedMessage));
//                selectedMessage.writeTo(System.out);
                mailContent.setCaretPosition(0);
                mailContent.setFont(new Font("黑体",Font.PLAIN,20));
            }
        }catch (MessagingException me){
            me.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (java.lang.Exception jle){
            jle.printStackTrace();
        }
    }
    public String getMailContent(Part part) throws Exception{
        StringBuffer bodyText = new StringBuffer();
        System.out.println(part.getContentType());
        if(part.isMimeType("text/plain")){
            bodyText.append((String)part.getContent());
        }
        else if(part.isMimeType("text/html")){
            bodyText.append((String)part.getContent());
        }
        else if(part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart) part.getContent();
            int cnt = multipart.getCount();
            for(int i = 0; i < cnt; i++){
                bodyText.append(getMailContent(multipart.getBodyPart(i)));
            }
        }
        else if(part.isMimeType("message/rfc822")){
            bodyText.append(((Part)part.getContent()));
        }
        return bodyText.toString();
    }
    private void deleteMail(List selectedMailList) throws Exception{
        Vector<Integer> vector = new Vector<Integer>();
        for(int i = 0; i < selectedMailList.size(); i++){
            vector.add(mailListModel.indexOf(selectedMailList.get(i)));
        }
        vector.sort(null);
        for(int i = vector.size()-1; i >= 0; i--){
            int a = vector.elementAt(i).intValue();
            receivingMessage[a].setFlag(Flags.Flag.DELETED, true);
            mailListModel.remove(a);
        }
    }
    private String[] getFromArr(){
        String[] fromArr = from.split(";");
        for(int i = 0; i < fromArr.length; i++){
            int firstAngle = fromArr[i].indexOf('<'), secondAngle = fromArr[i].indexOf('>');
            secondAngle = secondAngle == -1 ? fromArr[i].length() : secondAngle;
            fromArr[i] = fromArr[i].substring(firstAngle + 1, secondAngle);
            System.out.println(fromArr[i]);
        }
        return fromArr;
    }
}
