package org.talend.esb.mep.requestcallback.feature;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.wsdl11.WSDLServiceFactory;
import org.talend.esb.mep.requestcallback.impl.wsdl.PLRole;
import org.talend.esb.mep.requestcallback.impl.wsdl.PLType;

public class CallbackInfo {

	public static class OperationMapping {

		private final String requestOperation;
		private final String callbackOperation;
		private final boolean isFaultCallback;

		public OperationMapping(String requestOperation, String callbackOperation, boolean isFaultCallback) {
			super();
			this.requestOperation = requestOperation;
			this.callbackOperation = callbackOperation;
			this.isFaultCallback = isFaultCallback;
		}

		public String getRequestOperation() {
			return requestOperation;
		}

		public String getCallbackOperation() {
			return callbackOperation;
		}

		public boolean isFaultCallback() {
			return isFaultCallback;
		}
	}

	private static final String SR_QUERY_PATH = "/services/registry/lookup/wsdl/";
	// private static final String POLICY_QUERY_HINT = "mergeWithPolicies=true";
	private static final int SR_QUERY_PATH_LEN = SR_QUERY_PATH.length();

	private QName portTypeName = null;
	private QName callbackPortTypeName = null;
	private QName callbackServiceName = null;
	private String callbackPortName = null;
	private final String wsdlLocation;
	private String callbackWsdlLocation = null;
	private final List<OperationMapping> operationMappings = new ArrayList<OperationMapping>();
	// private boolean wsdlPolicyQuery = false;

	public CallbackInfo(URL wsdlLocation) {
		this(wsdlLocation.toExternalForm());
	}

	public CallbackInfo(String wsdlLocation) {
		this(createServiceFactory(wsdlLocation).getDefinition(), wsdlLocation);
	}

	public CallbackInfo(CallbackInfo source, QName service, String port, boolean copyWsdlLocation) {
		portTypeName = source.portTypeName;
		callbackPortTypeName = source.callbackPortTypeName;
		callbackServiceName = service;
		callbackPortName = port;
		if (copyWsdlLocation) {
			wsdlLocation = source.wsdlLocation;
			if (service != null && service.equals(source.callbackServiceName)) {
				callbackWsdlLocation = source.callbackWsdlLocation;
				// wsdlPolicyQuery = source.wsdlPolicyQuery;
			}
		} else {
			wsdlLocation = null;
		}
		operationMappings.addAll(source.operationMappings);
	}

	public QName getPortTypeName() {
		return portTypeName;
	}

	public QName getCallbackPortTypeName() {
		return callbackPortTypeName;
	}

	public QName getCallbackServiceName() {
		return callbackServiceName;
	}

	public String getCallbackPortName() {
		return callbackPortName;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	public List<OperationMapping> getOperationMappings() {
		return operationMappings;
	}

	public String getSpecificCallbackSenderWsdlLocation(String policyAlias) {
		return getCallbackWsdlLocation(policyAlias, true);
	}

	public String getSpecificCallbackReceiverWsdlLocation(String policyAlias) {
		return getCallbackWsdlLocation(policyAlias, false);
	}

	public String getEffectiveCallbackSenderWsdlLocation(String policyAlias) {
		final String loc = getCallbackWsdlLocation(policyAlias, true);
		return loc == null ? wsdlLocation : loc;
	}

	public String getEffectiveCallbackReceiverWsdlLocation(String policyAlias) {
		final String loc = getCallbackWsdlLocation(policyAlias, false);
		return loc == null ? wsdlLocation : loc;
	}

	private CallbackInfo(Definition definition, String wsdlLocation) {
		super();
		this.wsdlLocation = wsdlLocation;
		String portTypeHint = null;
		String callbackPortTypeHint = null;
		for (Object ee : definition.getExtensibilityElements()) {
			if (!(ee instanceof PLType)) {
				continue;
			}
			final PLType pl = (PLType) ee;
			if (pl != null) {
				final List<PLRole> roles = pl.getRoles();
				if (roles != null) {
					for (PLRole role : roles) {
						final String name = role.getName();
						if ("service".equals(name)) {
							portTypeHint = role.getPortType().getName();
						} else if ("callback".equals(name)) {
							callbackPortTypeHint = role.getPortType().getName();
						}
					}
				}
				if (portTypeHint != null && callbackPortTypeHint != null) {
					break;
				}
			}
		}
		if (portTypeHint == null || callbackPortTypeHint == null) {
			return;
		}
		for (Object o : definition.getPortTypes().entrySet()) {
			Entry<?, ?> entry = (Entry<?, ?>) o;
			QName portTypeName = (QName) entry.getKey();
			PortType portType = (PortType) entry.getValue();
			if (representsName(portTypeName, portTypeHint, definition)) {
				this.portTypeName = portTypeName;
			} else if (representsName(portTypeName, callbackPortTypeHint, definition)) {
				this.callbackPortTypeName = portTypeName;
				for (Object op : portType.getOperations()) {
					Operation operation = (Operation) op;
					String partnerOpName = null;
					boolean isFault = false;
					for (Object att : operation.getExtensionAttributes().entrySet()) {
						Entry<?, ?> attEntry = (Entry<?, ?>) att;
						QName attName = (QName) attEntry.getKey();
						QName value = (QName) attEntry.getValue();
						if ("partnerOperation".equals(attName.getLocalPart())) {
							partnerOpName = value.getLocalPart();
						} else if ("faultOperation".equals(attName.getLocalPart())) {
							isFault = "true".equalsIgnoreCase(value.getLocalPart());
						}
					}
					if (partnerOpName != null) {
						this.operationMappings.add(new OperationMapping(partnerOpName, operation.getName(), isFault));
					}
				}
			}
		}
		if (callbackPortTypeName != null) {
			for (Object o : definition.getServices().entrySet()) {
				Entry<?, ?> entry = (Entry<?, ?>) o;
				QName serviceName = (QName) entry.getKey();
				Service service = (Service) entry.getValue();
				for (Object p : service.getPorts().entrySet()) {
					Entry<?, ?> portEntry = (Entry<?, ?>) p;
					String portName = (String) portEntry.getKey();
					Port port = (Port) portEntry.getValue();
					Binding b = port.getBinding();
					if (callbackPortTypeName.equals(b.getPortType().getQName())) {
						callbackServiceName = serviceName;
						callbackPortName = portName;
					}
				}
			}
		}
	}

	private String getCallbackWsdlLocation(String policyAlias, boolean isSender) {
		if (callbackWsdlLocation == null) {
			callbackWsdlLocation = callbackWsdlLocation(
					wsdlLocation, callbackServiceName);
			// wsdlPolicyQuery = wsdlLocation.indexOf(POLICY_QUERY_HINT) >= 0;
		}
		if (callbackWsdlLocation.length() == 0) {
			return null;
		}
		String resString = callbackWsdlLocation;
    	// if (wsdlPolicyQuery) {
		//	resString += isSender
		//			? callbackSenderPolicyQuery(policyAlias)
		//					: callbackReceiverPolicyQuery(policyAlias);
    	// }
		resString += isSender
				? callbackSenderPolicyQuery(policyAlias)
						: callbackReceiverPolicyQuery(policyAlias);
    	return resString;
	}

	private static WSDLServiceFactory createServiceFactory(String wsdlLocation) {
		final Bus b = CXFBusFactory.getThreadDefaultBus();
		RequestCallbackFeature.applyWsdlExtensions(b);
		return new WSDLServiceFactory(b, wsdlLocation);
	}

	private static boolean representsName(QName fullName, String abbrevatedName, Definition definition) {
		final int ndx = abbrevatedName.indexOf(':');
		if (ndx < 0) {
			return abbrevatedName.equals(fullName.getLocalPart());
		}
		final String prefix = abbrevatedName.substring(0, ndx);
		final String localName = abbrevatedName.substring(ndx + 1);
		final String namespace = definition.getNamespace(prefix);
		if (namespace == null) {
			return false;
		}
		return namespace.equals(fullName.getNamespaceURI()) && localName.equals(fullName.getLocalPart());
	}

	private static String callbackWsdlLocation(final String wsdlLocation,
    		final QName callbackService) {
    	if (wsdlLocation == null || callbackService == null ||
    			!(wsdlLocation.startsWith("http://") ||
    					wsdlLocation.startsWith("https://"))) {
    		return "";
    	}
    	final int ndx = wsdlLocation.indexOf(SR_QUERY_PATH);
    	if (ndx < 0) {
    		return "";
    	}
    	try {
			return wsdlLocation.substring(0,
					wsdlLocation.indexOf(SR_QUERY_PATH) + SR_QUERY_PATH_LEN)
					+ URLEncoder.encode(callbackService.toString(), "UTF-8");
    	} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unexpected URL creation problem: ", e);
    	}
    }

    private static String callbackSenderPolicyQuery(String policyAlias) {
    	switch (CallContext.EFFECTIVE_POLICY_DISTRIBUTION_MODE) {
    	  case EXCHANGE:
      		if (policyAlias == null || policyAlias.length() == 0) {
    			return "?mergeWithPolicies=true&participant=consumer";
    		}
    		return "?mergeWithPolicies=true&participant=consumer&consumerPolicyAlias="
    				+ policyAlias;
    	  case SERVICE:
    		return "?mergeWithPolicies=true&participant=provider";
    	  default:
    		throw new IllegalStateException(
    				"Invalid configuration of policy distribution mode. ");
    	}
    }

    private static String callbackReceiverPolicyQuery(String policyAlias) {
    	switch (CallContext.EFFECTIVE_POLICY_DISTRIBUTION_MODE) {
    	  case EXCHANGE:
    		return "?mergeWithPolicies=true&participant=provider";
    	  case SERVICE:
    		if (policyAlias == null || policyAlias.length() == 0) {
    			return "?mergeWithPolicies=true&participant=consumer";
    		}
    		return "?mergeWithPolicies=true&participant=consumer&consumerPolicyAlias="
    				+ policyAlias;
    	  default:
    		throw new IllegalStateException(
    				"Invalid configuration of policy distribution mode. ");
    	}
    }
}
