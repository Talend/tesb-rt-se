package org.apache.esb.sts.provider.operation;

import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;

public class RenewDelegate implements RenewOperation {

	@Override
	public RequestSecurityTokenResponseType renew(
			RequestSecurityTokenType request) {
		System.out.println("dummy renew");
		return null;
	}

}
