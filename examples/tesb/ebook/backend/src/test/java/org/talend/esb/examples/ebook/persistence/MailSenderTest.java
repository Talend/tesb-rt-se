package org.talend.esb.examples.ebook.persistence;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.examples.ebook.persistence.MailSender;

public class MailSenderTest {
    @Test
    @Ignore
    public void testSendEbook() throws URISyntaxException {
        URI uri = new URI("http://www.gutenberg.org/ebooks/50180.kindle.noimages");
        MailSender sender = new MailSender();
        sender.send("root@localhost", uri);
    }
}
