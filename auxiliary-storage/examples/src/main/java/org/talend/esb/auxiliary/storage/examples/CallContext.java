package org.talend.esb.auxiliary.storage.examples;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class CallContext {

	private QName portTypeName;
	private QName serviceName;
	private QName operationName;
	private String requestId;
	private String correlationId;
	private String callbackId;
	private String replyToAddress;
	private URL wsdlLocationURL;
	private Map<String, String> userData;

	public QName getPortTypeName() {
		return portTypeName;
	}

	public void setPortTypeName(QName portTypeName) {
		this.portTypeName = portTypeName;
	}

	public QName getServiceName() {
		return serviceName;
	}

	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}

	public QName getOperationName() {
		return operationName;
	}

	public void setOperationName(QName operationName) {
		this.operationName = operationName;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}

	public String getReplyToAddress() {
		return replyToAddress;
	}

	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

	public String getWsdlLocation() {
		return wsdlLocationURL == null ? null : wsdlLocationURL
				.toExternalForm();
	}

	public void setWsdlLocation(String wsdlLocation)
			throws MalformedURLException {
		this.wsdlLocationURL = wsdlLocation == null ? null : new URL(
				wsdlLocation);
	}

	public void setWsdlLocation(File wsdlLocation) throws MalformedURLException {
		this.wsdlLocationURL = wsdlLocation == null ? null : wsdlLocation
				.toURI().toURL();
	}

	public void setWsdlLocation(URL wsdlLocation) {
		setWsdlLocationURL(wsdlLocation);
	}

	public URL getWsdlLocationURL(URL wsdlLocationURL) {
		return wsdlLocationURL;
	}

	public void setWsdlLocationURL(URL wsdlLocationURL) {
		this.wsdlLocationURL = wsdlLocationURL;
	}

	public Map<String, String> getUserData() {
		if (userData == null) {
			userData = new HashMap<String, String>();
		}
		return userData;
	}

	public boolean hasUserData() {
		return userData != null && !userData.isEmpty();
	}
}
