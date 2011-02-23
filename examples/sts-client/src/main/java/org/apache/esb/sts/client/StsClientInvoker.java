package org.apache.esb.sts.client;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.trust.STSUtils;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StsClientInvoker extends TimerTask implements InitializingBean {

	private static final String KEY_TYPE_PUBLIC_KEY =
		"http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey";

	private STSClient stsClientUsernameToken;
	private STSClient stsClientCertificate;
	private boolean isSaml11;

	public void setStsClientUsernameToken(STSClient stsClientUsernameToken) {
		this.stsClientUsernameToken = stsClientUsernameToken;
	}

	public void setStsClientCertificate(STSClient stsClientCertificate) {
		this.stsClientCertificate = stsClientCertificate;
	}

	public void setIsSaml11(boolean isSaml11) {
		this.isSaml11 = isSaml11;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		new Timer().schedule(this, 1000);
	}
	
	@Override
	public void run() {
		STSClient stsClient;
		try {
			Element templateElement = getTemplate();
			if (stsClientUsernameToken != null) {
				stsClient = stsClientUsernameToken;
			} else {
				stsClient = stsClientCertificate;
				
				Element keyTypeElement = templateElement.getOwnerDocument().createElementNS(
						STSUtils.WST_NS_05_12, "KeyType");
				keyTypeElement.appendChild(templateElement.getOwnerDocument()
						.createTextNode(KEY_TYPE_PUBLIC_KEY));
				templateElement.appendChild(keyTypeElement);

				stsClient.setUseCertificateForConfirmationKeyInfo(true);
			}
			
			stsClient.setTemplate(templateElement);

			SecurityToken securityToken = stsClient.requestSecurityToken();
			System.out.println("securityToken.getId()="
					+ securityToken.getId());
			
			XMLObject assertion = getSAMLAssertionResponse(securityToken);
			
			System.out.println("securityToken.getTokenType()="+securityToken.getTokenType());

			if (SAMLConstants.SAML20_NS.equals(securityToken.getTokenType())) {
				System.out.println("assertion.getID() = " + ((Assertion)assertion).getID());
				System.out.println("assertion.getIssuer().getValue()" + ((Assertion)assertion).getIssuer().getValue());
			} else if (SAMLConstants.SAML1_NS.equals(securityToken.getTokenType())){
				System.out.println("assertion.getID() = " + ((org.opensaml.saml1.core.Assertion)assertion).getID());
				System.out.println("assertion.getIssuer() = " + ((org.opensaml.saml1.core.Assertion)assertion).getIssuer());
			} else {
				throw new RuntimeException("Usupported token type");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private XMLObject getSAMLAssertionResponse(SecurityToken securityToken) {
		
		Element token = securityToken.getToken();
		
		System.out.println(XMLHelper.prettyPrintXML(token));
		
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException("OpenSAML configuration failed");
		}
		
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(token);
		
		try {
			return unmarshaller.unmarshall(token);
		} catch (UnmarshallingException e) {
			e.printStackTrace();
			throw new RuntimeException("Unmarshalling of token failed");
		}
	}

	private Element getTemplate() {
		String tokenType = isSaml11
			? SAMLConstants.SAML1_NS
			: SAMLConstants.SAML20_NS;

		Document doc = DOMUtils.createDocument();
		Element templateElement = doc.createElement("template");
		doc.appendChild(templateElement);
		Element el = doc.createElementNS(STSUtils.WST_NS_05_12,
				"TokenType");
		el.appendChild(doc
				.createTextNode(tokenType));
		templateElement.appendChild(el);
	
		return templateElement;
	}

	public static void main(String args[]) throws Exception {
		SpringBusFactory bf = new SpringBusFactory();
		URL busFile = StsClientInvoker.class
				.getResource("/META-INF/spring/beans.xml");
		Bus bus = bf.createBus(busFile.toString());
		SpringBusFactory.setDefaultBus(bus);
		Thread.sleep(50000);
	}
}
