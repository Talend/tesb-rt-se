/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import oauth2.common.Calendar;

@Path("accounts")
public class SocialService {

	@Context
	private SecurityContext context;
	private UserAccounts accounts;
	
	public SocialService() {
	}
	
	public void setAccounts(UserAccounts accounts) {
		this.accounts = accounts;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("calendar")
	public void updateUserCalendar(@FormParam("hour") int hour, 
			                       @FormParam("event") String eventDescription) {
		UserAccount account = getAccount();
		account.getCalendar().getEntry(hour).setEventDescription(eventDescription);
	}

	@GET
	@Path("calendar")
	public Calendar getUserCalendar() {
		UserAccount account = getAccount();
		return account.getCalendar();
	}
	
	
	@GET
	public UserAccount getAccount() {
		String userName = context.getUserPrincipal().getName();
		return accounts.getAccount(userName);
	}
}
