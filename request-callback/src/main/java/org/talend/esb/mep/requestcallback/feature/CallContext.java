package org.talend.esb.mep.requestcallback.feature;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class CallContext implements Serializable {
	
	private static final long serialVersionUID = -5024912330689208965L;
	
	private QName portTypeName;
	private QName serviceName;
	private QName operationName;
	private String requestId;
	private String callId;
	private String callbackId;
	private String replyToAddress;
	private String bindingId;
	private URL wsdlLocationURL;
	private Map<String, String> userData;
	private static boolean logging = false;

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

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
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

	public String getBindingId() {
		return bindingId;
	}

	public void setBindingId(String bindingId) {
		this.bindingId = bindingId;
	}

	public String getWsdlLocation() {
		return wsdlLocationURL == null ? null : wsdlLocationURL.toExternalForm();
	}

	public void setWsdlLocation(String wsdlLocation) throws MalformedURLException {
		this.wsdlLocationURL = wsdlLocation == null ? null : new URL(wsdlLocation);
	}

	public void setWsdlLocation(File wsdlLocation) throws MalformedURLException {
		this.wsdlLocationURL = wsdlLocation == null ? null : wsdlLocation.toURI().toURL();
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

	public static boolean isLogging() {
		return logging;
	}

	public static void setLogging(boolean logging) {
		CallContext.logging = logging;
	}

	public <T> T createCallbackProxy(Class<T> proxyInterface) {
        JaxWsProxyFactoryBean callback = new JaxWsProxyFactoryBean();
        // callback.setServiceName(serviceName);
        // callback.setEndpointName(new QName(serviceName.getNamespaceURI(), serviceName.getLocalPart() + "Port"));
        callback.setAddress(replyToAddress);
        // callback.setServiceClass(proxyInterface);
        callback.getFeatures().add(new RequestCallbackFeature());
        if (logging) {
        	callback.getFeatures().add(new LoggingFeature());
        }
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME, this);
        callback.setProperties(properties);
        return callback.create(proxyInterface);
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(
			Class<T> sourceClass, Service.Mode mode, QName operation) {
		QName callbackPortTypeName = new QName(
				portTypeName.getNamespaceURI(), portTypeName.getLocalPart() + "Consumer");
		QName callbackServiceName = new QName(
				callbackPortTypeName.getNamespaceURI(), callbackPortTypeName.getLocalPart() + "Service");
		QName callbackPortName = new QName(
				callbackPortTypeName.getNamespaceURI(), callbackPortTypeName.getLocalPart() + "Port");
        Service service = Service.create(callbackServiceName);
        service.addPort(callbackPortName, bindingId, replyToAddress);
        Dispatch<T> dispatch = service.createDispatch(
        		callbackPortName, sourceClass, mode);
        setupDispatch(dispatch);
        Map<String, Object> requestContext = dispatch.getRequestContext();
        requestContext.put(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME, this);
        requestContext.put("thread.local.request.context", "true");
        if (operation != null) {
            requestContext.put(MessageContext.WSDL_OPERATION, operationName);
            requestContext.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            requestContext.put(BindingProvider.SOAPACTION_URI_PROPERTY, operationName.getLocalPart());
        }
		return dispatch;
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(Class<T> sourceClass, QName operation) {
		return createCallbackDispatch(sourceClass, Service.Mode.PAYLOAD, operation);
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(Class<T> sourceClass) {
		return createCallbackDispatch(sourceClass, Service.Mode.PAYLOAD, null);
	}

	public Dispatch<StreamSource> createCallbackDispatch(QName operation) {
		return createCallbackDispatch(StreamSource.class, Service.Mode.PAYLOAD, operation);
	}

	public Dispatch<StreamSource> createCallbackDispatch() {
		return createCallbackDispatch(StreamSource.class, Service.Mode.PAYLOAD, null);
	}

	public static CallContext getCallContext(Map<?, ?> contextHolder) {
		try {
			return (CallContext) contextHolder.get(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static void setCallbackEndpoint(Map<String, Object> context, Object callbackEndpoint) {
		context.put(RequestCallbackFeature.CALLBACK_ENDPOINT_PROPERTY_NAME, callbackEndpoint);
	}

	public static void setupEndpoint(Endpoint endpoint) {
		if (!(endpoint instanceof EndpointImpl)) {
			throw new IllegalArgumentException("Only CXF JAX-WS endpoints supported. ");
		}
		EndpointImpl ep = (EndpointImpl) endpoint;
        List<Feature> features = new ArrayList<Feature>();
        features.add(new RequestCallbackFeature());
        if (logging) {
        	features.add(new LoggingFeature());
        }
        ep.setFeatures(features);
        ep.getProperties().put("jaxws.provider.interpretNullAsOneway", Boolean.TRUE);
	}

	public static void setupDispatch(Dispatch<?> dispatch) {
		if (!(dispatch instanceof DispatchImpl)) {
			throw new IllegalArgumentException("Only CXF JAX-WS Dispatch supported. ");
		}
		DispatchImpl<?> dsp = (DispatchImpl<?>) dispatch;
        Client dispatchClient = dsp.getClient();
        RequestCallbackFeature requestCallbackFeature = new RequestCallbackFeature();
        requestCallbackFeature.initialize(dispatchClient, dispatchClient.getBus());
        if (logging) {
	        LoggingFeature loggingFeature = new LoggingFeature();
	        loggingFeature.initialize(dispatchClient, dispatchClient.getBus());
        }
	}
}
