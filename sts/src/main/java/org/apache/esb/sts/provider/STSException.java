package org.apache.esb.sts.provider;

/**
 *
 */
public class STSException extends RuntimeException {
	
	private static final long serialVersionUID = -6540501345865299260L;

	public STSException(String message) {
		super(message);
	}

	public STSException(String message, Throwable e) {
		super(message, e);
	}

}
