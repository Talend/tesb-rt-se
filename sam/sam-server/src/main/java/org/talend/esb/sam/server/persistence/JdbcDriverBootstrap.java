package org.talend.esb.sam.server.persistence;

public class JdbcDriverBootstrap {
	public static Class forName(String className) throws ClassNotFoundException {
		System.out.println("loading Class for className: " + className);
		return Class.forName(className);
	}

}
