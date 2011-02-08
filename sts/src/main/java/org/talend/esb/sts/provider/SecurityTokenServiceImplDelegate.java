package org.talend.esb.sts.provider;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestedSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.wsdl.SecurityTokenService;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SecurityTokenServiceImplDelegate implements SecurityTokenService {

	private static final org.oasis_open.docs.ws_sx.ws_trust._200512.ObjectFactory WS_TRUST_FACTORY = new org.oasis_open.docs.ws_sx.ws_trust._200512.ObjectFactory();
	private static final Log LOG = LogFactory
			.getLog(SecurityTokenServiceImplDelegate.class.getName());
	private static final String TOKEN_TYPE_VALUE = "urn:oasis:names:tc:SAML:2.0:assertion";
	private static final String SAML_AUTH_CONTEXT = "ac:classes:X509";

	private SecureRandomIdentifierGenerator generator;

	public RequestSecurityTokenResponseType validate(
			RequestSecurityTokenType request) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestSecurityTokenResponseCollectionType requestCollection(
			RequestSecurityTokenCollectionType requestCollection) {
		// TODO Auto-generated method stub
		return null;
	}

	public static RequestSecurityTokenResponseType wrapAssertionToResponse(
			Element samlAssertion) {
		RequestSecurityTokenResponseType response = WS_TRUST_FACTORY
				.createRequestSecurityTokenResponseType();
		PasswordCallbackImpl pc = new PasswordCallbackImpl();		
		JAXBElement<String> tokenType = WS_TRUST_FACTORY
				.createTokenType(TOKEN_TYPE_VALUE);

		
		RequestedSecurityTokenType requestedTokenType = WS_TRUST_FACTORY
				.createRequestedSecurityTokenType();
		requestedTokenType.setAny(samlAssertion);
		JAXBElement<RequestedSecurityTokenType> requestedToken = WS_TRUST_FACTORY
				.createRequestedSecurityToken(requestedTokenType);
		response.getAny().add(tokenType);
		response.getAny().add(requestedToken);
		return response;
	}

	public Assertion createSAML2Assertion(String nameId) {
		Subject subject = createSubject(nameId);
		return createAuthnAssertion(subject);
	}

	private Subject createSubject(String username) {
		NameID nameID = (new NameIDBuilder()).buildObject();
		nameID.setValue(username);
		String format = "urn:oasis:names:tc:SAML:1.1:nameid-format:transient";
		if (format != null) {
			nameID.setFormat(format);
		}

		Subject subject = (new SubjectBuilder()).buildObject();
		subject.setNameID(nameID);

		String confirmationMethod = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
		if (confirmationMethod != null) {
			SubjectConfirmation confirmation = (new SubjectConfirmationBuilder())
					.buildObject();
			confirmation.setMethod(confirmationMethod);
			subject.getSubjectConfirmations().add(confirmation);
		}
		return subject;
	}

	private Assertion createAuthnAssertion(Subject subject) {
		Assertion assertion = createAssertion(subject);

		AuthnContextClassRef ref = (new AuthnContextClassRefBuilder())
				.buildObject();
		String authnCtx = SAML_AUTH_CONTEXT;
		if (authnCtx != null) {
			ref.setAuthnContextClassRef(authnCtx);
		}
		AuthnContext authnContext = (new AuthnContextBuilder()).buildObject();
		authnContext.setAuthnContextClassRef(ref);

		AuthnStatement authnStatement = (new AuthnStatementBuilder())
				.buildObject();
		authnStatement.setAuthnInstant(new DateTime());
		authnStatement.setAuthnContext(authnContext);

		assertion.getStatements().add(authnStatement);

		return assertion;
	}

	private Assertion createAssertion(Subject subject) {
		Assertion assertion = (new AssertionBuilder()).buildObject();
		assertion.setID(generator.generateIdentifier());

		DateTime now = new DateTime();
		assertion.setIssueInstant(now);

		String issuerURL = "http://www.sopera.de/SAML2";
		if (issuerURL != null) {
			Issuer issuer = (new IssuerBuilder()).buildObject();
			issuer.setValue(issuerURL);
			assertion.setIssuer(issuer);
		}

		assertion.setSubject(subject);

		Conditions conditions = (new ConditionsBuilder()).buildObject();
		conditions.setNotBefore(now.minusMillis(3600000));
		conditions.setNotOnOrAfter(now.plusMillis(3600000));
		assertion.setConditions(conditions);
		return assertion;
	}

	public RequestSecurityTokenResponseType keyExchangeToken(
			RequestSecurityTokenType request) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestSecurityTokenResponseCollectionType issue(
			RequestSecurityTokenType request) {
		// TODO Auto-generated method stub
		
		
		try {
			generator = new SecureRandomIdentifierGenerator();
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		String userName = "test";
		Assertion samlAssertion = createSAML2Assertion(userName);
		
		List<Object> requestParams = request.getAny();
		String showName = "";
		for (Object param : requestParams) {
				Element jaxbParam = (Element) param;
				showName = (String) jaxbParam.getTextContent();
		}

		String ADOC = "<User>"+GlobalUser.getUserName()+":"+GlobalUser.getUserPassword()+"</User>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document d = null;
		try {
			builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(ADOC));
			d = builder.parse(is);
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		samlAssertion.setDOM(d.getDocumentElement());
		try {
			xmlToString(samlAssertion.getDOM());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error(e);
			e.printStackTrace();
		}
		RequestSecurityTokenResponseType response = wrapAssertionToResponse(samlAssertion
				.getDOM());

		RequestSecurityTokenResponseCollectionType responseCollection = WS_TRUST_FACTORY
				.createRequestSecurityTokenResponseCollectionType();
		responseCollection.getRequestSecurityTokenResponse().add(response);
		LOG.info("Finished operation requestSecurityToken");
		return responseCollection;
	}

	public static String xmlToString(Node node) throws Exception {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			//throw new Exception(stringWriter.getBuffer().toString());
			 return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public RequestSecurityTokenResponseType cancel(
			RequestSecurityTokenType request) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestSecurityTokenResponseType renew(
			RequestSecurityTokenType request) {
		// TODO Auto-generated method stub
		return null;
	}

}
