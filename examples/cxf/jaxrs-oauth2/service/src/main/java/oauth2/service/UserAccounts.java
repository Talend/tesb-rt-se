/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.service;

import java.util.concurrent.ConcurrentHashMap;

public class UserAccounts {

	private ConcurrentHashMap<String, UserAccount> accounts = 
		new ConcurrentHashMap<String, UserAccount>();
	private ConcurrentHashMap<String, UserAccount> accountAliases = 
			new ConcurrentHashMap<String, UserAccount>();
	
	public void setAccount(String userName, UserAccount account) {
		accounts.putIfAbsent(userName, account);
		if (account.getAccountAlias() != null) {
		    accountAliases.putIfAbsent(account.getAccountAlias(), account);
		}
	}
	
	public UserAccount getAccount(String name) {
		return accounts.get(name);
	}
	
	public UserAccount getAccountWithAlias(String alias) {
		return accountAliases.get(alias);
	}
	
	public UserAccount removeAccount(String name) {
		UserAccount account = accounts.remove(name);
		if (account != null && account.getAccountAlias() != null) {
			accountAliases.remove(account.getAccountAlias());
		}
		return account;
	}
	
}
