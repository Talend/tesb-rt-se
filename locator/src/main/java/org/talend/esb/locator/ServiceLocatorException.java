
package org.talend.esb.locator;


public class ServiceLocatorException extends Exception {

	private static final long serialVersionUID = 7546855647307337936L;

	public ServiceLocatorException() {
	}

	public ServiceLocatorException(String msg) {
		super(msg);
	}

	public ServiceLocatorException(Throwable cause) {
		super(cause);
	}

	public ServiceLocatorException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
