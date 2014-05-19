package org.talend.esb.mep.requestcallback.feature;

import java.net.URL;
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

	private QName portTypeName = null;
	private QName callbackPortTypeName = null;
	private QName callbackServiceName = null;
	private QName callbackPortName = null;
	private String wsdlLocation;
	private List<OperationMapping> operationMappings = new ArrayList<OperationMapping>();
	
	public CallbackInfo(URL wsdlLocation) {
		this(createServiceFactory(wsdlLocation).getDefinition(), wsdlLocation.toExternalForm());
	}

	public CallbackInfo(String wsdlLocation) {
		this(createServiceFactory(wsdlLocation).getDefinition(), wsdlLocation);
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

	public QName getCallbackPortName() {
		return callbackPortName;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	public List<OperationMapping> getOperationMappings() {
		return operationMappings;
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
				for (PLRole role : pl.getRoles()) {
					final String name = role.getName();
					if ("service".equals(name)) {
						portTypeHint = role.getPortType().getName();
					} else if ("callback".equals(name)) {
						callbackPortTypeHint = role.getPortType().getName();
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
						this.operationMappings.add(new OperationMapping(operation.getName(), partnerOpName, isFault));
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
					QName portName = (QName) portEntry.getKey();
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

	private static WSDLServiceFactory createServiceFactory(String wsdlLocation) {
		final Bus b = CXFBusFactory.getThreadDefaultBus();
		return new WSDLServiceFactory(b, wsdlLocation);
	}

	private static WSDLServiceFactory createServiceFactory(URL wsdlLocation) {
		final Bus b = CXFBusFactory.getThreadDefaultBus();
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
}
