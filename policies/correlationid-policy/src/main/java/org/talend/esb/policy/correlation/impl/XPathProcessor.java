package org.talend.esb.policy.correlation.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.interceptor.BareOutInterceptor;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.staxutils.CachingXmlEventWriter;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.neethi.Assertion;
import org.talend.esb.policy.correlation.impl.xpath.XpathNamespace;
import org.talend.esb.policy.correlation.impl.xpath.XpathPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.rits.cloning.Cloner;

public class XPathProcessor extends BareOutInterceptor {

	public static final String CORRELATION_NAME_SEPARATOR = "#";
	public static final String CORRELATION_PART_SEPARATOR = ";";
	public static final String CORRELATION_PART_NAME_VALUE_SEPARATOR = "=";
   	public static String TEMP_CORRELATION_ID = "org.talend.esb.temp.correlation.id";
   	public static String CORRELATION_ID_XPATH_ASSERTION = "org.talend.esb.correlation-id.xpath.assertion";
   	public static String ORIGINAL_OUT_STREAM_CTX_PROPERTY_NAME = 
			"org.talend.correlation.id.original.out.stream"; 

	private ByteArrayOutputStream buffer;
	private XMLStreamWriter xmlWriter;

	public XPathProcessor(Message message) {
		super();
		buffer  = new ByteArrayOutputStream();
		xmlWriter = StaxUtils.createXMLStreamWriter(buffer,
				getEncoding(message));
	}
	
	@Override
	protected void writeParts(Message message, Exchange exchange,
			BindingOperationInfo operation, MessageContentsList objs,
			List<MessagePartInfo> parts) {
		Service service = exchange.getService();
		
		DataWriter<XMLStreamWriter> dataWriter = getDataWriter(message,
				service, XMLStreamWriter.class);

		for (MessagePartInfo part : parts) {
			if (objs.hasValue(part)) {
				NamespaceContext c = null;
				if (!part.isElement()
						&& xmlWriter instanceof CachingXmlEventWriter) {
					try {
						c = xmlWriter.getNamespaceContext();
						xmlWriter
								.setNamespaceContext(new CachingXmlEventWriter.NSContext(
										null));
					} catch (XMLStreamException e) {
					}
				}
				Object o = objs.get(part);
				dataWriter.write(o, part, xmlWriter);
				if (c != null) {
					try {
						xmlWriter.setNamespaceContext(c);
					} catch (XMLStreamException e) {
						// ignore
					}
				}
			}
		}

		try {
			xmlWriter.flush();
		} catch (Exception e) {
		}
	}

	@Override
	protected <T> DataWriter<T> getDataWriter(Message message, Service service,
			Class<T> output) {
		DataWriter<T> writer = service.getDataBinding().createWriter(output);
		writer.setProperty(DataWriter.ENDPOINT, message.getExchange()
				.getEndpoint());
		writer.setProperty(Message.class.getName(), message);
		return writer;
	}

	private String getEncoding(Message message) {
		Exchange ex = message.getExchange();
		String encoding = (String) message.get(Message.ENCODING);
		if (encoding == null && ex.getInMessage() != null) {
			encoding = (String) ex.getInMessage().get(Message.ENCODING);
			message.put(Message.ENCODING, encoding);
		}

		if (encoding == null) {
			encoding = "UTF-8";
			message.put(Message.ENCODING, encoding);
		}
		return encoding;
	}
	
	public String getCorrelationID(Assertion assertion, Message message) {

		CorrelationIDAssertion cAssertion = null;
		if(!(assertion instanceof CorrelationIDAssertion)){
			throw new RuntimeException(
					"Can not find correlation assertion");
		}
		
		cAssertion = (CorrelationIDAssertion)assertion;
		
		Node body = getSoapBody(message);
		
		if (body == null) {
			throw new RuntimeException(
					"SoapBody elements are not found in soap message");
		}
		
		List<XpathPart> parts = cAssertion.getCorrelationParts();
		
		if(parts==null || parts.isEmpty()) return null;
		
		List<XpathNamespace> namespaces = cAssertion.getCorrelationNamespaces();

		processJXpathParts(parts, namespaces, body);

		return buildCorrelationIdFromXpathParts(parts,
				cAssertion.getCorrelationName());
	}
	
	private Node getSoapBody(Message message) {
		if(!MessageUtils.isOutbound(message)){
			//processing of incoming message
			try{
				if(message.getContent(SOAPMessage.class) != null){
					SOAPMessage soap = (SOAPMessage)message.getContent(SOAPMessage.class);
					return soap.getSOAPBody();
				}else{
					throw new RuntimeException("Can not find SOAP message in context");
				}
			}catch(Exception ex){
				throw new RuntimeException("Can not read SOAP body: " + ex);
			}
		}else{
			// processing of outgoing message
			// try to build SoapBody
			loadSoapBodyToBuffer(message);

			try {
				
				DocumentBuilderFactory builderFactory =
				        DocumentBuilderFactory.newInstance();
				
				builderFactory.setNamespaceAware(true);
				DocumentBuilder builder = builderFactory.newDocumentBuilder();				
				 
				Document doc = builder.parse(
			            new ByteArrayInputStream(buffer.toByteArray()));
				
				return (Node)doc;
				
			} catch (Exception e) {
				throw new RuntimeException("Can not read SOAP body: " + e); 
			}
		}
	}

	private void loadSoapBodyToBuffer(Message message){
		Cloner cloner = new Cloner();
		MessageContentsList original = MessageContentsList.getContentsList(message);
		MessageContentsList clone = cloner.deepClone(original);
		message.setContent(List.class, clone);
		handleMessage(message);
		message.setContent(List.class, original);
	}
	
	private String buildCorrelationIdFromXpathParts(
			final List<XpathPart> parts, final String cName) {

		StringBuilder builder = new StringBuilder();

		if (cName != null) {
			builder.append(cName);
			builder.append(CORRELATION_NAME_SEPARATOR);
		}

		boolean firstPart = true;
		for (XpathPart part : parts) {
			String partName = part.getName();
			String partValue = part.getValue();
			
			if(!part.isIgnore()){
				if(!firstPart){
					//Do not add part separator for first part
					builder.append(CORRELATION_PART_SEPARATOR);
				}else{
					firstPart = false;
				}
				
				if (partName != null) {
					builder.append(partName);
					builder.append(CORRELATION_PART_NAME_VALUE_SEPARATOR);
				}
				
				builder.append(partValue);
			}
		}

		return builder.toString();
	}
	
	private void processJXpathParts(List<XpathPart> parts, 
			List<XpathNamespace> namespaces,  Node body){
		
	
		JXPathContext messageContext = JXPathContext.newContext(body);
		
		if(namespaces!=null){
			for (XpathNamespace namespace : namespaces) {
				String prefix = namespace.getPrefix();
				String uri = namespace.getUri();
				if(null != uri && null != prefix){
					messageContext.registerNamespace(prefix, uri);
				}
				
			}	
		}

		for (XpathPart part : parts) {
			
			try {
				JXPathContext.compile(part.getXpath());
			} catch (JXPathException ex) {
				throw new RuntimeException("Validation of XPATH expression"
						+ "{ name: " + part.getName() + "; xpath: "
						+ part.getXpath() + " } failed", ex);
			}

			try {
				Object val = messageContext.getValue(part.getXpath());
				String result = (val==null)?null:val.toString();
				part.setValue(val.toString());
				
				if((result==null || result.isEmpty()) && !part.isOptional()){
					throw new RuntimeException(
							"Can not evaluate Xpath expression" + "{ name: "
									+ part.getName() + "; xpath: "
									+ part.getXpath() + " }");						
				}
			} catch (RuntimeException ex) {
				if (!part.isOptional()) {
					throw new RuntimeException(
							"Evaluation of XPATH expression" + "{ name: "
									+ part.getName() + "; xpath: "
									+ part.getXpath() + " } failed", ex);
				}else{
					part.setIgnore(true);
				}

			}
		}
	}
}


