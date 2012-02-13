/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.attachment;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

/**
 * This interface describes a JAX-RS root resource capable of echoing 
 * the multipart/mixed attachments
 */

@Path("/multipart")
public interface MultipartsService {

    /**
     * Echoes back the multipart body representing the payload 
     * consisting of one or more parts.  
     */
    @POST
    @Consumes("multipart/mixed")
    @Produces("multipart/mixed")
    public MultipartBody echoAttachment(MultipartBody body);

}
