package org.talend.esb.examples.ebook.persistence;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class MailSenderTest {
    @Test
    public void testSendEbook() throws URISyntaxException {
        URI uri = this.getClass().getClassLoader().getResource("pg50180.mobi").toURI();
        MailSender sender = new MailSender();
        sender.send("root@localhost", uri);
    }
}
