package org.talend.esb.examples.ebook.itest;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.BookRepository;

@Ignore
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ImportRouteTest extends AbstractJPAItest {
    @Inject
    ConnectionFactory cf;
    
    @Inject
    BookRepository bookRepo;
    
    @Test
    public void testImport() throws Exception {
        final Book book = createBook("My title");
        sendMessage("books.in", book);
        Book book2 = tryTo("get book", new Callable<Book>() {
            public Book call() throws Exception {
                return bookRepo.getBook(book.getId());
            }
        });
        Assert.assertEquals(book.getId(), book2.getId());
        Assert.assertEquals(book.getTitle(), book2.getTitle());
        bookRepo.delete(book.getId());
    }
    
    @Test(expected=TimeoutException.class)
    public void testImportError() throws Exception {
        final Book book = createBook("error1");
        sendMessage("books.in", book);
        tryTo("get book", new Callable<Book>() {
            public Book call() throws Exception {
                return bookRepo.getBook(book.getId());
            }
        }, 10000);
    }

    private String marshal(Object body) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(body.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        jaxbMarshaller.marshal(body, writer);
        return writer.toString();
    }

    private void sendMessage(String queue, Object body) throws JMSException, TimeoutException, JAXBException {
        Connection con = tryTo("connect to jms broker", new Callable<Connection>() {
            public Connection call() throws Exception {
                return cf.createConnection();
            }
        }, 20000);
        Session sess = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = sess.createQueue(queue);
        MessageProducer prod = sess.createProducer(destination);
        TextMessage message = sess.createTextMessage();
        message.setText(marshal(body));
        prod.send(message);
        prod.close();
        sess.close();
        con.close();
    }

    @Configuration
    public Option[] getConfiguration() {
        return new Option[] {
            baseOptions(),
            features(ebooksFeatures, "example-ebook-backend", "example-ebook-importer")
        };
    }
}
