package org.apache.esb.sts.provider.token;

import org.w3c.dom.Element;

public interface TokenProvider {

	String getTokenType();
	
	Element createToken(String username);
	
	String getTokenId(Element token);
}
