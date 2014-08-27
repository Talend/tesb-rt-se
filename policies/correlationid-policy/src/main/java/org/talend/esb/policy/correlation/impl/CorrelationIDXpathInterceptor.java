package org.talend.esb.policy.correlation.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutEndingInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

class CorrelationIDXpathInterceptor extends
		AbstractPhaseInterceptor<Message> {

	public static final String CORRELATION_NAME_SEPARATOR = "#";//"&";
	public static final String CORRELATION_PART_SEPARATOR = ";";//";";
	public static final String CORRELATION_PART_NAME_VALUE_SEPARATOR = "=";
   	public static String TEMP_CORRELATION_ID = "org.talend.esb.temp.correlation.id";
   	public static String CORRELATION_ID_XPATH_ASSERTION = "org.talend.esb.correlation-id.xpath.assertion";
   	public static String ORIGINAL_OUT_STREAM_CTX_PROPERTY_NAME = 
			"org.talend.correlation.id.original.out.stream"; 

	public CorrelationIDXpathInterceptor() {
		super(Phase.PREPARE_SEND);
	}

	OutputStream originalOutputStream;

	ByteArrayOutputStream captureStream;

	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			captureOutStream(message);
		} catch (SAXException e) {
			throw new Fault(e);
		} catch (IOException e) {
			throw new Fault(e);
		} catch (ParserConfigurationException e) {
			throw new Fault(e);
		}
		// Add a final interceptor to write end elements
		message.getInterceptorChain().add(
				new CorrelationIDXpathEndingInterceptor(originalOutputStream, captureStream));
	}

	protected void captureOutStream(Message message) throws SAXException,
			IOException, ParserConfigurationException {
		originalOutputStream = message.getContent(OutputStream.class);

		captureStream = new ByteArrayOutputStream();
		message.setContent(OutputStream.class, captureStream);

		message.setContextualProperty(
				ORIGINAL_OUT_STREAM_CTX_PROPERTY_NAME,
				originalOutputStream);
	}

	static class CorrelationIDXpathEndingInterceptor extends
			AbstractPhaseInterceptor<Message> {

		OutputStream originalOuputStream;
		ByteArrayOutputStream captureStream;
		
		public CorrelationIDXpathEndingInterceptor(final OutputStream orignalStream, 
				final ByteArrayOutputStream captureStream){
			super(Phase.PRE_STREAM_ENDING);
			addAfter(StaxOutEndingInterceptor.class.getName());
			this.originalOuputStream = orignalStream;
			this.captureStream = captureStream;
		}
		
		public CorrelationIDXpathEndingInterceptor() {
			super(Phase.PRE_STREAM_ENDING);
			// add after StaxOutEndingInterceptor before
			// AttachmentEndingInterceptor
			addAfter(StaxOutEndingInterceptor.class.getName());
		}

		@Override
		public void handleMessage(Message message) throws Fault {
			try {
				restoreOriginalStream(message);
			} catch (SAXException e) {
				throw new Fault(e);
			} catch (IOException e) {
				throw new Fault(e);
			} catch (ParserConfigurationException e) {
				throw new Fault(e);
			}
		}


		protected void restoreOriginalStream(Message message) throws SAXException,
				IOException, ParserConfigurationException {
			
			message.setContent(OutputStream.class, originalOuputStream);
			
			CorrelationIDAssertion cAssertion = getCorrelationIdXPathAssertion(message);
			if(cAssertion!=null){
				String correlationId = buildCorrelationID(cAssertion);
				fillOriginalStream(correlationId, true);
			}else{
				fillOriginalStream(null, false);
			}			
		}

		protected void fillOriginalStream(String correlationId, boolean replaceCorrelationID)
				throws UnsupportedEncodingException, IOException {

			if(replaceCorrelationID){
				ByteArrayInputStream bis = new ByteArrayInputStream(
						captureStream.toByteArray());
				
				ReplacingStream rs = new ReplacingStream(bis,
						TEMP_CORRELATION_ID
								.getBytes("UTF-8"), correlationId.getBytes("UTF-8"));
				int b;
				while (-1 != (b = rs.read()))
					originalOuputStream.write(b);
				rs.close();
			}else{
				captureStream.writeTo(originalOuputStream);
			}
			captureStream.close();
		}

		protected String buildCorrelationID(CorrelationIDAssertion cAssertion) {
			
			if(cAssertion==null){
				throw new RuntimeException(
						"Can not find correlation assertion");
			}

			List<CorrelationIDPart> parts = cAssertion.getCorrelationParts();
			
			if(parts==null || parts.isEmpty()) return null;

			Document body = getSoapBodyChilds();

			if (body == null) {
				throw new RuntimeException(
						"SoapBody elements are not found in soap message");
			}

			for (CorrelationIDPart part : parts) {

				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();
				XPathExpression expr = null;
				try {
					expr = xpath.compile(part.getXpath());
				} catch (XPathExpressionException ex) {
					throw new RuntimeException("Creation of XPATH expression"
							+ "{ name: " + part.getName() + "; xpath: "
							+ part.getXpath() + " } failed", ex);
				}

				try {
					String result = (String) expr.evaluate(body);
					part.setValue(result);
					if((result==null || result.isEmpty()) && !part.isOptional()){
						throw new RuntimeException(
								"Value for not optional Xpath expression is not found " + "{ name: "
										+ part.getName() + "; xpath: "
										+ part.getXpath() + " }");						
					}
				} catch (XPathExpressionException ex) {
					if (!part.isOptional()) {
						throw new RuntimeException(
								"Evaluation of XPATH expression" + "{ name: "
										+ part.getName() + "; xpath: "
										+ part.getXpath() + " } failed", ex);
					}

				}
			}

			return buildCorrelationIdFromXpathParts(parts,
					cAssertion.getCorrelationName());
		}

		protected Document getSoapBodyChilds() {

			Document soap = null;
			try {
				
//				soap = DOMUtils.readXml(new ByteArrayInputStream(captureStream.toByteArray()));
				
				DocumentBuilderFactory builderFactory =
				        DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;				
				builder = builderFactory.newDocumentBuilder();
				soap = builder.parse(
			            new ByteArrayInputStream(captureStream.toByteArray()));
			} catch (Exception e) {
				throw new RuntimeException("Can not read SOAP message: " + e); 
			}

			Document soapBody = DOMUtils.createDocument();
			NodeList n = soap.getDocumentElement()
					.getLastChild().getChildNodes();
			
			for (int cnt = 0; cnt< n.getLength(); cnt++){
				Node soapBodyElementNode = soapBody.importNode(n.item(cnt), true);
				soapBody.appendChild(soapBodyElementNode);
			}
			
			return soapBody;
		}

		protected CorrelationIDAssertion getCorrelationIdXPathAssertion(
				final Message message) {

			Object assertion = message
					.getContextualProperty(CORRELATION_ID_XPATH_ASSERTION);
			if (assertion instanceof CorrelationIDAssertion) {
				return (CorrelationIDAssertion) assertion;
			}
			return null;
		}

		protected String buildCorrelationIdFromXpathParts(
				final List<CorrelationIDPart> parts, final String cName) {

			StringBuilder builder = new StringBuilder();

			if (cName != null) {
				builder.append(cName);
				builder.append(CORRELATION_NAME_SEPARATOR);
			}

			int partsCntr = 0;
			for (CorrelationIDPart part : parts) {
				String partName = part.getName();
				String partValue = part.getValue();
				if (partName != null) {
					builder.append(partName);
					builder.append(CORRELATION_PART_NAME_VALUE_SEPARATOR);
				}
				builder.append(partValue);
				if(++partsCntr != parts.size()){
					//Do not add part separator for last part
					builder.append(CORRELATION_PART_SEPARATOR);
				}
			}

			return builder.toString();
		}
	}

}
