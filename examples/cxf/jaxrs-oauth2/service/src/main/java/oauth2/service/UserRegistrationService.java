/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import oauth2.common.UserRegistration;

@Path("registerUser")
public class UserRegistrationService {

	private UserAccounts accounts;
	
	public UserRegistrationService() {
	}
	
	public void setAccounts(UserAccounts accounts) {
		this.accounts = accounts;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/")
	public UserRegistration register(@FormParam("user") String name,
			                         @FormParam("alias") String alias,
			                         @FormParam("password") String password) {
		if (accounts.getAccount(name) != null) {
			throw new WebApplicationException(400);
		}
		accounts.setAccount(name, new UserAccount(name, password, alias));
		return new UserRegistration(name);
	}
}
