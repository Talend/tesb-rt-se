package org.talend.esb.mep.requestcallback.sample;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.jms.JMSTransportFactory;
import org.apache.cxf.wsdl11.WSDLServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.talend.esb.mep.requestcallback.beans.JmsUriConfigurator;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.sample.internal.ClientProviderHandler;
import org.talend.esb.mep.requestcallback.sample.internal.ClientProviderHandler.IncomingMessageHandler;
import org.talend.esb.mep.requestcallback.sample.internal.SeekBookInBasementFaultCallback;
import org.talend.esb.mep.requestcallback.sample.internal.SeekBookInBasementHandler;
import org.talend.esb.mep.requestcallback.sample.internal.SeekBookInBasementResponseCallback;
import org.talend.esb.mep.requestcallback.sample.internal.ServiceProviderHandler;

import static org.junit.Assert.*;

@Ignore
@RunWith(Parameterized.class)
public class RequestCallbackJmsTest {

	private static final int NO_RUN = 0;
	// private static final int REQUEST_RESPONSE = 1;
	// private static final int ONE_WAY = 2;
	private static final int REQUEST_CALLBACK = 3;
	private static final int REQUEST_CALLBACK_ENFORCED = 4;

	private static final String SERVICE_NAMESPACE =
			"http://services.talend.org/demos/Library/1.0";
	private static final String SERVICE_NAMESPACE_A =
			"http://services.talend.org/demos/Library/1.1";
    private static final QName SERVICE_NAME =
    		new QName(SERVICE_NAMESPACE, "LibraryProvider"); 
    private static final QName SERVICE_NAME_A =
    		new QName(SERVICE_NAMESPACE_A, "LibraryProvider"); 
    private static final QName PORT_NAME =
    		new QName(SERVICE_NAMESPACE, "Library_jmsPort");
    private static final QName PORT_NAME_A =
    		new QName(SERVICE_NAMESPACE_A, "Library_jmsPort");
    private static boolean hasActiveMQ = probeActiveMQ();

    private final QName serviceName;
    private final QName portName;
	private final String wsdlLocation;
	private final String requestLocation;
	private final String responseLocation;
	private final String operation;
	private final int mep;
	private final BlockingQueue<String> messageTransfer =
			new ArrayBlockingQueue<String>(8);
	private final BlockingQueue<Throwable> errorTransfer =
			new ArrayBlockingQueue<Throwable>(8);
	private final Map<String, IncomingMessageHandler> callbackMap =
			new ConcurrentHashMap<String, IncomingMessageHandler>();

	private EndpointInfo endpointInfo = null;
	private Server server = null;
	private Endpoint callbackEndpoint = null;

	public RequestCallbackJmsTest(
			String wsdlLocation,
			String requestLocation,
			String responseLocation,
			String operation, int mep,
			boolean alternateName) {
		super();
		this.wsdlLocation = wsdlLocation;
		this.requestLocation = requestLocation;
		this.responseLocation = responseLocation;
		this.operation = operation;
		this.mep = hasActiveMQ ? mep : NO_RUN;
		if (alternateName) {
			serviceName = SERVICE_NAME_A;
			portName = PORT_NAME_A;
		} else {
			serviceName = SERVICE_NAME;
			portName = PORT_NAME;
		}
	}

	@Parameterized.Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays.asList(new Object[][] {
			{   "/LibraryJmsA.wsdl",
				"/request-library-rc.xml",
				"/response-library-rc.xml",
				"seekBookInBasement",
				REQUEST_CALLBACK_ENFORCED,
				false   },
			{   "/LibraryJmsB.wsdl",
				"/request-library-rc.xml",
				"/response-library-rc.xml",
				"seekBookInBasement",
				REQUEST_CALLBACK,
				true   },
			{   "/LibraryJmsC.wsdl",
				"/request-library-rc-C.xml",
				"/response-library-rc-C.xml",
				"seekBookInBasement",
				REQUEST_CALLBACK,
				false   },
			{ "", "", "", "", NO_RUN, false }
		});
	}

	@Before
	public void initialize() throws Exception {
		if (mep == NO_RUN) {
			return;
		}

    	final Bus bus = BusFactory.getDefaultBus();
    	final JMSTransportFactory jmsTransport = new JMSTransportFactory();
    	final DestinationFactoryManager dfm =
    			bus.getExtension(DestinationFactoryManager.class);
    	dfm.registerDestinationFactory(
    			"http://schemas.xmlsoap.org/soap/http", jmsTransport);
    	dfm.registerDestinationFactory(
    			"http://schemas.xmlsoap.org/soap/jms", jmsTransport);
    	dfm.registerDestinationFactory(
    			"http://schemas.xmlsoap.org/wsdl/soap/http", jmsTransport);
    	dfm.registerDestinationFactory(
    			"http://cxf.apache.org/bindings/xformat", jmsTransport);
    	 
    	final ConduitInitiatorManager extension =
    			bus.getExtension(ConduitInitiatorManager.class);
    	extension.registerConduitInitiator(
    			"http://schemas.xmlsoap.org/wsdl/soap/http", jmsTransport);
    	extension.registerConduitInitiator(
    			"http://schemas.xmlsoap.org/soap/http", jmsTransport);
    	extension.registerConduitInitiator(
    			"http://schemas.xmlsoap.org/soap/jms", jmsTransport);
    	extension.registerConduitInitiator(
    			"http://cxf.apache.org/bindings/xformat", jmsTransport);

		final WSDLServiceFactory wsdlSvcFactory = new WSDLServiceFactory
        		(CXFBusFactory.getThreadDefaultBus(), wsdlLocation);
		final org.apache.cxf.service.Service cxfService = wsdlSvcFactory.create();
		final ServiceInfo si = findServiceByName(cxfService, serviceName);
		if (si == null) {
			throw new RuntimeException("WSDL does not contain service " + serviceName);
		}
		final EndpointInfo ei = findEndpoint(si, portName);
		if (ei == null) {
			throw new RuntimeException("WSDL does not contain port " + portName);
		}
		endpointInfo = ei;

		callbackMap.put("seekBookInBasementResponse",
				new SeekBookInBasementResponseCallback(messageTransfer));
		callbackMap.put("seekBookInBasementFault",
				new SeekBookInBasementFaultCallback(messageTransfer));
	}

	@Test(timeout = 300000L)
	public void testRequestCallback() throws Exception {
		if (mep == NO_RUN) {
			if (!hasActiveMQ) {
				System.err.println("ActiveMQ is not available");
			}
			return;
		}
		subTestServerStartup();
		subTestServiceCall();
	}

	@After
	public void done() throws Exception {
		if (mep == NO_RUN) {
			return;
		}
		if (callbackEndpoint != null) {
			callbackEndpoint.stop();
		}
		if (server != null) {
			server.stop();
			server.destroy();
		}
		checkError(true);
	}

	private void subTestServerStartup() throws Exception {
        final SeekBookInBasementHandler businessHandler =
        		new SeekBookInBasementHandler(responseLocation, "classpath:" + wsdlLocation);
        final ServiceProviderHandler implementor =
        		new ServiceProviderHandler(
        				errorTransfer, messageTransfer, businessHandler, operation);

        final JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();

        factory.setServiceName(serviceName);
        factory.setEndpointName(portName);
        factory.setWsdlLocation(wsdlLocation);
        factory.setServiceBean(implementor);

        CallContext.setupServerFactory(factory);
        JmsUriConfigurator configurator = JmsUriConfigurator.create(factory);
        factory.setAddress(configurator.getJmsAddress());
        server = factory.create();
        sleep(1);
        checkError(false);
	}

	private void subTestServiceCall() throws Exception {
		// server.start();
        final Service service = Service.create(
        		getClass().getResource(wsdlLocation), serviceName);
        service.addPort(portName, endpointInfo.getBinding().getBindingId(),
        		endpointInfo.getAddress());

        // 1. Register a local callback endpoint on the client side
        final ClientProviderHandler callbackHandler = new ClientProviderHandler(
        		errorTransfer, messageTransfer, callbackMap);
        final Endpoint ep = CallContext.createCallbackEndpoint(
        		callbackHandler, wsdlLocation);
        callbackEndpoint = ep;
        JmsUriConfigurator cConfigurator = JmsUriConfigurator.create(ep);
        ep.publish(cConfigurator.getJmsAddress());

        // 2. Create a client
        final Dispatch<StreamSource> dispatcher = service.createDispatch(
        		portName, StreamSource.class, Service.Mode.PAYLOAD);
        CallContext.setupDispatch(dispatcher, ep);
        JmsUriConfigurator configurator = JmsUriConfigurator.create(dispatcher);
        configurator.configureDispatch(dispatcher);
        if (mep == REQUEST_CALLBACK_ENFORCED) {
        	final QName opName = new QName(serviceName.getNamespaceURI(), operation);
        	CallContext.enforceOperation(opName, dispatcher);
        }

        // 3. Invoke the service operation
        final StreamSource request = new StreamSource(
        		getClass().getResourceAsStream(requestLocation));
        dispatcher.invokeOneWay(request);
        assertTrue(messageTransfer.take() != null);
        sleep(1);
        checkError(false);
	}

	private void checkError(boolean clear) throws Exception {
		try {
			final Throwable t = errorTransfer.poll();
			if (t != null) {
				throw t;
			}
		} catch (Exception e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException("Unknown Throwable. ", e);
		} finally {
			if (clear) {
				errorTransfer.clear();
			}
		}
	}

	private static void sleep(int seconds) {
		try {
			Thread.sleep(1000L * (long) seconds);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private static ServiceInfo findServiceByName(
			org.apache.cxf.service.Service cxfService, QName serviceName) {
		for (ServiceInfo si : cxfService.getServiceInfos()) {
			if (si.getName().equals(serviceName)) {
				return si;
			}
		}
		return null;
	}

	private static EndpointInfo findEndpoint(ServiceInfo si, QName name) {
		for (EndpointInfo ei : si.getEndpoints()) {
			if (name.equals(ei.getName())) {
				return ei;
			}
		}
		return null;
	}

	private static boolean probeActiveMQ() {
		try {
			Socket s = new Socket("localhost", 61616);
			s.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
