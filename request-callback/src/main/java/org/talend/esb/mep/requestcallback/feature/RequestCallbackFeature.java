package org.talend.esb.mep.requestcallback.feature;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.wsdl.JAXBExtensionHelper;
import org.apache.cxf.wsdl.WSDLManager;
import org.talend.esb.mep.requestcallback.impl.CallbackActionInterceptor;
import org.talend.esb.mep.requestcallback.impl.RequestCallbackInInterceptor;
import org.talend.esb.mep.requestcallback.impl.RequestCallbackOutInterceptor;

/**
 *
 */

public class RequestCallbackFeature extends AbstractFeature {

    public static final String REQUEST_CALLBACK_CONFIGURATION_OSGI_PROPERTY =
            "org.talend.esb.mep.requestcallback";

    public static final String REQUEST_CALLBACK_CONFIGURATION_SYSTEM_PROPERTY =
            "org.talend.esb.mep.requestcallback.configuration";

    public static final String REQUEST_CALLBACK_CONFIGURATION_RESOURCE =
            "org.talend.esb.mep.requestcallback.Configuration.properties";

    public static final String CALL_ID_NAME = "callId";

    public static final String CALLBACK_ID_NAME = "callbackId";

    public static final String CORRELATION_ID_NAME = "correlationId";

    public static final QName CORRELATION_ID_HEADER_NAME = new QName(
            "http://www.talend.com/esb/sam/correlationId/v1", CORRELATION_ID_NAME);

    public static final QName CALL_ID_HEADER_NAME = new QName(
            "http://www.talend.com/esb/requestcallback", CALL_ID_NAME);

    public static final QName CALLBACK_ID_HEADER_NAME = new QName(
            "http://www.talend.com/esb/requestcallback", CALLBACK_ID_NAME);

    public static final String CALLCONTEXT_PROPERTY_NAME =
            "org.talend.esb.mep.requestcallback.CallContext";

    public static final String CALLBACK_ENDPOINT_PROPERTY_NAME =
            "org.talend.esb.mep.requestcallback.CallbackEndpoint";

    public static final String CALL_INFO_PROPERTY_NAME =
            "org.talend.esb.mep.requestcallback.CallInfo";

    /** The class logger. */
    private static final Logger LOG = Logger.getLogger(RequestCallbackFeature.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Client client, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing Request-Callback feature for bus " + bus +
                    " and client " + client);
        }

        applyWsdlExtensions(bus);
        initializeProvider(client, bus);
    }

    /**
     * Adds JAXB WSDL extensions to allow work with custom
     * WSDL elements such as \"partner-link\"
     *
     * @param bus CXF bus
     */
    public static void applyWsdlExtensions(Bus bus) {

        ExtensionRegistry registry = bus.getExtension(WSDLManager.class).getExtensionRegistry();

        try {

            JAXBExtensionHelper.addExtensions(bus, registry,
                    javax.wsdl.Definition.class,
                    org.talend.esb.mep.requestcallback.impl.wsdl.PLType.class);

            JAXBExtensionHelper.addExtensions(bus, registry,
                    javax.wsdl.Binding.class,
                    org.talend.esb.mep.requestcallback.impl.wsdl.CallbackExtension.class);

        } catch (JAXBException e) {
            throw new RuntimeException("Failed to add WSDL JAXB extensions", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing Request-Callback feature for bus " + bus +
                    " and server " + server);
        }
        initializeProvider(server.getEndpoint(), bus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Resolving bus extensions for Request-Callback feature");
        }
        WSAddressingFeature addressing = new WSAddressingFeature();
        addressing.setAddressingRequired(true);
        addressing.initialize(provider, bus);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing interceptors for Request-Callback feature");
        }
        final RequestCallbackInInterceptor inInterceptor = new RequestCallbackInInterceptor();
        final CallbackActionInterceptor cbInterceptor = new CallbackActionInterceptor();
        final RequestCallbackOutInterceptor outInterceptor = new RequestCallbackOutInterceptor();
        provider.getInInterceptors().add(inInterceptor);
        provider.getInInterceptors().add(cbInterceptor);
        provider.getOutInterceptors().add(outInterceptor);
    }
}
