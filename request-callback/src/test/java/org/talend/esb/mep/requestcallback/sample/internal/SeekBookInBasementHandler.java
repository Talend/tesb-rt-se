package org.talend.esb.mep.requestcallback.sample.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;

import org.apache.cxf.helpers.IOUtils;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.sample.internal.ServiceProviderHandler.IncomingMessageHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

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

        //StreamSource response = new StreamSource(this.getClass().getResourceAsStream(responseLocation));
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(responseLocation)));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s).append("\n");
        }

        StreamSource response = new StreamSource(new StringReader(sb.toString()));

        if (context.getWsdlLocationURL() ==  null && wsdlLocation != null && wsdlLocation.length() > 0) {
        	System.err.println("Setting CallContext WSDL location attribute in message handler");
        	context.setWsdlLocation(wsdlLocation);
        }
        if (context.getWsdlLocationURL() == null) {
        	System.err.println("CallContext has no WSDL location set");
        }
        Dispatch<StreamSource> responseProxy = context.createCallbackDispatch(
        		new QName("seekBookInBasementResponse"));
        responseProxy.invokeOneWay(response);
    }
}
