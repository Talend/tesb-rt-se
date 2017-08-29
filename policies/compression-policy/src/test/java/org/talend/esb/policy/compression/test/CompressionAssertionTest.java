package org.talend.esb.policy.compression.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.compression.impl.CompressionAssertion;
import org.talend.esb.policy.compression.impl.internal.CompressionConstants;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class CompressionAssertionTest {

	private ClassPathXmlApplicationContext serviceContext;

	private ClassPathXmlApplicationContext startContext(String configFileName) {
		ClassPathXmlApplicationContext context;
		context = new ClassPathXmlApplicationContext(
				new String[] { configFileName });
		context.start();
		return context;
	}

	private ClassPathXmlApplicationContext startParticipants(String dir) {
		String configFileName = "conf/assertion-test/" + dir
				+ "/service-context.xml";
		return startContext(configFileName);
	}

	private ListOfBooks searchFor(String authorLastName, String isbn,
			Library client) throws SeekBookError {
		SearchFor request = new SearchFor();
		request.getAuthorLastName().add(authorLastName);
		request.setISBNNumber(isbn);
		return client.seekBook(request);
	}

	private int booksInResponse(ListOfBooks response) {
		return response.getBook().size();
	}

	private String authorLastName(ListOfBooks response) {
		return response.getBook().get(0).getAuthor().get(0).getLastName();
	}

	private void commonTest(String testName, String searchFor, String isbn,
			String expectedResult) {

		final String dir = testName;

		serviceContext = startParticipants(dir);

		Library client = (Library) serviceContext.getBean("libraryHttp");

		ListOfBooks response = null;

		try {
			response = searchFor(searchFor, isbn, client);
		} catch (SeekBookError e) {
			fail("Exception during service call");
		}

		assertEquals("Books amount in response differs from 1", 1,
				booksInResponse(response));
		assertEquals("Received unexpected author name", expectedResult,
				authorLastName(response));
	}

	@After
	public void closeContextAfterEachTest() {

		if (serviceContext != null) {
			serviceContext.stop();
			serviceContext.close();
			serviceContext = null;
		}
	}

	@Test
	public void testConsumerRequest() {
		commonTest("consumer-request", "Icebear", generateString(1000),
				"Icebear");
	}

	@Test
	public void testConsumerResponse() {
		commonTest("consumer-response", "Icebear", generateString(1000),
				"Icebear");
	}

	@Test
	public void testIgnorable() {
		CompressionAssertion ca = new CompressionAssertion(
				generateStubElement());
		assertFalse(ca.isIgnorable());
	}

	@Test
	public void testSerialization() throws Exception {

		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", //$NON-NLS-1$
				Boolean.valueOf(true));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(baos,
				"UTF-8");

		CompressionAssertion ca = new CompressionAssertion(
				generateStubElement());
		ca.serialize(writer);

		writer.flush();

		writer.close();

		assertTrue(baos
				.toString()
				.equalsIgnoreCase(
						"<tpa:Compression xmlns:tpa=\"http://types.talend.com/policy/assertion/1.0\" threshold=\""
								+ CompressionConstants.TRESHOLD_ATTRIBUTE_DEFAULT
								+ "\"/>"));

	}

	@Test
	public void testEqual() {

		CompressionAssertion ca = new CompressionAssertion(
				generateStubElement());

		assertFalse(ca.equal(null));

		assertTrue(ca.equal(ca));

	}

	public String generateString(int size) {
		char[] chars = new char[size];
		Arrays.fill(chars, '*');
		return new String(chars);
	}

	public Element generateStubElement() {
		return new Element() {

			@Override
			public Object setUserData(String key, Object data,
					UserDataHandler handler) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setTextContent(String textContent) throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setPrefix(String prefix) throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setNodeValue(String nodeValue) throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public Node replaceChild(Node newChild, Node oldChild)
					throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node removeChild(Node oldChild) throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void normalize() {
				// TODO Auto-generated method stub

			}

			@Override
			public String lookupPrefix(String namespaceURI) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String lookupNamespaceURI(String prefix) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isSupported(String feature, String version) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSameNode(Node other) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isEqualNode(Node arg) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDefaultNamespace(String namespaceURI) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Node insertBefore(Node newChild, Node refChild)
					throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasChildNodes() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasAttributes() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Object getUserData(String key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getTextContent() throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node getPreviousSibling() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPrefix() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node getParentNode() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Document getOwnerDocument() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getNodeValue() throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public short getNodeType() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getNodeName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node getNextSibling() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getNamespaceURI() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getLocalName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node getLastChild() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node getFirstChild() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getFeature(String feature, String version) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NodeList getChildNodes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBaseURI() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NamedNodeMap getAttributes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public short compareDocumentPosition(Node other)
					throws DOMException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Node cloneNode(boolean deep) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Node appendChild(Node newChild) throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setIdAttributeNode(Attr idAttr, boolean isId)
					throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setIdAttributeNS(String namespaceURI, String localName,
					boolean isId) throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setIdAttribute(String name, boolean isId)
					throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Attr setAttributeNode(Attr newAttr) throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setAttributeNS(String namespaceURI,
					String qualifiedName, String value) throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setAttribute(String name, String value)
					throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void removeAttributeNS(String namespaceURI, String localName)
					throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeAttribute(String name) throws DOMException {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean hasAttributeNS(String namespaceURI, String localName)
					throws DOMException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasAttribute(String name) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getTagName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TypeInfo getSchemaTypeInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NodeList getElementsByTagNameNS(String namespaceURI,
					String localName) throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NodeList getElementsByTagName(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Attr getAttributeNodeNS(String namespaceURI, String localName)
					throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Attr getAttributeNode(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getAttributeNS(String namespaceURI, String localName)
					throws DOMException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getAttribute(String name) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
