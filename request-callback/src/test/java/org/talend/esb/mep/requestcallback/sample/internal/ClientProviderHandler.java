package org.talend.esb.mep.requestcallback.sample.internal;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;

import org.talend.esb.mep.requestcallback.feature.CallContext;

import static org.junit.Assert.*;

@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class ClientProviderHandler implements Provider<StreamSource> {

	public interface IncomingMessageHandler {
	    void handleMessage(StreamSource request, CallContext callContext) throws Exception;
	}

	@Resource
    private WebServiceContext wsContext;

	private final BlockingQueue<Throwable> errorTransfer;
	private final BlockingQueue<String> messageTransfer;
	private final Map<String, IncomingMessageHandler> callbackMap;

    public ClientProviderHandler(BlockingQueue<Throwable> errorTransfer,
    		BlockingQueue<String> messageTransfer,
    		Map<String, IncomingMessageHandler> callbackMap) {
    	this.errorTransfer = errorTransfer;
    	this.messageTransfer = messageTransfer;
        this.callbackMap = callbackMap;
    }
    
    @Override
    public StreamSource invoke(StreamSource request) {
    	try {
	    	CallContext context = CallContext.getCallContext(wsContext);
	    	assertNotNull("CallContext missing", context);
	    	QName opName = context.getOperationName();
	    	assertNotNull("Response action missing", opName);
	        IncomingMessageHandler businessHandler = callbackMap.get(opName.getLocalPart());
	        if (businessHandler == null) {
	            throw new RuntimeException("Unknown callback operation: " + context.getOperationName().getLocalPart());
	        }
	        businessHandler.handleMessage(request, context);
    	} catch (Exception e) {
    		addError(e);
    	} catch (AssertionError e) {
    		addError(e);
    	}
        return null;
    }

    public void addError(Throwable error) {
    	errorTransfer.offer(error);
    	messageTransfer.offer("ERROR");
    }
}
