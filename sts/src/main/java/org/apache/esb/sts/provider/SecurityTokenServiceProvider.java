package org.apache.esb.sts.provider;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.esb.sts.provider.operation.CancelOperation;
import org.apache.esb.sts.provider.operation.IssueOperation;
import org.apache.esb.sts.provider.operation.KeyExchangeTokenOperation;
import org.apache.esb.sts.provider.operation.RenewOperation;
import org.apache.esb.sts.provider.operation.RequestCollectionOperation;
import org.apache.esb.sts.provider.operation.ValidateOperation;

import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;
import org.w3c.dom.NodeList;

@WebServiceProvider(serviceName = "SecurityTokenServiceProvider", portName = "SecurityTokenServiceSOAP", targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/wsdl", wsdlLocation = "WEB-INF/classes/model/ws-trust-1.4-service.wsdl")
@ServiceMode(value = Service.Mode.PAYLOAD)
public class SecurityTokenServiceProvider implements Provider<DOMSource> {

	private static final Log LOG = LogFactory
			.getLog(SecurityTokenServiceProvider.class.getName());

	private static final String WSTRUST_13_NAMESPACE = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
	private static final String WSTRUST_REQUESTTYPE_ELEMENTNAME = "RequestType";
	private static final String WSTRUST_REQUESTTYPE_ISSUE = WSTRUST_13_NAMESPACE
			+ "/Issue";
	private CancelOperation cancelOperation;
	private IssueOperation issueOperation;
	private KeyExchangeTokenOperation keyExchangeTokenOperation;
	private RenewOperation renewOperation;
	private RequestCollectionOperation requestCollectionOperation;
	private ValidateOperation validateOperation;

	public void setCancelOperation(CancelOperation cancelOperation) {
		this.cancelOperation = cancelOperation;
	}

	public void setIssueOperation(IssueOperation issueOperation) {
		this.issueOperation = issueOperation;
	}

	public void setKeyExchangeTokenOperation(
			KeyExchangeTokenOperation keyExchangeTokenOperation) {
		this.keyExchangeTokenOperation = keyExchangeTokenOperation;
	}

	public void setRenewOperation(RenewOperation renewOperation) {
		this.renewOperation = renewOperation;
	}

	public void setRequestCollectionOperation(
			RequestCollectionOperation requestCollectionOperation) {
		this.requestCollectionOperation = requestCollectionOperation;
	}

	public void setValidateOperation(ValidateOperation validateOperation) {
		this.validateOperation = validateOperation;
	}

	public SecurityTokenServiceProvider() {

	}

	public DOMSource invoke(DOMSource request) {
		DOMSource response = new DOMSource();
		try {
			MessageFactory factory = MessageFactory.newInstance();
			SOAPMessage soapReq = factory.createMessage();
			soapReq.getSOAPPart().setContent(request);
			LOG.info("Incoming Client Request as a DOMSource data in MESSAGE Mode");
			NodeList nodeList = request
					.getNode()
					.getFirstChild()
					.getOwnerDocument()
					.getElementsByTagNameNS(WSTRUST_13_NAMESPACE,
							WSTRUST_REQUESTTYPE_ELEMENTNAME);
			if (nodeList == null || nodeList.getLength() == 0) {
				// TODO throw back a fault
			}
			if (nodeList.item(0).getTextContent()
					.equalsIgnoreCase(WSTRUST_REQUESTTYPE_ISSUE)) {
				LOG.info("The request type is "
						+ nodeList.item(0).getTextContent());
				RequestSecurityTokenResponseCollectionType tokenResponse = issueOperation
						.issue(convertToJAXBObject(request));
				SOAPMessage soapResponse = convertJAXBToSOAPMessage(tokenResponse);
				response.setNode(soapResponse.getSOAPPart());
			} else {
				// TODO throw a fault as right now other operations are not
				// supported
			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO
			// throw a fault
		}

		return response;
	}

	private RequestSecurityTokenType convertToJAXBObject(DOMSource source) {
		RequestSecurityTokenType request = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("org.oasis_open.docs.ws_sx.ws_trust._200512");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			// DOMSource requestSource = new DOMSource();
			// requestSource.setNode(source.getNode().getFirstChild());
			JAXBElement jaxbElement = (JAXBElement) unmarshaller
					.unmarshal(source);
			request = (RequestSecurityTokenType) jaxbElement.getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
			// TODO
		}
		return request;
	}

	private SOAPMessage convertJAXBToSOAPMessage(
			RequestSecurityTokenResponseCollectionType response) {
		SOAPMessage soapResponse = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("org.oasis_open.docs.ws_sx.ws_trust._200512");
			Marshaller marshaller = jaxbContext.createMarshaller();
			MessageFactory factory = MessageFactory.newInstance();
			soapResponse = factory.createMessage();
			// marshaller.marshal(response, soapResponse.getSOAPBody()
			// .getOwnerDocument());

			marshaller.marshal(
					new JAXBElement(new QName("uri", "local"),
							RequestSecurityTokenResponseCollectionType.class,
							response), soapResponse.getSOAPPart());
			// .getOwnerDocument()soapResponse.getSOAPBody()
			// .getOwnerDocument())

		} catch (Exception e) {
			e.printStackTrace();
			// TODO
		}
		return soapResponse;
	}

}
