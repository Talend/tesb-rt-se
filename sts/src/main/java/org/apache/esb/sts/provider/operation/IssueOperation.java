package org.apache.esb.sts.provider.operation;

import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;

public interface IssueOperation {

	RequestSecurityTokenResponseCollectionType issue(RequestSecurityTokenType request);

}
