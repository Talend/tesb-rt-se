
package org.apache.esb.sts.provider;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;

@WebServiceProvider(serviceName="SecurityTokenServiceProvider",
		portName="SecurityTokenServiceSOAP",
		targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/wsdl",
        wsdlLocation = "WEB-INF/classes/model/ws-trust-1.4-service.wsdl")

@ServiceMode(value=Service.Mode.PAYLOAD)


public class  SecurityTokenServiceProvider implements Provider<DOMSource> {
	
	private static final Log LOG = LogFactory
	.getLog(SecurityTokenServiceProvider.class.getName());
			
	public SecurityTokenServiceProvider(){
	
	}
	
	public DOMSource invoke(DOMSource request){
	    DOMSource response = new DOMSource();
	    try { 
            MessageFactory factory = MessageFactory.newInstance(); 
            SOAPMessage soapReq = factory.createMessage(); 
            soapReq.getSOAPPart().setContent(request); 
            LOG.info("Incoming Client Request as a DOMSource data in MESSAGE Mode"); 
            soapReq.writeTo(System.out); 
            System.out.println("\n");
	    }catch(Exception e){
	    	//TODO
	    }
	    
	    return response;
	}
	
	
	private RequestSecurityTokenType convertToJAXBObject(DOMSource source){
		RequestSecurityTokenType request = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			 request = (RequestSecurityTokenType)unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			//TODO
		}
		return request;
	}
	
}
