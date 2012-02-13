/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import oauth.common.ConsumerRegistration;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.rs.security.oauth.data.Client;

@Path("/registerProvider")
public class ThirdPartyRegistrationService {
	private static final String DEFAULT_CLIENT_ID = "123456789";
	private static final String DEFAULT_CLIENT_SECRET = "987654321";
	
	@Context
	private UriInfo uriInfo;
	private OAuthManager manager;
	private Map<String, CachedOutputStream> appLogos = 
	    new HashMap<String, CachedOutputStream>();
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/")
	public ConsumerRegistration register(MultipartBody body) {
	    String appName = body.getAttachmentObject("appName", String.class);
	    String appURI = body.getAttachmentObject("appURI", String.class);
	    String appDesc = body.getAttachmentObject("appDescription", String.class);

	    URI logoURI = null;
	    
	    Attachment att = body.getAttachment("appLogo");
	    if (att != null) {
	        InputStream logoStream = att.getObject(InputStream.class);
	        CachedOutputStream cos = new CachedOutputStream();
	        try {
	            IOUtils.copy(logoStream, cos);
	            appLogos.put(appName.toLowerCase(), cos);
	            
	            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
	            ub.path("logo").path(appName.toLowerCase());
	            
	            ContentDisposition cd = att.getContentDisposition();
	            if (cd != null && cd.getParameter("filename") != null) {
	               ub.path(cd.getParameter("filename"));
	            }
	            logoURI = ub.build();
	            
	        } catch (IOException ex) {
	            // ignore   
	        }
	        
	        
	    }
	    
		String clientId = generateClientId(appName, appURI);
		String clientSecret = generateClientSecret();
	
		Client newClient = new Client(clientId, clientSecret, appName, appURI);
		
		newClient.setApplicationDescription(appDesc);
		newClient.setLogoUri(logoURI.toString());
		
		manager.registerClient(newClient);
		return new ConsumerRegistration(clientId, clientSecret);
	}

	@GET
	@Path("logo/{appName}/{image}")
	@Produces({"image/png", "image/jpeg", "image/gif" })
	public byte[] getApplicationLogo(@PathParam("appName") String appName) {
	    CachedOutputStream cos = appLogos.get(appName);
	    try {
	        return cos == null ? null : cos.getBytes();
	    } catch (IOException ex) {
	        return null;
	    }
	}
	
	public String generateClientId(String appName, String appURI) {
	    // if appURI is not allowed to contain paths, example, it can only be
	    // www.mycompany.com, then appURI can be used as a consumer key
		return DEFAULT_CLIENT_ID;
	}
	
	public String generateClientSecret() {
        return DEFAULT_CLIENT_SECRET;
    }
	
	public void setDataProvider(OAuthManager manager) {
		this.manager = manager;
	}
}
