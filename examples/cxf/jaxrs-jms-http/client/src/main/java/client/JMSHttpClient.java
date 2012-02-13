/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.jms.JMSUtils;

import common.books.Book;

/**
 *  Example showing how CXF JAX-RS WebClient and JMS consumers
 *  make remote calls to HTTP and JMS services backed up by the 
 *  shared service implementation instance.   
 *
 */
public final class JMSHttpClient {
	
    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;
	
    private static final String HTTP_PORT;
    static {
		Properties props = new Properties();
		try {
		    props.load(JMSHttpClient.class.getResourceAsStream("/client.properties"));
		} catch (Exception ex) {
		    throw new RuntimeException("client.properties resource is not available");
		}
	    HTTP_PORT = props.getProperty(PORT_PROPERTY);
    } 
	
    int port;
	
    public JMSHttpClient() {
	    this(getPort());	
    }
	
    public JMSHttpClient(int port) {
	    this.port = port;	
    }

    public void useWebClient() {
    	System.out.println("Using WebClient to get the book with id 123");
    	WebClient client = WebClient.create("http://localhost:" + port + "/services/bookstore/");
    	Book book = client.post(new Book("HTTP"), Book.class);
    	System.out.println(book.getId() + ":" + book.getName());
    }
    
    public void useJMSClient() throws Exception {
    	
    	System.out.println("Getting the book with id 123 over JMS");
    	
    	getBookOverJMS();
    	
    	System.out.println("Adding a new book over JMS");
    	addGetBookOverJMS(new Book("JMS Transport"));

    	System.out.println("Adding a new book over JMS Oneway");
    	addGetOnewayBookOverJMS();
    	
    	System.out.println("Adding a new book over HTTP Oneway, " 
    			+ " and getting it back over JMS");
    	addOnewayOverHttpGetOverJMS();
    }
    
    
    private void getBookOverJMS() throws Exception {
        Context ctx = getContext();
        Destination destination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.text");
        Destination replyToDestination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.response");
                
        Connection connection = createAndStartConnection(ctx);
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            postGetMessage(session, destination, replyToDestination);
            checkBookInResponse(session, replyToDestination);
        } finally {
        	closeSessionAndConnection(session, connection);
        }
        
    }
    
    private Connection createAndStartConnection(Context ctx) throws Exception {
    	ConnectionFactory factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
    	Connection connection = factory.createConnection();
        connection.start();
        return connection;
    }
    
    private void closeSessionAndConnection(Session session, Connection connection) {
    	try {
    		session.close();
            connection.stop();
            connection.close();
        } catch (JMSException ex) {
            // ignore
        }	
    }
    
    private void addGetBookOverJMS(Book book) throws Exception {
        Context ctx = getContext();
        Destination destination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.text");
        Destination replyToDestination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.response");
                
        Connection connection = createAndStartConnection(ctx);
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            postBook(book, session, destination, replyToDestination);
            checkBookInResponse(session, replyToDestination);
        } finally {
        	closeSessionAndConnection(session, connection);
        }
        
    }
    
    
    private void addGetOnewayBookOverJMS() throws Exception {
        Context ctx = getContext();
                Destination destination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.text");
        Destination replyToDestination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.response");
                
        Connection connection = createAndStartConnection(ctx);
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            postOneWayBook(session, destination);
            checkBookInResponse(session, replyToDestination);
        } finally {
        	closeSessionAndConnection(session, connection);
        }
        
    }
    
    private void addOnewayOverHttpGetOverJMS() throws Exception {
        
    	WebClient client = WebClient.create("http://localhost:" + port + "/services/bookstore");
    	Response response = client.put(new Book("FROM HTTP TO JMS", 126L));
    	if (response.getStatus() != 202) {
    		new RuntimeException("This is a oneway request");
    	}
    	
    	Context ctx = getContext();
                Destination replyToDestination = (Destination)ctx.lookup("dynamicQueues/test.jmstransport.response");
                
        Connection connection = createAndStartConnection(ctx);
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            checkBookInResponse(session, replyToDestination);
        } finally {
        	closeSessionAndConnection(session, connection);
        }
        
    }
    
    private void postGetMessage(Session session, Destination destination, Destination replyTo) 
	    throws Exception {
	    MessageProducer producer = session.createProducer(destination);
	    Message message = session.createMessage();
	    message.setJMSReplyTo(replyTo);
	    message.setStringProperty("org.apache.cxf.request.uri", "123");
	    message.setStringProperty("org.apache.cxf.request.method", "GET");
	    producer.send(message);
	    producer.close();
	}
    
    private void postOneWayBook(Session session, Destination destination) 
	    throws Exception {
	    MessageProducer producer = session.createProducer(destination);
	    
	    Message message = JMSUtils.createAndSetPayload(
	        writeBook(new Book("JMS OneWay", 125L)), session, "text");
	    message.setStringProperty("org.apache.cxf.request.method", "PUT");
	                
	    producer.send(message);
	    producer.close();
	}
	
	private void postBook(Book book, Session session, Destination destination, Destination replyTo) 
	    throws Exception {
	    MessageProducer producer = session.createProducer(destination);
	    
	    Message message = JMSUtils.createAndSetPayload(writeBook(book), session, "text");
	    message.setJMSReplyTo(replyTo);
	    
	    message.setStringProperty("org.apache.cxf.request.method", "POST");
	                
	    producer.send(message);
	    producer.close();
	}
    
    
    private void checkBookInResponse(Session session, Destination replyToDestination) throws Exception {
        MessageConsumer consumer = session.createConsumer(replyToDestination);
        Message jmsMessage = consumer.receive(5000);
        org.apache.cxf.message.Message cxfMessage = new org.apache.cxf.message.MessageImpl();
        JMSUtils.retrieveAndSetPayload(cxfMessage, jmsMessage, null);
        Book b = readBook(cxfMessage.getContent(InputStream.class));
        System.out.println(b.getId() + ":" + b.getName());
    }
    
    private Context getContext() throws Exception {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                          "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:" + 61616);
        return new InitialContext(props);
        
    }
    
    
    private Book readBook(InputStream is) throws Exception {
        JAXBContext c = JAXBContext.newInstance(new Class[]{Book.class});
        Unmarshaller u = c.createUnmarshaller();
        return (Book)u.unmarshal(is);
    }
    
    private String writeBook(Book b) throws Exception {
        JAXBContext c = JAXBContext.newInstance(new Class[]{Book.class});
        Marshaller m = c.createMarshaller();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        m.marshal(b, bos);
        return bos.toString();
    }

    public static void main (String[] args) throws Exception {
        
    	JMSHttpClient client = new JMSHttpClient();
    	client.useWebClient();
    	client.useJMSClient();
    }
    
    private static int getPort() { 
    	try {
    	    return Integer.valueOf(HTTP_PORT);
    	} catch (NumberFormatException ex) {
    	    // ignore
    	}
    	return DEFAULT_PORT_VALUE;
    }
}

