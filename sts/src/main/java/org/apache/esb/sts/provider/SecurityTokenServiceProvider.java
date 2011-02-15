
package org.apache.esb.sts.provider;

import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

@WebServiceProvider(serviceName="SecurityTokenServiceProvider",
		portName="SecurityTokenServiceSOAP",
		targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/wsdl",
        wsdlLocation = "WEB-INF/classes/model/ws-trust-1.4-service.wsdl")

@ServiceMode(value=Service.Mode.PAYLOAD)


public class  SecurityTokenServiceProvider implements Provider<DOMSource> {
	
	public SecurityTokenServiceProvider(){
	
	}
	
	public DOMSource invoke(DOMSource request){
	    DOMSource response = new DOMSource();
	    return response;
	}
	
}
