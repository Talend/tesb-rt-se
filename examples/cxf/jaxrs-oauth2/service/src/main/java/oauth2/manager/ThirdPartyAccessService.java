/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.manager;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import oauth2.common.Calendar;
import oauth2.common.OAuthConstants;
import oauth2.service.UserAccount;
import oauth2.service.UserAccounts;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.OAuthContext;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;

@Path("/calendar")
public class ThirdPartyAccessService {

    @Context 
    private MessageContext mc;
    private UserAccounts accounts;
	
	public void setAccounts(UserAccounts accounts) {
		this.accounts = accounts;
	}
	
	@GET
	public Calendar getUserCalendar() {
	    OAuthContext oauth = getOAuthContext();
	    String userName = oauth.getSubject().getLogin();
	    UserAccount account = accounts.getAccount(userName);
	    if (account == null) {
	    	account = accounts.getAccountWithAlias(userName);
	    }
		return account.getCalendar();
	}
	
	@POST
	public void updateCalendar(@FormParam("hour") int hour, 
	                           @FormParam("description") String description) {
	    assertClientMayUpdate(hour);
	    Calendar calendar = getUserCalendar();
	    calendar.getEntry(hour).setEventDescription(description);
	}

    /**
     * This permission check can be done in a custom filter; it can be simpler to do in the actual service
     * code if the context data (such as an hour in this case) are not available in the request URI but in the
     * message payload
     * 
     * @param hour
     */
    private void assertClientMayUpdate(int hour) {
        OAuthContext oauth = getOAuthContext();
        List<OAuthPermission> perms = oauth.getPermissions();
        boolean checkPassed = false;
        for (OAuthPermission perm : perms) {
            if (perm.getPermission().startsWith(OAuthConstants.UPDATE_CALENDAR_SCOPE)) {
                int authorizedHour = Integer.valueOf(perm.getPermission()
                    .substring(OAuthConstants.UPDATE_CALENDAR_SCOPE.length()));
                if (authorizedHour == 24 || authorizedHour == hour) {
                    checkPassed = true;
                }
            }
        }
        if (!checkPassed) {
            throw new WebApplicationException(403);
        }
    }
	
	private OAuthContext getOAuthContext() {
	    OAuthContext oauth = mc.getContent(OAuthContext.class);
        if (oauth == null || oauth.getSubject() == null || oauth.getSubject().getLogin() == null) {
            throw new WebApplicationException(403);
        }
        return oauth;
	}
	
	
}
