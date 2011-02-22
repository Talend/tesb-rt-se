package org.apache.esb.sts.provider.token;

/**
 *
 */
public class TokenException extends RuntimeException {

	private static final long serialVersionUID = 5745640698200387659L;

	public TokenException(String message) {
		super(message);
	}

	public TokenException(String message, Throwable e) {
		super(message, e);
	}

}
