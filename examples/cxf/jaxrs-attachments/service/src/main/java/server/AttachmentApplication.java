/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.provider.JSONProvider;

import service.attachment.MultipartsServiceImpl;
import service.attachment.XopAttachmentServiceImpl;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/attachments")
public class AttachmentApplication extends Application {
    
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<Object>();
        singletons.add(new XopAttachmentServiceImpl());
        singletons.add(new MultipartsServiceImpl());
        
        JSONProvider provider = new JSONProvider();
        // equivalent to provider.setIgnoreNamespaces(true);
        provider.setOutTransformElements(
                Collections.singletonMap("{http://books}Book", "Book"));
        
        provider.setInTransformElements(
            Collections.singletonMap("Book", "{http://books}Book"));
        
        singletons.add(provider);
        
        return singletons;
    }
}
