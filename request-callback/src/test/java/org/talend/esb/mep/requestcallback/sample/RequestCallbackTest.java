package org.talend.esb.mep.requestcallback.sample;

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
import org.apache.cxf.transport.local.LocalTransportFactory;
import org.apache.cxf.wsdl11.WSDLServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.sample.internal.ClientProviderHandler;
import org.talend.esb.mep.requestcallback.sample.internal.ClientProviderHandler.IncomingMessageHandler;
import org.talend.esb.mep.requestcallback.sample.internal.SeekBookInBasementFaultCallback;
import org.talend.esb.mep.requestcallback.sample.internal.SeekBookInBasementHandler;
import org.talend.esb.mep.requestcallback.sample.internal.SeekBookInBasementResponseCallback;
import org.talend.esb.mep.requestcallback.sample.internal.ServiceProviderHandler;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RequestCallbackTest {

    private static final int NO_RUN = 0;
    // private static final int REQUEST_RESPONSE = 1;
    // private static final int ONE_WAY = 2;
    private static final int REQUEST_CALLBACK = 3;
    private static final int REQUEST_CALLBACK_ENFORCED = 4;

    private static final String SERVICE_NAMESPACE =
            "http://services.talend.org/demos/Library/1.0";
    private static final QName SERVICE_NAME =
            new QName(SERVICE_NAMESPACE, "LibraryProvider"); 
    private static final QName PORT_NAME =
            new QName(SERVICE_NAMESPACE, "Library_WS-I");
    private static final String CLIENT_CALLBACK_ENDPOINT =
            "local://LibraryConsumerEndpoint";

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

    public RequestCallbackTest(
            String wsdlLocation,
            String requestLocation,
            String responseLocation,
            String operation, int mep) {
        super();
        this.wsdlLocation = wsdlLocation;
        this.requestLocation = requestLocation;
        this.responseLocation = responseLocation;
        this.operation = operation;
        this.mep = mep;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
            {   "/LibraryA.wsdl",
                "/request-library-rc.xml",
                "/response-library-rc.xml",
                "seekBookInBasement",
                REQUEST_CALLBACK_ENFORCED   },
            {   "/LibraryB.wsdl",
                "/request-library-rc.xml",
                "/response-library-rc.xml",
                "seekBookInBasement",
                REQUEST_CALLBACK   },
            {   "/LibraryC.wsdl",
                "/request-library-rc-C.xml",
                "/response-library-rc-C.xml",
                "seekBookInBasement",
                REQUEST_CALLBACK   },
            { "", "", "", "", NO_RUN }
        });
    }

    @Before
    public void initialize() throws Exception {
        if (mep == NO_RUN) {
            return;
        }
        final Bus bus = BusFactory.getDefaultBus();
        final LocalTransportFactory localTransport = new LocalTransportFactory();
        final DestinationFactoryManager dfm =
                bus.getExtension(DestinationFactoryManager.class);
        dfm.registerDestinationFactory(
                "http://schemas.xmlsoap.org/soap/http", localTransport);
        dfm.registerDestinationFactory(
                "http://schemas.xmlsoap.org/soap/jms", localTransport);
        dfm.registerDestinationFactory(
                "http://schemas.xmlsoap.org/wsdl/soap/http", localTransport);
        dfm.registerDestinationFactory(
                "http://cxf.apache.org/bindings/xformat", localTransport);
        dfm.registerDestinationFactory(
                "http://cxf.apache.org/transports/local", localTransport);

        final ConduitInitiatorManager extension =
                bus.getExtension(ConduitInitiatorManager.class);
        extension.registerConduitInitiator(
                "http://cxf.apache.org/transports/local", localTransport);
        extension.registerConduitInitiator(
                "http://schemas.xmlsoap.org/wsdl/soap/http", localTransport);
        extension.registerConduitInitiator(
                "http://schemas.xmlsoap.org/soap/http", localTransport);
        extension.registerConduitInitiator(
                "http://schemas.xmlsoap.org/soap/jms", localTransport);
        extension.registerConduitInitiator(
                "http://cxf.apache.org/bindings/xformat", localTransport);

                final WSDLServiceFactory wsdlSvcFactory = new WSDLServiceFactory
                (CXFBusFactory.getThreadDefaultBus(), wsdlLocation, SERVICE_NAME);
        final org.apache.cxf.service.Service cxfService = wsdlSvcFactory.create();
        final ServiceInfo si = findServiceByName(cxfService, SERVICE_NAME);
        if (si == null) {
            throw new RuntimeException("WSDL does not contain service " + SERVICE_NAME);
        }
        final EndpointInfo ei = findEndpoint(si, PORT_NAME);
        if (ei == null) {
            throw new RuntimeException("WSDL does not contain port " + PORT_NAME);
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

        factory.setServiceName(SERVICE_NAME);
        factory.setEndpointName(PORT_NAME);
        factory.setWsdlLocation(wsdlLocation);
        factory.setServiceBean(implementor);

        CallContext.setupServerFactory(factory);
        server = factory.create();
        sleep(1);
        checkError(false);
    }

    private void subTestServiceCall() throws Exception {
        // server.start();
        final Service service = Service.create(
                getClass().getResource(wsdlLocation), SERVICE_NAME);
        service.addPort(PORT_NAME, endpointInfo.getBinding().getBindingId(),
                endpointInfo.getAddress());

        // 1. Register a local callback endpoint on the client side
        final ClientProviderHandler callbackHandler = new ClientProviderHandler(
                errorTransfer, messageTransfer, callbackMap);
        final Endpoint ep = CallContext.createCallbackEndpoint(
                callbackHandler, wsdlLocation);
        callbackEndpoint = ep;
        ep.publish(CLIENT_CALLBACK_ENDPOINT);

        // 2. Create a client
        final Dispatch<StreamSource> dispatcher = service.createDispatch(
                PORT_NAME, StreamSource.class, Service.Mode.PAYLOAD);
        CallContext.setupDispatch(dispatcher, ep);
        if (mep == REQUEST_CALLBACK_ENFORCED) {
            final QName opName = new QName(SERVICE_NAMESPACE, operation);
            CallContext.enforceOperation(opName, dispatcher);
        }

        // 3. Invoke the service operation
        final StreamSource request = new StreamSource(
                getClass().getResourceAsStream(requestLocation));
        dispatcher.invokeOneWay(request);
        assertTrue(messageTransfer.take() != null);
        sleep(3);
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
}
