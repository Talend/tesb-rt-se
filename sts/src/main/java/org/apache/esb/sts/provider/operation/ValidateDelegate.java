package org.apache.esb.sts.provider.operation;

import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;

public class ValidateDelegate implements ValidateOperation {

	@Override
	public RequestSecurityTokenResponseType validate(
			RequestSecurityTokenType request) {
		System.out.println("dummy validate");
		return null;
	}

}
