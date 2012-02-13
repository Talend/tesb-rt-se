/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.thirdparty;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

@Provider
public class SecurityContextFilter implements RequestHandler {

	@Context
	private HttpHeaders headers;
	private Map<String, String> users;
	
	public void setUsers(Map<String, String> users) {
		this.users = users;
	} 
	
	
	public Response handleRequest(Message message, ClassResourceInfo cri) {
		List<String> authValues = headers.getRequestHeader("Authorization");
		if (authValues.size() != 1) {
			return createFaultResponse();
		}
		String[] values = authValues.get(0).split(" ");
		if (values.length != 2 || !"Basic".equals(values[0])) {
			return createFaultResponse();
		}
		
		String decodedValue = null;
		try {
			decodedValue = new String(Base64Utility.decode(values[1]));
		} catch (Base64Exception ex) {
			return createFaultResponse();
		}
		final String[] namePassword = decodedValue.split(":");
		if (namePassword.length != 2) {
			return createFaultResponse();
		}
		String password = users.get(namePassword[0]); 
		if (password == null || !password.equals(namePassword[1])) {
			return createFaultResponse();
		}
		final SecurityContext sc = new SecurityContext() {

			public Principal getUserPrincipal() {
				return new SimplePrincipal(namePassword[0]);
			}

			public boolean isUserInRole(String arg0) {
				return false;
			}
			
		};
		message.put(SecurityContext.class, sc);
		return null;
	}

	private Response createFaultResponse() {
		return Response.status(401).header("WWW-Authenticate", "Basic realm=\"Reservations\"").build();
	}
}
