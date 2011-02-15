package org.apache.esb.sts.provider.operation;

import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.esb.sts.provider.SecurityTokenServiceImpl;
import org.joda.time.DateTime;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestedReferenceType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestedSecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.KeyIdentifierType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityTokenReferenceType;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.common.xml.SAMLConstants;
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
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IssueDelegate implements IssueOperation {

	private static final Log LOG = LogFactory
			.getLog(SecurityTokenServiceImpl.class.getName());
	private static final org.oasis_open.docs.ws_sx.ws_trust._200512.ObjectFactory WS_TRUST_FACTORY = new org.oasis_open.docs.ws_sx.ws_trust._200512.ObjectFactory();
	private static final org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory WSSE_FACTORY = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();
	private static final String SAML_AUTH_CONTEXT = "ac:classes:X509";

	private SecureRandomIdentifierGenerator generator;
	
	@Override
	public RequestSecurityTokenResponseCollectionType issue(
			RequestSecurityTokenType request) {
		
		for (Object requestObject : request.getAny()) {
			System.out.println("requestObject="+requestObject.getClass().getName());
			if(requestObject instanceof JAXBElement) {
				JAXBElement<?> jaxbElement = (JAXBElement<?>)requestObject;
				System.out.println("jaxbElement.getName().getLocalPart()="+jaxbElement.getName().getLocalPart());
				System.out.println("jaxbElement.getDeclaredType()="+jaxbElement.getDeclaredType());
				System.out.println("jaxbElement.getValue()="+jaxbElement.getValue());
			} else if (requestObject instanceof Element) {
				Element element = (Element)requestObject;
				System.out.println("Element="+element.getNodeName());
			}
		}
		try {
			generator = new SecureRandomIdentifierGenerator();
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		Assertion samlAssertion = createSAML2Assertion("dummy");

		// Convert SAML to DOM
		Document assertionDocument = null;
		try {
			assertionDocument = toDom(samlAssertion);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RequestSecurityTokenResponseType response = wrapAssertionToResponse(
				/*samlAssertion.getDOM()*/
				assertionDocument.getDocumentElement());

		RequestSecurityTokenResponseCollectionType responseCollection = WS_TRUST_FACTORY
				.createRequestSecurityTokenResponseCollectionType();
		responseCollection.getRequestSecurityTokenResponse().add(response);
		LOG.info("Finished operation requestSecurityToken");
		return responseCollection;
	}

	private static Document toDom(XMLObject object) throws MarshallingException, ParserConfigurationException, ConfigurationException {
		Document document = getDocumentBuilder().newDocument();

		DefaultBootstrap.bootstrap();

		Marshaller out = Configuration.getMarshallerFactory().getMarshaller(
				object);
		out.marshall(object, document);
		return document;
	}

	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		return factory.newDocumentBuilder();
	}

	private static RequestSecurityTokenResponseType wrapAssertionToResponse(
			Element samlAssertion) {
		RequestSecurityTokenResponseType response = WS_TRUST_FACTORY
				.createRequestSecurityTokenResponseType();

		// TokenType
		JAXBElement<String> tokenType = WS_TRUST_FACTORY
			.createTokenType(SAMLConstants.SAML20_NS);
		response.getAny().add(tokenType);

		// RequestedSecurityToken
		RequestedSecurityTokenType requestedTokenType = WS_TRUST_FACTORY
			.createRequestedSecurityTokenType();
		JAXBElement<RequestedSecurityTokenType> requestedToken = WS_TRUST_FACTORY
				.createRequestedSecurityToken(requestedTokenType);
		requestedTokenType.setAny(samlAssertion);
		response.getAny().add(requestedToken);
		
		// RequestedAttachedReference
		RequestedReferenceType requestedReferenceType = WS_TRUST_FACTORY
			.createRequestedReferenceType();
		SecurityTokenReferenceType securityTokenReferenceType = WSSE_FACTORY.
			createSecurityTokenReferenceType();
		KeyIdentifierType keyIdentifierType = WSSE_FACTORY
			.createKeyIdentifierType();
		keyIdentifierType.setValue(samlAssertion.getAttribute(Assertion.ID_ATTRIB_NAME));
		JAXBElement<KeyIdentifierType> keyIdentifier = WSSE_FACTORY
			.createKeyIdentifier(keyIdentifierType);
		securityTokenReferenceType.getAny().add(keyIdentifier);
		requestedReferenceType.setSecurityTokenReference(securityTokenReferenceType);

		JAXBElement<RequestedReferenceType> requestedAttachedReference = WS_TRUST_FACTORY
			.createRequestedAttachedReference(requestedReferenceType);
		response.getAny().add(requestedAttachedReference);

		return response;
	}

	private Assertion createSAML2Assertion(String nameId) {
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

}
