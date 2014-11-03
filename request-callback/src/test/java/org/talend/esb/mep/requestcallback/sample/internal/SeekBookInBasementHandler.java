package org.talend.esb.mep.requestcallback.sample.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;

import org.apache.cxf.helpers.IOUtils;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.sample.internal.ServiceProviderHandler.IncomingMessageHandler;

public class SeekBookInBasementHandler implements IncomingMessageHandler {

	private final String responseLocation;
	private final String wsdlLocation;

	public SeekBookInBasementHandler(String responseLocation, String wsdlLocation) {
		super();
		this.responseLocation = responseLocation;
		this.wsdlLocation = wsdlLocation;
	}

	@Override
    public void handleMessage(StreamSource request, CallContext context) throws Exception {
        System.out.println("Invoked SeekBookInBasement handler");
        System.out.println(IOUtils.readStringFromStream(request.getInputStream()));
        System.out.println(String.format("Message: %s\n related with: none\n call correlation: %s\n",
                                         context.getRequestId(), context.getCallId()));
        StreamSource response = new StreamSource(this.getClass().getResourceAsStream(responseLocation));
        if (wsdlLocation != null && wsdlLocation.length() > 0) {
        	context.setWsdlLocation(wsdlLocation);
        }
        Dispatch<StreamSource> responseProxy = context.createCallbackDispatch(
        		new QName("seekBookInBasementResponse"));
        responseProxy.invokeOneWay(response);
    }
}
