package org.talend.esb.mep.requestcallback.feature;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.talend.esb.mep.requestcallback.impl.ActionVerifierInterceptor;
import org.talend.esb.mep.requestcallback.impl.RequestCallbackInInterceptor;
import org.talend.esb.mep.requestcallback.impl.RequestCallbackOutInterceptor;

/**
 * 
 */

public class RequestCallbackFeature extends AbstractFeature {

    public static final String CALL_ID_NAME = "callId";

    public static final String CALLBACK_ID_NAME = "callbackId";

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
        initializeProvider(client, bus);
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
    	final ToolBox toolBox = bus.getExtension(ToolBox.class);
    	if (toolBox == null) {
    		throw new IllegalStateException(
    				"Bus extensions for Request-Callback feature are not configured. ");
    	}
        toolBox.initialize(this);
        WSAddressingFeature addressing = new WSAddressingFeature();
        addressing.setAddressingRequired(true);
        addressing.initialize(provider, bus);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing interceptors for Request-Callback feature");
        }
		final RequestCallbackInInterceptor inInterceptor = new RequestCallbackInInterceptor();
		final RequestCallbackOutInterceptor outInterceptor = new RequestCallbackOutInterceptor();
		final ActionVerifierInterceptor avInterceptor = new ActionVerifierInterceptor();
		provider.getInInterceptors().add(inInterceptor);
		provider.getOutInterceptors().add(outInterceptor);
		provider.getOutInterceptors().add(avInterceptor);
    }
}
