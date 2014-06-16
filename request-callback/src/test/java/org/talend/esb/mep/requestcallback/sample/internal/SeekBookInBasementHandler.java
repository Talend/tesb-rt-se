package org.talend.esb.mep.requestcallback.sample.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;

import org.apache.cxf.helpers.IOUtils;
import org.talend.esb.mep.requestcallback.beans.JmsConfigurator;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.sample.internal.ServiceProviderHandler.IncomingMessageHandler;

public class SeekBookInBasementHandler implements IncomingMessageHandler {

	private final String responseLocation;

	public SeekBookInBasementHandler(String responseLocation) {
		super();
		this.responseLocation = responseLocation;
	}

	@Override
    public void handleMessage(StreamSource request, CallContext context) throws Exception {
        System.out.println("Invoked SeekBookInBasement handler");
        System.out.println(IOUtils.readStringFromStream(request.getInputStream()));
        System.out.println(String.format("Message: %s\n related with: none\n call correlation: %s\n",
                                         context.getRequestId(), context.getCallId()));
        StreamSource response = new StreamSource(this.getClass().getResourceAsStream(responseLocation));
        Dispatch<StreamSource> responseProxy = context.createCallbackDispatch(
        		new QName("seekBookInBasementResponse"));
        if (context.getReplyToAddress().startsWith("jms")) {
            JmsConfigurator cConfigurator = JmsConfigurator.create(responseProxy);
            cConfigurator.configureDispatch(responseProxy, context.getReplyToAddress());
        }
        responseProxy.invokeOneWay(response);
    }
}
