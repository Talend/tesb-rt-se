package org.talend.esb.policy.correlation.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutEndingInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.policy.correlation.impl.xpath.XpathNamespace;
import org.talend.esb.policy.correlation.impl.xpath.XpathPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class CorrelationIDXpathInterceptor extends
		AbstractPhaseInterceptor<Message> {

	public static final String CORRELATION_NAME_SEPARATOR = "#";
	public static final String CORRELATION_PART_SEPARATOR = ";";
	public static final String CORRELATION_PART_NAME_VALUE_SEPARATOR = "=";
   	public static String TEMP_CORRELATION_ID = "org.talend.esb.temp.correlation.id";
   	public static String CORRELATION_ID_XPATH_ASSERTION = "org.talend.esb.correlation-id.xpath.assertion";
   	public static String ORIGINAL_OUT_STREAM_CTX_PROPERTY_NAME = 
			"org.talend.correlation.id.original.out.stream"; 

	public CorrelationIDXpathInterceptor() {
		super(Phase.PREPARE_SEND);
	}
	
	public CorrelationIDXpathInterceptor(String phase, CorrelationIDAssertion policy) {
		super(phase);
		this.policy = policy;
	}	
	
	OutputStream originalOutputStream;

	ByteArrayOutputStream captureStream;
	
	CorrelationIDAssertion policy = null;

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
				new CorrelationIDXpathEndingInterceptor(originalOutputStream, captureStream, policy));
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
		CorrelationIDAssertion policy = null;
		
		public CorrelationIDXpathEndingInterceptor(final OutputStream orignalStream, 
				final ByteArrayOutputStream captureStream, CorrelationIDAssertion policy){
			super(Phase.PRE_STREAM_ENDING);
			addAfter(StaxOutEndingInterceptor.class.getName());
			this.originalOuputStream = orignalStream;
			this.captureStream = captureStream;
			this.policy = policy;
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
			
			OutputStream wrapper = message.getContent(OutputStream.class);
			message.setContent(OutputStream.class, originalOuputStream);
			
			CorrelationIDAssertion cAssertion = getCorrelationIdXPathAssertion(message);
			
			
			if(cAssertion!=null){
				String correlationId = buildCorrelationID(cAssertion);
				boolean rewriteCorrelationId = cAssertion.getMethodType().equals(CorrelationIDAssertion.MethodType.XPATH);
				if(rewriteCorrelationId){
					message.put(CorrelationIDFeature.MESSAGE_CORRELATION_ID, correlationId);
				}
				fillOriginalStream(correlationId, rewriteCorrelationId, wrapper);
			}else{
				fillOriginalStream(null, false, wrapper);
			}			
		}

		protected void fillOriginalStream(String correlationId, boolean replaceCorrelationID, OutputStream wrapper)
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
			wrapper.flush();
			wrapper.close();
		}
		
		
		static class XpathNamespaceContext implements NamespaceContext {

			
			private Map<String, String> namespacesMap  = new HashMap<String, String>();
			
			//private List<CorrelationIDNamespace> namespaces = new ArrayList<CorrelationIDNamespace>();
			
			List<String> prefixes = new ArrayList<String>();
			
			public XpathNamespaceContext(List<XpathNamespace> namespaces){
				
				if(namespaces!=null){
					for (XpathNamespace namespace : namespaces) {
						String prefix = namespace.getPrefix();
						String uri = namespace.getUri();
						if(prefix!=null && namespace!=null){
							namespacesMap.put(prefix, uri);
						}
					}
				}
			}
			
			@Override
			public String getNamespaceURI(String prefix) {
				return namespacesMap.get(prefix);
			}

			@Override
			public String getPrefix(String namespaceURI) {

				if(namespaceURI==null){
					return null;
				}
				
				for (Map.Entry<String, String> entry : namespacesMap.entrySet())
				{
					String uri = entry.getValue();
					String prefix = entry.getKey();
					
					if(namespaceURI.compareToIgnoreCase(uri)==0){
						return prefix;
					}
				}
				return null;
			}

			@Override
			public Iterator<String> getPrefixes(String namespaceURI) {
				return namespacesMap.keySet().iterator();
			}
			
		}
		
		
		protected void processXpathParts(List<XpathPart> parts, 
				List<XpathNamespace> namespaces,  Document body){
			
			XPathFactory xPathfactory = XPathFactory.newInstance();

			XPath xpath = xPathfactory.newXPath();
			xpath.setNamespaceContext(new XpathNamespaceContext(namespaces));
			
			for (XpathPart part : parts) {

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
					}else{
						part.setIgnore(true);
					}
				}
			}
		}
		
		protected void processJXpathParts(List<XpathPart> parts, 
				List<XpathNamespace> namespaces,  Document body){
			
		
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

		protected String buildCorrelationID(CorrelationIDAssertion cAssertion) {
			
			if(cAssertion==null){
				throw new RuntimeException(
						"Can not find correlation assertion");
			}

			List<XpathPart> parts = cAssertion.getCorrelationParts();
			
			if(parts==null || parts.isEmpty()) return null;
			
			List<XpathNamespace> namespaces = cAssertion.getCorrelationNamespaces();

			Document body = getSoapBodyChilds();

			if (body == null) {
				throw new RuntimeException(
						"SoapBody elements are not found in soap message");
			}

			processJXpathParts(parts, namespaces, body);
	
			return buildCorrelationIdFromXpathParts(parts,
					cAssertion.getCorrelationName());
		}

		protected Document getSoapBodyChilds() {

			Document soap = null;
			try {
				
//				soap = DOMUtils.readXml(new ByteArrayInputStream(captureStream.toByteArray()));
				
				DocumentBuilderFactory builderFactory =
				        DocumentBuilderFactory.newInstance();
				
				builderFactory.setNamespaceAware(true);
				DocumentBuilder builder = builderFactory.newDocumentBuilder();				
				 
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
			
			if(this.policy!=null){
				return policy;
			}

			Object assertion = message
					.getContextualProperty(CORRELATION_ID_XPATH_ASSERTION);
			if (assertion instanceof CorrelationIDAssertion) {
				return (CorrelationIDAssertion) assertion;
			}
			return null;
		}

		protected String buildCorrelationIdFromXpathParts(
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
	}
	
	public static class ReplacingStream extends FilterInputStream {
		
		LinkedList<Integer> inQueue = new LinkedList<Integer>();
	    LinkedList<Integer> outQueue = new LinkedList<Integer>();
	    final byte[] search, replacement;

	    public ReplacingStream(InputStream in, byte[] search,
	                                                   byte[] replacement) {
	        super(in);
	        this.search = search;
	        this.replacement = replacement;
	    }

	    private boolean isMatchFound() {
	        Iterator<Integer> inIter = inQueue.iterator();
	        for (int i = 0; i < search.length; i++)
	            if (!inIter.hasNext() || search[i] != inIter.next())
	                return false;
	        return true;
	    }

	    private void readAhead() throws IOException {
	        while (inQueue.size() < search.length) {
	            int next = super.read();
	            inQueue.offer(next);
	            if (next == -1)
	                break;
	        }
	    }

	    @Override
	    public int read() throws IOException {
	        if (outQueue.isEmpty()) {
	            readAhead();
	            if (isMatchFound()) {
	                for (int i = 0; i < search.length; i++)
	                    inQueue.remove();

	                for (byte b : replacement)
	                    outQueue.offer((int) b);
	            } else
	                outQueue.add(inQueue.remove());
	        }
	        return outQueue.remove();
	    }
	}
}
