/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.service;

import java.util.concurrent.ConcurrentHashMap;

public class UserAccounts {

	private ConcurrentHashMap<String, UserAccount> accounts = 
		new ConcurrentHashMap<String, UserAccount>();
	
	public void setAccount(String userName, UserAccount account) {
		accounts.putIfAbsent(userName, account);
	}
	
	public UserAccount getAccount(String name) {
		return accounts.get(name);
	}
	
	public UserAccount removeAccount(String name) {
		return accounts.remove(name);
	}
	
}
