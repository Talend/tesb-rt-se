package org.apache.esb.sts.provider.token;

import java.security.NoSuchAlgorithmException;

import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.common.xml.SAMLConstants;
import org.w3c.dom.Element;

public class Saml1TokenProvider implements TokenProvider {

	@Override
	public String getTokenType() {
		return SAMLConstants.SAML1_NS;
	}

	@Override
	public Element createToken(String username) {
		org.opensaml.saml1.core.Subject subject = createSubjectSAML1(username);
		org.opensaml.saml1.core.Assertion samlAssertion = createAuthnAssertionSAML1(subject);
		try {
			return SamlUtils.toDom(samlAssertion).getDocumentElement();
		} catch (Exception e) {
			throw new TokenException("Can't serialize SAML assertion", e);
		}
	}

	@Override
	public String getTokenId(Element token) {
		return token.getAttribute(org.opensaml.saml1.core.Assertion.ID_ATTRIB_NAME);
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
		try {
			SecureRandomIdentifierGenerator generator = new SecureRandomIdentifierGenerator();
			assertion.setID(generator.generateIdentifier());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		assertion.setIssuer(issuerURL);
        assertion.setIssueInstant(now);
        assertion.setVersion(SAMLVersion.VERSION_11);

        assertion.getAuthenticationStatements().add(authnStatement);
//        assertion.getAttributeStatements().add(attrStatement);
        assertion.setConditions(conditions);

		return assertion;
	}

}
