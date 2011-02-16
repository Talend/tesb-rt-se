package org.apache.esb.sts.provider.operation;

import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;

public class RequestCollectionDelegate implements RequestCollectionOperation {

	@Override
	public RequestSecurityTokenResponseCollectionType requestCollection(
			RequestSecurityTokenCollectionType requestCollection) {
		System.out.println("dummy requestCollection");
		return null;
	}

}
