/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.thirdparty;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

@Provider
public class SecurityContextFilter implements ContainerRequestFilter {

	@Context
	private HttpHeaders headers;
	private Map<String, String> users;
	private String realm;
	public void setUsers(Map<String, String> users) {
		this.users = users;
	} 
	
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
	    Message message = JAXRSUtils.getCurrentMessage();
		
		SecurityContext sc = message.get(SecurityContext.class);
		if (sc != null) {
		    Principal principal  = sc.getUserPrincipal();
		    if (principal != null && users.containsKey(principal.getName())) {
			    return;
		    }
		}
		
		List<String> authValues = headers.getRequestHeader("Authorization");
		if (authValues.size() != 1) {
			requestContext.abortWith(createFaultResponse());
			return;
		}
		String[] values = authValues.get(0).split(" ");
		if (values.length != 2 || !"Basic".equals(values[0])) {
			requestContext.abortWith(createFaultResponse());
			return;
		}
		
		String decodedValue = null;
		try {
			decodedValue = new String(Base64Utility.decode(values[1]));
		} catch (Base64Exception ex) {
			requestContext.abortWith(createFaultResponse());
			return;
		}
		final String[] namePassword = decodedValue.split(":");
		if (namePassword.length != 2) {
			requestContext.abortWith(createFaultResponse());
			return;
		}
		String password = users.get(namePassword[0]); 
		if (password == null || !password.equals(namePassword[1])) {
			requestContext.abortWith(createFaultResponse());
			return;
		}
		final SecurityContext newSc = new SecurityContext() {

			public Principal getUserPrincipal() {
				return new SimplePrincipal(namePassword[0]);
			}

			public boolean isUserInRole(String arg0) {
				return false;
			}
			
		};
		message.put(SecurityContext.class, newSc);
	}

	private Response createFaultResponse() {
		return Response.status(401).header("WWW-Authenticate", 
				"Basic realm=\"" + getRealm() + "\"").build();
	}


	public String getRealm() {
		return realm;
	}


	public void setRealm(String realm) {
		this.realm = realm;
	}
}
