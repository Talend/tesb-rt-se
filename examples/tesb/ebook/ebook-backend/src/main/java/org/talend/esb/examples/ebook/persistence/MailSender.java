package org.talend.esb.examples.ebook.persistence;

import java.net.URI;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailSender {
    Logger LOG = LoggerFactory.getLogger(MailSender.class);

    public void send(String recipient, URI bookURI) {
        try {
            Properties props = new Properties();
            /*
             * props.put("mail.smtp.auth", "true");
             * props.put("mail.smtp.starttls.enable", "true");
             * props.put("mail.smtp.host", host); 
             * props.put("mail.smtp.port", "25");
             */
            Session session = Session.getInstance(props);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ebook@talend.org"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("EBook " + bookURI);
            message.setContent(createMultipart("Ebook is attached", bookURI.toURL()));
            Transport.send(message);
            LOG.info("Mail sent to {}.", recipient);
        } catch (Exception e) {
            throw new RuntimeException("Error sending mail with eBook", e);
        }
    }

    private Authenticator createAuthenticator(final String username, final String password) {
        return new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

    private Multipart createMultipart(String message, URL attachment) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(message);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();
        DataSource source = new URLDataSource(attachment);
        messageBodyPart.setDataHandler(new DataHandler(source));
        multipart.addBodyPart(messageBodyPart);
        return multipart;

    }
}
