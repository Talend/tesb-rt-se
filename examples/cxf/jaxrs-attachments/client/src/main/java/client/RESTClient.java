/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.awt.Image;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.util.ByteArrayDataSource;

import common.attachment.Book;
import common.attachment.MultipartsService;
import common.attachment.XopAttachmentService;
import common.attachment.XopBean;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.provider.JSONProvider;

/**
 * Example showing the interaction between HTTP-centric and proxy based RESTful clients and JAX-RS server
 * providing multiple services (PersonService and SearchService)
 */
public final class RESTClient {

    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;

    private static final String HTTP_PORT;
    static {
        Properties props = new Properties();
        try {
            props.load(RESTClient.class.getResourceAsStream("/client.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("client.properties resource is not available");
        }
        HTTP_PORT = props.getProperty(PORT_PROPERTY);
    }

    int port;

    public RESTClient() {
        this(getPort());
    }

    public RESTClient(int port) {
        this.port = port;
    }

    /**
     * Writes and reads the XOP attachment using a CXF JAX-RS WebClient.
     * Note that WebClient is created with the help of JAXRSClientFactoryBean.
     * JAXRSClientFactoryBean can be used when neither of the WebClient factory 
     * methods is appropriate. For example, in this case, an "mtom-enabled" 
     * property is set on the factory bean first.
     * 
     * 
     * @throws Exception
     */
    public void useXopAttachmentServiceWithWebClient() throws Exception {

        final String serviceURI = "http://localhost:" + port + "/services/attachments/xop";
        
        JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
        factoryBean.setAddress(serviceURI);
        factoryBean.setProperties(Collections.singletonMap(org.apache.cxf.message.Message.MTOM_ENABLED, 
                                                    (Object)"true"));
        WebClient client = factoryBean.createWebClient();
        client.type("multipart/related").accept("multipart/related");
        
        XopBean xop = createXopBean();
        
        System.out.println();
        System.out.println("Posting a XOP attachment with a WebClient");
        
        XopBean xopResponse = client.post(xop, XopBean.class);
        
        verifyXopResponse(xop, xopResponse);
    }
    
    /**
     * Writes and reads the XOP attachment using a CXF JAX-RS Proxy
     * The proxy automatically sets the "mtom-enabled" property by checking
     * the CXF EndpointProperty set on the XopAttachment interface.
     * 
     * @throws Exception
     */
    public void useXopAttachmentServiceWithProxy() throws Exception {

        final String serviceURI = "http://localhost:" + port + "/services/attachments";
        
        XopAttachmentService proxy = JAXRSClientFactory.create(serviceURI, 
                                                               XopAttachmentService.class);
        
        XopBean xop = createXopBean();
        
        System.out.println();
        System.out.println("Posting a XOP attachment with a proxy");
                
        XopBean xopResponse = proxy.echoXopAttachment(xop);
        
        verifyXopResponse(xop, xopResponse);
    }
    
    /**
     * Writes and reads the multipart/mixed attachments using a CXF JAX-RS WebClient
     * Note that a custom JAXB-driven JSONProvider is registered to simpify dealing 
     * with one of the parts in the JSON format: it is configured to drop namespace 
     * prefixes on the write and add a namespace to the incoming payload so that it
     * can be read into the namespace-qualified JAXB Book bean.
     *  
     * @throws Exception
     */
    public void useAttachmentServiceWithWebClient() throws Exception {

        final String serviceURI = "http://localhost:" + port + "/services/attachments/multipart";
        
        
        JSONProvider provider = new JSONProvider();
        provider.setIgnoreNamespaces(true);
        provider.setInTransformElements(
            Collections.singletonMap("Book", "{http://books}Book"));
        
        WebClient client = WebClient.create(serviceURI,
                                     Collections.singletonList(provider));
        
        client.type("multipart/mixed").accept("multipart/mixed");
        
        MultipartBody body = createMultipartBody();
        
        System.out.println();
        System.out.println("Posting Book attachments with a WebClient");
        
        MultipartBody bodyResponse = client.post(body, MultipartBody.class);
        
        verifyMultipartResponse(bodyResponse);
    }

    /**
     * Writes and reads the multipart/mixed attachments using a CXF JAX-RS Proxy
     * Note that a custom JAXB-driven JSONProvider is registered to simpify dealing 
     * with one of the parts in the JSON format: it is configured to drop namespace 
     * prefixes on the write and add a namespace to the incoming payload so that it
     * can be read into the namespace-qualified JAXB Book bean.
     * 
     * @throws Exception
     */
    public void useAttachmentServiceWithProxy() throws Exception {

        final String serviceURI = "http://localhost:" + port + "/services/attachments";
        
        
        JSONProvider provider = new JSONProvider();
        provider.setIgnoreNamespaces(true);
        provider.setInTransformElements(
            Collections.singletonMap("Book", "{http://books}Book"));
        
        MultipartsService client = JAXRSClientFactory.create(serviceURI,
                                     MultipartsService.class,                                    
                                     Collections.singletonList(provider));
        
        MultipartBody body = createMultipartBody();
        
        System.out.println();
        System.out.println("Posting Book attachments with a proxy");
        
        MultipartBody bodyResponse = client.echoAttachment(body);
        
        verifyMultipartResponse(bodyResponse);
    }

    /**
     * Creates a XopBean. The image on the disk is included as a byte array, 
     * a DataHandler and java.awt.Image
     * @return the bean
     * @throws Exception
     */
    private XopBean createXopBean() throws Exception  {
        XopBean xop = new XopBean();
        xop.setName("xopName");
        
        InputStream is = getClass().getResourceAsStream("/java.jpg");
        byte[] data = IOUtils.readBytesFromStream(is);
        
        // Pass java.jpg as an array of bytes
        xop.setBytes(data);
        
        // Wrap java.jpg as a DataHandler
        xop.setDatahandler(new DataHandler(
                             new ByteArrayDataSource(data, "application/octet-stream")));
        
        if (Boolean.getBoolean("java.awt.headless")) {
            System.out.println("Running headless. Ignoring an Image property.");
        } else {
            xop.setImage(getImage("/java.jpg"));
        }
        
        return xop;
    }
    
    /**
     * Verifies that the received image is identical to the original one. 
     * @param xopOriginal
     * @param xopResponse
     */
    private void verifyXopResponse(XopBean xopOriginal, XopBean xopResponse) {
        if (!Arrays.equals(xopResponse.getBytes(), xopOriginal.getBytes())) {
            throw new RuntimeException("Received XOP attachment is corrupted");
        }
        System.out.println();
        System.out.println("XOP attachment has been successfully received");
    }
    
    private Image getImage(String name) throws Exception {
        return ImageIO.read(getClass().getResource(name));
    }

    /**
     * Creates MultipartBody. It contains 3 parts, "book1", "book2" and "image". 
     * These individual parts have their Content-Type set to application/xml,
     * application/json and application/octet-stream
     *   
     * MultipartBody will use the Content-Type value of the individual
     * part to write its data by delegating to a matching JAX-RS MessageBodyWriter
     * provider
     * 
     * @return
     * @throws Exception
     */
    private MultipartBody createMultipartBody() throws Exception  {
        List<Attachment> atts = new LinkedList<Attachment>();
        atts.add(new Attachment("book1", "application/xml", new Book("JAXB", 1L)));
        atts.add(new Attachment("book2", "application/json", new Book("JSON", 2L)));
        
        atts.add(new Attachment("image", "application/octet-stream",
                                getClass().getResourceAsStream("/java.jpg")));
        
        return new MultipartBody(atts, true);  

    }
    
    private void verifyMultipartResponse(MultipartBody bodyResponse) throws Exception {
        Book jaxbBook = bodyResponse.getAttachmentObject("book1", Book.class);
        Book jsonBook = bodyResponse.getAttachmentObject("book2", Book.class);
        
        byte[] receivedImageBytes = bodyResponse.getAttachmentObject("image", byte[].class);
        InputStream is = getClass().getResourceAsStream("/java.jpg");
        byte[] imageBytes = IOUtils.readBytesFromStream(is); 
        
        if ("JAXB".equals(jaxbBook.getName()) && 1L == jaxbBook.getId()
            && "JSON".equals(jsonBook.getName()) && 2L == jsonBook.getId()
            && Arrays.equals(imageBytes, receivedImageBytes)) {
            System.out.println();
            System.out.println("Book attachments have been successfully received");
        } else {
            throw new RuntimeException("Received Book attachment is corrupted");
        }
    }
    
    public static void main(String[] args) throws Exception {

        RESTClient client = new RESTClient();

        // Post XOP with CXF JAX-RS WebClient
        client.useXopAttachmentServiceWithWebClient();
        
        // Post XOP with CXF JAX-RS Proxy
        client.useXopAttachmentServiceWithProxy();
        
        // Post Book attachments in XML and JSON formats with CXF JAX-RS WebClient
        client.useAttachmentServiceWithWebClient();
        
        // Post Book attachments in XML and JSON formats with CXF JAX-RS Proxy
        client.useAttachmentServiceWithProxy();
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
