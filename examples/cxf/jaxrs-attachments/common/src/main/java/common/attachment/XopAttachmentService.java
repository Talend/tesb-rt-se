/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.attachment;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.annotations.EndpointProperty;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

/**
 * This interface describes a JAX-RS root resource capable of echoing 
 * the XOP attachments
 */


/**
 * The "mtom-enabled" property enables reading and writing XOP attachments.
 * It can also be set programmatically or from the external configuration
 */
@EndpointProperty(key = org.apache.cxf.message.Message.MTOM_ENABLED, value = "true")
@Path("/xop")
public interface XopAttachmentService {

    /**
     * Echoes the XOPBean. 
     * Note that the type parameter set in the Produces media type indicates to
     * the JAX-RS Multipart Provider that a provider capable of dealing with@Multipart
     */
    @POST
    @Consumes("multipart/related")
    @Produces("multipart/related;type=text/xml")
    @Multipart("xop")
    public XopBean echoXopAttachment(@Multipart XopBean xop);

}
