package org.talend.esb.sts.provider;


public class GlobalUser {
	public static String userName;
	public static String userPassword;
	
	public static String getUserName() {
		return userName;
	}
	public static void setUserName(String userName) {
		GlobalUser.userName = userName;
	}
	public static String getUserPassword() {
		return userPassword;
	}
	public static void setUserPassword(String userPassword) {
		GlobalUser.userPassword = userPassword;
	}
	
}
