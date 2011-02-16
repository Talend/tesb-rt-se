package org.apache.esb.sts.provider;

import org.apache.esb.sts.provider.operation.CancelOperation;
import org.apache.esb.sts.provider.operation.IssueOperation;
import org.apache.esb.sts.provider.operation.KeyExchangeTokenOperation;
import org.apache.esb.sts.provider.operation.RenewOperation;
import org.apache.esb.sts.provider.operation.RequestCollectionOperation;
import org.apache.esb.sts.provider.operation.ValidateOperation;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.wsdl.SecurityTokenService;

@javax.jws.WebService(
        serviceName = "SecurityTokenServiceProvider",
        portName = "SecurityTokenServiceSOAP",
        targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/wsdl",
        wsdlLocation = "WEB-INF/classes/model/ws-trust-1.4-service.wsdl",
        endpointInterface = "org.oasis_open.docs.ws_sx.ws_trust._200512.wsdl.SecurityTokenService")
public class SecurityTokenServiceImpl implements SecurityTokenService {

	private CancelOperation cancelOperation;
	private IssueOperation issueOperation;
	private KeyExchangeTokenOperation keyExchangeTokenOperation;
	private RenewOperation renewOperation;
	private RequestCollectionOperation requestCollectionOperation;
	private ValidateOperation validateOperation;

	public void setCancelOperation(CancelOperation cancelOperation) {
		this.cancelOperation = cancelOperation;
	}

	public void setIssueOperation(IssueOperation issueOperation) {
		this.issueOperation = issueOperation;
	}

	public void setKeyExchangeTokenOperation(
			KeyExchangeTokenOperation keyExchangeTokenOperation) {
		this.keyExchangeTokenOperation = keyExchangeTokenOperation;
	}

	public void setRenewOperation(RenewOperation renewOperation) {
		this.renewOperation = renewOperation;
	}

	public void setRequestCollectionOperation(
			RequestCollectionOperation requestCollectionOperation) {
		this.requestCollectionOperation = requestCollectionOperation;
	}

	public void setValidateOperation(ValidateOperation validateOperation) {
		this.validateOperation = validateOperation;
	}

	public RequestSecurityTokenResponseType validate(
			RequestSecurityTokenType request) {
		return validateOperation.validate(request);
	}

	public RequestSecurityTokenResponseCollectionType requestCollection(
			RequestSecurityTokenCollectionType requestCollection) {
		return requestCollectionOperation.requestCollection(requestCollection);
	}

	public RequestSecurityTokenResponseType keyExchangeToken(
			RequestSecurityTokenType request) {
		return keyExchangeTokenOperation.keyExchangeToken(request);
	}

	public RequestSecurityTokenResponseCollectionType issue(
			RequestSecurityTokenType request) {
		return issueOperation.issue(request);
	}

	public RequestSecurityTokenResponseType cancel(
			RequestSecurityTokenType request) {
		return cancelOperation.cancel(request);
	}

	public RequestSecurityTokenResponseType renew(
			RequestSecurityTokenType request) {
		return renewOperation.renew(request);
	}

}
