package org.talend.esb.mep.requestcallback.sample.internal;

import java.util.concurrent.BlockingQueue;

import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.sample.internal.ClientProviderHandler.IncomingMessageHandler;

public class SeekBookInBasementFaultCallback implements IncomingMessageHandler {

	private final BlockingQueue<String> messageTransfer;

	public SeekBookInBasementFaultCallback(BlockingQueue<String> messageTransfer) {
		super();
		this.messageTransfer = messageTransfer;
	}

    @Override
    public void handleMessage(StreamSource request, CallContext context) throws Exception {
        System.out.println("Invoked SeekBookInBasementFault callback");
        String msg = IOUtils.readStringFromStream(request.getInputStream());
        System.out.println(msg);
        System.out.println(String.format("Message: %s\n related with: none\n call correlation: %s\n",
                                         context.getRequestId(), context.getCallId()));
        messageTransfer.add(msg);
    }
}
