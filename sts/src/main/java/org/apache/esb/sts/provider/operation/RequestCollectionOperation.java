package org.apache.esb.sts.provider.operation;

import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;

public interface RequestCollectionOperation {

	RequestSecurityTokenResponseCollectionType requestCollection(
			RequestSecurityTokenCollectionType requestCollection);

}
