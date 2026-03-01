package mktvsmart.screen.mail;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/* loaded from: classes.dex */
public class Mail {
    private boolean debuggable;
    private String emailBody;
    private String emailSubject;
    private String fromAddress;
    private String hostAddress;
    private String hostPort;
    private boolean isSupportSSL;
    private boolean isSupportTLS;
    private boolean isValidated;
    private Multipart multipart;
    private String password;
    private String socketFactoryPort;
    private String[] toAddress;
    private String username;

    public Mail() {
        this.hostAddress = "smtp.gmail.com";
        this.hostPort = "465";
        this.socketFactoryPort = "465";
        this.username = "";
        this.password = "";
        this.fromAddress = "";
        this.emailSubject = "";
        this.emailBody = "";
        this.debuggable = false;
        this.isValidated = true;
        this.multipart = new MimeMultipart("mixed");
        this.isSupportSSL = true;
        this.isSupportTLS = false;
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Mail(String userAddress, String password) {
        this();
        String[] temp = userAddress.split("@");
        String domainName = temp[1];
        if (domainName.equals("hotmail.com") || domainName.equals("outlook.com")) {
            this.isSupportSSL = false;
            this.isSupportTLS = true;
            this.hostAddress = "smtp.live.com";
            this.hostPort = "587";
            this.socketFactoryPort = "587";
        } else if (domainName.equals("yahoo.com")) {
            this.isSupportSSL = true;
            this.hostAddress = "smtp.mail.yahoo.com";
            this.hostPort = "465";
            this.socketFactoryPort = "465";
        } else if (!domainName.equals("gmail.com")) {
            if (domainName.equals("gotechcn.com")) {
                this.isSupportSSL = false;
                this.hostAddress = "mail." + domainName;
                this.hostPort = "25";
                this.socketFactoryPort = "25";
            } else {
                this.isSupportSSL = false;
                this.hostAddress = "smtp." + domainName;
                this.hostPort = "25";
                this.socketFactoryPort = "25";
            }
        }
        this.username = userAddress;
        this.password = password;
    }

    public synchronized boolean send() throws Exception {
        boolean z;
        Properties props = setProperties();
        UserAuthenticator userAuth = null;
        if (!this.username.equals("") && !this.password.equals("") && this.toAddress.length > 0 && !this.fromAddress.equals("") && !this.emailSubject.equals("")) {
            if (this.isValidated) {
                userAuth = new UserAuthenticator(this.username, this.password);
            }
            Session session = Session.getInstance(props, userAuth);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(this.fromAddress));
            InternetAddress[] addressTo = new InternetAddress[this.toAddress.length];
            for (int i = 0; i < this.toAddress.length; i++) {
                addressTo[i] = new InternetAddress(this.toAddress[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo);
            msg.setSubject(this.emailSubject);
            msg.setSentDate(new Date());
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(this.emailBody);
            this.multipart.addBodyPart(messageBodyPart);
            msg.setContent(this.multipart);
            Transport.send(msg);
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public void addAttachment(String path, String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        File file = new File(path, filename);
        FileDataSource source = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        this.multipart.addBodyPart(messageBodyPart);
    }

    public void removeAttachment(String filename) throws Exception {
        BodyPart messagebBodyPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(filename);
        messagebBodyPart.setDataHandler(new DataHandler(source));
        messagebBodyPart.setFileName(filename);
        this.multipart.removeBodyPart(messagebBodyPart);
    }

    public void removeAll() throws Exception {
        int count = this.multipart.getCount();
        for (int loop = 0; loop < count; loop++) {
            this.multipart.removeBodyPart(0);
        }
    }

    private Properties setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", this.hostAddress);
        props.put("mail.debug", this.debuggable ? "true" : "false");
        props.put("mail.smtp.auth", this.isValidated ? "true" : "false");
        props.put("mail.smtp.port", this.hostPort);
        props.put("mail.smtp.starttls.enable", this.isSupportTLS ? "true" : "false");
        if (this.isSupportSSL) {
            props.put("mail.smtp.socketFactory.port", this.socketFactoryPort);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }
        return props;
    }

    public String getBody() {
        return this.emailBody;
    }

    public void setBody(String body) {
        this.emailBody = body;
    }

    public void setTo(String[] toArr) {
        this.toAddress = toArr;
    }

    public void setFrom(String string) {
        this.fromAddress = string;
    }

    public void setSubject(String string) {
        this.emailSubject = string;
    }
}
