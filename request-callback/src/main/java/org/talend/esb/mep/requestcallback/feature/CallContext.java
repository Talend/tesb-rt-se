package org.talend.esb.mep.requestcallback.feature;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.factory.AbstractServiceConfiguration;
import org.apache.cxf.service.factory.DefaultServiceConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.mep.requestcallback.impl.wsdl.CallbackDefaultServiceConfiguration;

public class CallContext implements Serializable {

	private static final String NULL_MEANS_ONEWAY = "jaxws.provider.interpretNullAsOneway";
	private static final String CLASSPATH_URL_PREFIX = "classpath:";
	private static final long serialVersionUID = -5024912330689208965L;
	private static final int CLASSPATH_URL_PREFIX_LENGTH = CLASSPATH_URL_PREFIX.length();

	private QName portTypeName;
	private QName serviceName;
	private QName operationName;
	private String requestId;
	private String callId;
	private String correlationId;
	private String callbackId;
	private String replyToAddress;
	private String bindingId;
	private String flowId;  // Service Activity Monitoring flowId
	private URL wsdlLocationURL;
	private Map<String, String> userData;
	private transient CallbackInfo callbackInfo = null;
	private static boolean logging = false;
	private static boolean serviceActivityMonitoring = false;

	private static ClassPathXmlApplicationContext samContext = null;

	public QName getPortTypeName() {
		return portTypeName;
	}

	public void setPortTypeName(QName portTypeName) {
		this.portTypeName = portTypeName;
		this.callbackInfo = null;
	}

	public QName getServiceName() {
		return serviceName;
	}

	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
		this.callbackInfo = null;
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

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId){
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
		this.wsdlLocationURL = toWsdlUrl(wsdlLocation);
		this.callbackInfo = null;
	}

	public void setWsdlLocation(File wsdlLocation) throws MalformedURLException {
		this.wsdlLocationURL = wsdlLocation == null ? null : wsdlLocation.toURI().toURL();
		this.callbackInfo = null;
	}

	public void setWsdlLocation(URL wsdlLocation) {
		setWsdlLocationURL(wsdlLocation);
	}

	public URL getWsdlLocationURL() {
		return wsdlLocationURL;
	}

	public void setWsdlLocationURL(URL wsdlLocationURL) {
		this.wsdlLocationURL = wsdlLocationURL;
		this.callbackInfo = null;
	}

	public CallbackInfo getCallbackInfo() {
		if (callbackInfo == null && wsdlLocationURL != null) {
			callbackInfo = new CallbackInfo(wsdlLocationURL);
		}
		return callbackInfo;
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

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public static boolean isServiceActivityMonitoring() {
        return serviceActivityMonitoring;
    }

    public static void setServiceActivityMonitoring(boolean value) {
        CallContext.serviceActivityMonitoring = value;
        if (CallContext.serviceActivityMonitoring && samContext == null) {
            samContext = new ClassPathXmlApplicationContext(
                    new String[] {"/META-INF/tesb/agent-context.xml"});
        }
    }

	public <T> T createCallbackProxy(Class<T> proxyInterface) {
        final JaxWsProxyFactoryBean callback = new JaxWsProxyFactoryBean();
        callback.setServiceName(serviceName);
        callback.setEndpointName(new QName(serviceName.getNamespaceURI(), serviceName.getLocalPart() + "Port"));
        callback.setAddress(replyToAddress);
        callback.setServiceClass(proxyInterface);
        final List<Feature> features = callback.getFeatures();
        features.add(new RequestCallbackFeature());
        if (logging) {
        	features.add(new LoggingFeature());
        }
        if (serviceActivityMonitoring) {
            features.add(getEventFeature());
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME, this);
        callback.setProperties(properties);

        return callback.create(proxyInterface);
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(
			Class<T> sourceClass, Service.Mode mode, QName operation, URL wsdlLocationURL) {
		final QName callbackPortTypeName = new QName(
				portTypeName.getNamespaceURI(), portTypeName.getLocalPart() + "Consumer");
		final QName callbackServiceName = new QName(
				callbackPortTypeName.getNamespaceURI(), callbackPortTypeName.getLocalPart() + "Service");
		final QName callbackPortName = new QName(
				callbackPortTypeName.getNamespaceURI(), callbackPortTypeName.getLocalPart() + "Port");

		Service service = null;
		final URL wsdlURL = wsdlLocationURL == null ? this.wsdlLocationURL : wsdlLocationURL;
		if (wsdlURL != null) {
			try {
				service = Service.create(wsdlURL, callbackServiceName);
			} catch (WebServiceException e) {
				// ignore, as old-style request-callback WSDLs will fail here.
			}
		}
		final Dispatch<T> dispatch;
		if (service != null) {
			if (!service.getPorts().hasNext()) {
				service.addPort(callbackPortName, bindingId, replyToAddress);
			}
			dispatch = service.createDispatch(
	        		callbackPortName, sourceClass, mode);
	        dispatch.getRequestContext().put(
	        		BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replyToAddress);
		} else {
			service = Service.create(callbackServiceName);
			service.addPort(callbackPortName, bindingId, replyToAddress);
			dispatch = service.createDispatch(
	        		callbackPortName, sourceClass, mode);
		}
         
        setupDispatch(dispatch);
        final Map<String, Object> requestContext = dispatch.getRequestContext();
        requestContext.put(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME, this);
        // The current request context is still not thread local, but subsequent
        // calls to dispatch.getRequestContext() return a thread local one.
        requestContext.put(JaxWsClientProxy.THREAD_LOCAL_REQUEST_CONTEXT, Boolean.TRUE);
        if (operation != null) {
            requestContext.put(MessageContext.WSDL_OPERATION, operation);
            requestContext.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            requestContext.put(BindingProvider.SOAPACTION_URI_PROPERTY, operation.getLocalPart());
        }
		return dispatch;
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(
			Class<T> sourceClass, Service.Mode mode, QName operation) {
		return createCallbackDispatch(sourceClass, mode, operation, null);
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(Class<T> sourceClass, QName operation, URL wsdlLocation) {
		return createCallbackDispatch(sourceClass, Service.Mode.PAYLOAD, operation, wsdlLocation);
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(Class<T> sourceClass, QName operation) {
		return createCallbackDispatch(sourceClass, Service.Mode.PAYLOAD, operation, null);
	}

	public <T extends Source> Dispatch<T> createCallbackDispatch(Class<T> sourceClass) {
		return createCallbackDispatch(sourceClass, Service.Mode.PAYLOAD, null, null);
	}

	public Dispatch<StreamSource> createCallbackDispatch(QName operation) {
		return createCallbackDispatch(StreamSource.class, Service.Mode.PAYLOAD, operation, null);
	}

	public Dispatch<StreamSource> createCallbackDispatch() {
		return createCallbackDispatch(StreamSource.class, Service.Mode.PAYLOAD, null, null);
	}

	public static CallContext getCallContext(WebServiceContext wsContext) {
		return getCallContext(wsContext.getMessageContext());
	}

	public static CallContext getCallContext(Map<?, ?> contextHolder) {
		try {
			return (CallContext) contextHolder.get(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Endpoint createCallbackEndpoint(Object implementor, String wsdlLocation) {
		return createCallbackEndpoint(implementor, new CallbackInfo(wsdlLocation));
	}

	public static Endpoint createCallbackEndpoint(Object implementor, URL wsdlLocation) {
		return createCallbackEndpoint(implementor, new CallbackInfo(wsdlLocation));
	}

	public static void setCallbackEndpoint(Dispatch<?> dispatch, Object callbackEndpoint) {
		dispatch.getRequestContext().put(RequestCallbackFeature.CALLBACK_ENDPOINT_PROPERTY_NAME, callbackEndpoint);
	}

	public static void setCallbackEndpoint(Map<String, Object> context, Object callbackEndpoint) {
		context.put(RequestCallbackFeature.CALLBACK_ENDPOINT_PROPERTY_NAME, callbackEndpoint);
	}

	public static void setupEndpoint(Endpoint endpoint) {
		if (!(endpoint instanceof EndpointImpl)) {
			throw new IllegalArgumentException("Only CXF JAX-WS endpoints supported. ");
		}
		final EndpointImpl ep = (EndpointImpl) endpoint;
        final List<Feature> features = new ArrayList<Feature>();
        features.add(new RequestCallbackFeature());
        if (logging) {
        	features.add(new LoggingFeature());
        }
        if (serviceActivityMonitoring) {
            features.add(getEventFeature());
        }
        if (ep.getFeatures() != null) {
            features.addAll(ep.getFeatures());
        }
        ep.setFeatures(features);
        ep.getProperties().put(NULL_MEANS_ONEWAY, Boolean.TRUE);
	}

	public static void setupDispatch(Dispatch<?> dispatch) {
		if (!(dispatch instanceof DispatchImpl)) {
			throw new IllegalArgumentException("Only CXF JAX-WS Dispatch supported. ");
		}
		final DispatchImpl<?> dsp = (DispatchImpl<?>) dispatch;
        final Client dispatchClient = dsp.getClient();
        final Bus bus = dispatchClient.getBus();
        (new RequestCallbackFeature()).initialize(dispatchClient, bus);
        if (logging) {
	        (new LoggingFeature()).initialize(dispatchClient, bus);
        }
        if (serviceActivityMonitoring) {
            getEventFeature().initialize(dispatchClient, bus);
        }
	}

	public static void setupDispatch(Dispatch<?> dispatch, Object callbackEndpoint) {
		setupDispatch(dispatch);
		setCallbackEndpoint(dispatch, callbackEndpoint);
	}

	public static void setupServerFactory(JaxWsServerFactoryBean serverFactory) {
		final List<Feature> features = serverFactory.getFeatures();
        features.add(new RequestCallbackFeature());
        if (logging) {
	        features.add(new LoggingFeature());
        }
        if (serviceActivityMonitoring) {
            features.add(getEventFeature());
        }
        serverFactory.getProperties(true).put(NULL_MEANS_ONEWAY, Boolean.TRUE);
	}

	public <T> void setupCallbackProxy(T proxy) {
		final Client client = ClientProxy.getClient(proxy);
		final Bus bus = client.getBus();
        (new RequestCallbackFeature()).initialize(client, bus);
        if (logging) {
	        (new LoggingFeature()).initialize(client, bus);
        }
        if (serviceActivityMonitoring) {
        	getEventFeature().initialize(client, bus);
        }
        final BindingProvider bp = (BindingProvider) proxy;
		bp.getRequestContext().put(
				JaxWsClientProxy.THREAD_LOCAL_REQUEST_CONTEXT, Boolean.TRUE);
		bp.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replyToAddress);
		bp.getRequestContext().put(
				RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME, this);
	}

	public static CallbackInfo createCallbackInfo(String wsdlLocation) {
		return new CallbackInfo(wsdlLocation);
	}

	public static CallbackInfo createCallbackInfo(URL wsdlLocationURL) {
		return new CallbackInfo(wsdlLocationURL);
	}

	public static void enforceOperation(QName operationName, Dispatch<?> dispatch) {
		enforceOperation(operationName, dispatch.getRequestContext());
	}

	public static void enforceOperation(QName operationName, Map<String, Object> requestContext) {
        requestContext.put(MessageContext.WSDL_OPERATION, operationName);
        requestContext.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        requestContext.put(BindingProvider.SOAPACTION_URI_PROPERTY, operationName.getLocalPart());
	}

	public static Configuration resolveConfiguration(QName serviceName) {
		return ConfigurationInitializer.resolveConfiguration(serviceName);
	}

	public static Endpoint createCallbackEndpoint(Object implementor, CallbackInfo cbInfo) {
		final Bus bus = BusFactory.getThreadDefaultBus();
		final JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
        final List<Feature> features = new ArrayList<Feature>();
        features.add(new RequestCallbackFeature());
        if (logging) {
        	features.add(new LoggingFeature());
        }
        if (serviceActivityMonitoring) {
            features.add(getEventFeature());
        }

		serverFactory.setFeatures(features);
		final QName cbInterfaceName = cbInfo == null ? null : cbInfo.getCallbackPortTypeName();
		final String wsdlLocation = cbInfo == null ? null : cbInfo.getWsdlLocation();
		final boolean useWsdlLocation = wsdlLocation != null && cbInfo.getCallbackServiceName() != null &&
				cbInfo.getCallbackPortName() != null;
		if (cbInterfaceName != null) {
			final QName cbServiceName = cbInfo.getCallbackServiceName() == null
					? new QName(cbInterfaceName.getNamespaceURI(), cbInterfaceName.getLocalPart() + "Service")
					: cbInfo.getCallbackServiceName();
			final QName cbEndpointName = cbInfo.getCallbackServiceName() == null
					? new QName(cbInterfaceName.getNamespaceURI(), cbInterfaceName.getLocalPart() + "ServicePort")
					: new QName(cbServiceName.getNamespaceURI(), cbInfo.getCallbackPortName() == null
							? cbServiceName.getLocalPart() + "Port"
							: cbInfo.getCallbackPortName());
			serverFactory.setServiceName(cbServiceName);
			serverFactory.setEndpointName(cbEndpointName);
			final List<AbstractServiceConfiguration> svcConfigs = serverFactory.getServiceFactory().getServiceConfigurations();
			for (ListIterator<AbstractServiceConfiguration> it = svcConfigs.listIterator(); it.hasNext(); ) {
				final AbstractServiceConfiguration cfg = it.next();
				if (cfg instanceof DefaultServiceConfiguration) {
					final AbstractServiceConfiguration ncfg = new CallbackDefaultServiceConfiguration(cbInfo);
					it.set(ncfg);
				}
			}
			if (useWsdlLocation) {
				serverFactory.setWsdlLocation(wsdlLocation);
			}
		}
		final EndpointImpl endpoint = new EndpointImpl(bus, implementor, serverFactory);
		endpoint.setFeatures(features);
        endpoint.getProperties().put(NULL_MEANS_ONEWAY, Boolean.TRUE);
        if (cbInterfaceName != null) {
        	endpoint.setEndpointName(serverFactory.getEndpointName());
        	endpoint.setServiceName(serverFactory.getServiceName());
        	if (useWsdlLocation) {
        		endpoint.setWsdlLocation(wsdlLocation);
        	}
        }
		return endpoint;
	}

	private static Feature getEventFeature() {
	    return (Feature) samContext.getBean("eventFeature");
	}

	private static URL toWsdlUrl(final String wsdlLocation) throws MalformedURLException {
		if (wsdlLocation == null || wsdlLocation.length() == 0) {
			return null;
		}
        if (isWsdlUrlString(wsdlLocation)) {
        	if (wsdlLocation.startsWith(CLASSPATH_URL_PREFIX)) {
        		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        		if (cl == null) {
        			return null;
        		}
        		final int len = wsdlLocation.length();
        		for (int ndx = CLASSPATH_URL_PREFIX_LENGTH; ndx < len; ndx++) {
        			if (wsdlLocation.charAt(ndx) != '/') {
        				return cl.getResource(wsdlLocation.substring(ndx));
        			}
        		}
        		return null;
        	}
            return new URL(wsdlLocation);
        }
        return (new File(wsdlLocation)).toURI().toURL();
    }

    private static boolean isWsdlUrlString(String wsdlLocation) {
        if (wsdlLocation == null || wsdlLocation.length() == 0) {
            return false;
        }
        return wsdlLocation.startsWith("file:/") || wsdlLocation.startsWith("http://")
                || wsdlLocation.startsWith("https://")
                || wsdlLocation.startsWith(CLASSPATH_URL_PREFIX);
    }
}
