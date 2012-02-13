/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service.attachment;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;

import common.attachment.Book;
import common.attachment.MultipartsService;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

/**
 * JAX-RS MultipartsService root resource
 */
public class MultipartsServiceImpl implements MultipartsService {

    /**
     * {@inheritDoc}
     */
    public MultipartBody echoAttachment(MultipartBody body) {
        return duplicateMultipartBody(body);
    }

    /**
     * Verifies the MultipartBody by reading the individual parts
     * and copying them to a new MultipartBody instance which 
     * will be written out in the multipart/mixed format.
     * 
     * @param body the incoming MultipartBody
     * @return new MultipartBody
     */
    private MultipartBody duplicateMultipartBody(MultipartBody body) {
        
        // It is possible to access individual parts by the Content-Id values
        // This MultipartBody is expected to contain 3 parts, 
        // "book1", "book2" and "image". 
        
        // These individual parts have their Content-Type set to 
        // application/xml, application/json and application/octet-stream
        
        // MultipartBody will use the Content-Type value of the individual
        // part to read its data in a type safe way by delegating to a matching 
        // JAX-RS MessageBodyReader provider
        
        Book jaxbBook = body.getAttachmentObject("book1", Book.class);
        Book jsonBook = body.getAttachmentObject("book2", Book.class);
        
        // Accessing individual attachment part, its DataHandler will be 
        // used to access the underlying input stream, the type-safe access
        // is also possible
        
        Attachment imageAtt = body.getAttachment("image");
        
        if ("JAXB".equals(jaxbBook.getName()) && 1L == jaxbBook.getId()
            && "JSON".equals(jsonBook.getName()) && 2L == jsonBook.getId()
            && imageAtt != null) {
            return createMultipartBody(jaxbBook, jsonBook, imageAtt.getDataHandler());
        }
        throw new RuntimeException("Received Book attachment is corrupted");
    }
    
    private MultipartBody createMultipartBody(Book jaxbBook, Book jsonBook, DataHandler imageHandler) {
        List<Attachment> atts = new LinkedList<Attachment>();
        
        // One of the ways to create an individual part is to use 
        // a Content-Id, Content-Type and the object triple.
        // Multipart Provider will delegate to a matching JAX-RS
        // MessageBodyWriter
        
        atts.add(new Attachment("book1", "application/xml", jaxbBook));
        atts.add(new Attachment("book2", "application/json", jsonBook));
        try {
            atts.add(new Attachment("image", "application/octet-stream", imageHandler.getInputStream()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return new MultipartBody(atts, true);  

    }
}
