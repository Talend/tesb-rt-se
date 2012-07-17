/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.service;

import oauth2.common.Calendar;

public class UserAccount {
    private String name;
    private String password;
    
    private String accountAlias;
    
    private Calendar calendar = new Calendar();

    public UserAccount(String name, String password) {
    	this.name = name;
    	this.password = password;
    }
    
    public UserAccount(String name, String password, String alias) {
    	this.name = name;
    	this.password = password;
    	if (alias != null) {
    	    this.accountAlias = alias;
    	} else {
    		this.accountAlias = name;
    	}
    }
    
    public String getName() {
    	return name;
    }
    
    public String getPassword() {
    	return password;
    }
    
	public Calendar getCalendar() {
		return calendar;
	}

	public String getAccountAlias() {
		return accountAlias;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}
}
