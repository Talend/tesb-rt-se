package org.apache.esb.sts.provider.operation;

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.esb.sts.provider.ProviderPasswordCallback;
import org.apache.esb.sts.provider.STSException;
import org.apache.esb.sts.provider.SecurityTokenServiceImpl;
import org.joda.time.DateTime;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseCollectionType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestedReferenceType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestedSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.UseKeyType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.KeyIdentifierType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityTokenReferenceType;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
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
import org.w3._2000._09.xmldsig.KeyInfoType;
import org.w3._2000._09.xmldsig.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class IssueDelegate implements IssueOperation {

	private static final Log LOG = LogFactory
			.getLog(SecurityTokenServiceImpl.class.getName());
	private static final org.oasis_open.docs.ws_sx.ws_trust._200512.ObjectFactory WS_TRUST_FACTORY = new org.oasis_open.docs.ws_sx.ws_trust._200512.ObjectFactory();
	private static final org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory WSSE_FACTORY = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();
	private static final String SAML_AUTH_CONTEXT = "ac:classes:X509";

	private boolean saml2;
	
	private ProviderPasswordCallback passwordCallback;

	private SecureRandomIdentifierGenerator generator;
	
	private static final String X_509 = "X.509";

	public void setSaml2(boolean saml2) {
		this.saml2 = saml2;
	}

	public void setPasswordCallback(ProviderPasswordCallback passwordCallback) {
		this.passwordCallback = passwordCallback;
	}

	@Override
	public RequestSecurityTokenResponseCollectionType issue(
			RequestSecurityTokenType request) {
		
		String username = passwordCallback.resetUsername();
		
		for (Object requestObject : request.getAny()) {
			try {
				X509Certificate certificate = getCertificateFromRequest(requestObject);
				if (certificate != null) {
					username = certificate.getIssuerX500Principal().getName();
				}
			} catch (CertificateException e) {
				throw new STSException("Can't extract X509 certificate from request", e);
			}
		}
		
		if(username == null) {
			throw new STSException("No credentials provided");
		}

		try {
			generator = new SecureRandomIdentifierGenerator();
		} catch (NoSuchAlgorithmException e) {
			throw new STSException("Can't initialize secure random identifier generator", e);
		}
		
		// Convert SAML to DOM
		Document assertionDocument = null;
		try {
			if(saml2) {
				Assertion samlAssertion = createSAML2Assertion(username);
				assertionDocument = toDom(samlAssertion);
			}
			else {
				org.opensaml.saml1.core.Assertion samlAssertion = createSAML1Assertion(username);
				assertionDocument = toDom(samlAssertion);
			}
		} catch (Exception e) {
			throw new STSException("Can't serialize SAML assertion", e);
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

	private RequestSecurityTokenResponseType wrapAssertionToResponse(
			Element samlAssertion) {
		RequestSecurityTokenResponseType response = WS_TRUST_FACTORY
				.createRequestSecurityTokenResponseType();

		// TokenType
		JAXBElement<String> tokenType = WS_TRUST_FACTORY.createTokenType(saml2 ? SAMLConstants.SAML20_NS : SAMLConstants.SAML1_NS);
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
		if(saml2) {
			keyIdentifierType.setValue(samlAssertion.getAttribute(Assertion.ID_ATTRIB_NAME));
		}
		else {
			keyIdentifierType.setValue(samlAssertion.getAttribute(org.opensaml.saml1.core.Assertion.ID_ATTRIB_NAME));
		}
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

	private org.opensaml.saml1.core.Assertion createSAML1Assertion(String nameId) {
		org.opensaml.saml1.core.Subject subject = createSubjectSAML1(nameId);
		return createAuthnAssertionSAML1(subject);
	}
	
	private org.opensaml.saml1.core.Subject createSubjectSAML1(String username) {
		org.opensaml.saml1.core.NameIdentifier nameID = (new org.opensaml.saml1.core.impl.NameIdentifierBuilder()).buildObject();
		nameID.setNameIdentifier(username);
		String format = "urn:oasis:names:tc:SAML:1.1:nameid-format:transient";

		if (format != null) {
			nameID.setFormat(format);
		}

		org.opensaml.saml1.core.Subject subject = (new org.opensaml.saml1.core.impl.SubjectBuilder()).buildObject();
		subject.setNameIdentifier(nameID);

		String confirmationString = "urn:oasis:names:tc:SAML:1.0:cm:bearer";

		if (confirmationString != null) {

			org.opensaml.saml1.core.ConfirmationMethod confirmationMethod = (new org.opensaml.saml1.core.impl.ConfirmationMethodBuilder()).buildObject();
	        confirmationMethod.setConfirmationMethod(confirmationString);
			
	        org.opensaml.saml1.core.SubjectConfirmation confirmation = (new org.opensaml.saml1.core.impl.SubjectConfirmationBuilder()).buildObject();
			confirmation.getConfirmationMethods().add(confirmationMethod);
			
			subject.setSubjectConfirmation(confirmation);
		}
		return subject;
	}
	
	private org.opensaml.saml1.core.Assertion createAuthnAssertionSAML1(org.opensaml.saml1.core.Subject subject) {
		org.opensaml.saml1.core.AuthenticationStatement authnStatement = (new org.opensaml.saml1.core.impl.AuthenticationStatementBuilder()).buildObject();
        authnStatement.setSubject(subject);
//        authnStatement.setAuthenticationMethod(strAuthMethod);
        
        DateTime now = new DateTime();
        
        authnStatement.setAuthenticationInstant(now);
		
        org.opensaml.saml1.core.Conditions conditions = (new org.opensaml.saml1.core.impl.ConditionsBuilder()).buildObject();
		conditions.setNotBefore(now.minusMillis(3600000));
		conditions.setNotOnOrAfter(now.plusMillis(3600000));
		
		String issuerURL = "http://www.sopera.de/SAML1";
		
		org.opensaml.saml1.core.Assertion assertion = (new org.opensaml.saml1.core.impl.AssertionBuilder()).buildObject();
		assertion.setID(generator.generateIdentifier());
		assertion.setIssuer(issuerURL);
        assertion.setIssueInstant(now);
        assertion.setVersion(SAMLVersion.VERSION_11);

        assertion.getAuthenticationStatements().add(authnStatement);
//        assertion.getAttributeStatements().add(attrStatement);
        assertion.setConditions(conditions);
		
		return assertion;
	}
	
	private X509Certificate getCertificateFromRequest(Object requestObject) throws CertificateException {
		UseKeyType useKeyType = extractType(requestObject, UseKeyType.class);
		if(null != useKeyType) {
			KeyInfoType keyInfoType = extractType(useKeyType.getAny(), KeyInfoType.class);
			if(null != keyInfoType) {
				for (Object keyInfoContent : keyInfoType.getContent()) {
					X509DataType x509DataType = extractType(keyInfoContent, X509DataType.class);
					if (null != x509DataType) {
						for (Object x509Object : x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName()) {
							byte[] x509 = extractType(x509Object, byte[].class);
							if(null != x509) {
								CertificateFactory cf = CertificateFactory.getInstance(X_509);
								Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(x509));
								X509Certificate ret = (X509Certificate) certificate;
								return ret;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T> T extractType(Object param, Class<T> clazz) {
		if(param instanceof JAXBElement) {
			JAXBElement<?> jaxbElement = (JAXBElement<?>)param;
			if (clazz == jaxbElement.getDeclaredType()) {
				return (T)jaxbElement.getValue();
			}
		}
		return null;
	}
}
