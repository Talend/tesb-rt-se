package org.talend.esb.policy.correlation.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.interceptor.BareOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.staxutils.StaxSource;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.neethi.Assertion;
import org.talend.esb.policy.correlation.impl.xpath.XpathNamespace;
import org.talend.esb.policy.correlation.impl.xpath.XpathPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
	private Message message;
	private Assertion assertion;
	
	public XPathProcessor(Assertion assertion, Message message) {
		
		super();
		this.message = message;
		this.assertion=assertion;
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
				Object o = objs.get(part);
				try {
		            if (o instanceof Source) {
		            	XMLStreamReader reader = null;
	            		if(o instanceof DataSource){
	            			DataSource s = (DataSource)o;
	            			 reader = StaxUtils.createXMLStreamReader(s.getInputStream());
	            		}else if(o instanceof StreamSource){
	            			StreamSource s = (StreamSource)o;
	            			 reader = StaxUtils.createXMLStreamReader(s.getInputStream());
	            		} else if(o instanceof StaxSource){
	            			StaxSource s = (StaxSource)o;
	            			 reader = s.getXMLStreamReader();
	            		}		            		
	            		
	            		if(reader!=null){
							// Read original Stream data to buffer
							CachedOutputStream cos = new CachedOutputStream();
							StaxUtils.copy(reader, cos);
							reader.close();
							
							StaxUtils.copy(StaxUtils.createXMLStreamReader(cos.getInputStream()), xmlWriter);
		
							// Replace original source by cached one
							StaxSource source = new StaxSource(StaxUtils.createXMLStreamReader(cos.getInputStream()));
							objs.put(part, source);
	            		}else{
	            			dataWriter.write(o, part, xmlWriter);
	            		}
		            } else {
		            	dataWriter.write(o, part, xmlWriter);
		            }
				} catch (Exception e) {
					throw new RuntimeException("Can not read part of SOAP body", e);
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
	
	public String getCorrelationID() {

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

		Map<String, String> res = processJXpathParts(parts, namespaces, body);

		return buildCorrelationIdFromXpathParts(parts, 
				cAssertion.getCorrelationName(), res);
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
		handleMessage(message);
	}
	
	private String buildCorrelationIdFromXpathParts(
			final List<XpathPart> parts, final String cName, final Map<String, String> partsValues) {

		StringBuilder builder = new StringBuilder();

		if (cName != null) {
			builder.append(cName);
			builder.append(CORRELATION_NAME_SEPARATOR);
		}

		boolean firstPart = true;
		for (XpathPart part : parts) {
			String partName = part.getName();
			String partValue = partsValues.get(part.getXpath());
			
			if(partValue!=null){
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
	
	private Map<String, String> processJXpathParts(List<XpathPart> parts, 
			List<XpathNamespace> namespaces,  Node body){
		
		Map<String, String> resultMap = new HashMap<String, String>();
	
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
				resultMap.put(part.getXpath(), val.toString());

				
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
				}

			}
		}
		
		return  resultMap;
	}
}
