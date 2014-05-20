package org.talend.esb.mep.requestcallback.sample.internal;

import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;
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
public class ServiceProviderHandler implements Provider<StreamSource> {

	public interface IncomingMessageHandler {
	    void handleMessage(StreamSource request, CallContext callContext) throws Exception;
	}

	@Resource
    private WebServiceContext wsContext;

	private final BlockingQueue<Throwable> errorTransfer;
	private final BlockingQueue<String> messageTransfer;
	private final IncomingMessageHandler businessHandler;
	private final String expectedOperation;

    public ServiceProviderHandler(BlockingQueue<Throwable> errorTransfer,
    		BlockingQueue<String> messageTransfer,
    		IncomingMessageHandler businessHandler,
    		String expectedOperation) {
    	this.errorTransfer = errorTransfer;
    	this.messageTransfer = messageTransfer;
    	this.businessHandler = businessHandler;
    	this.expectedOperation = expectedOperation;
    }
    
    @Override
    public StreamSource invoke(StreamSource request) {
    	try {
	        System.out.println("Service is invoked!!!");
	        CallContext callContext = CallContext.getCallContext(wsContext);
	        assertEquals(callContext.getOperationName().getLocalPart(), expectedOperation);
	        businessHandler.handleMessage(request, callContext);
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
