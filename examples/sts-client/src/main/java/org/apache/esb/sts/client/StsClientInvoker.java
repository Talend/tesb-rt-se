package org.apache.esb.sts.client;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.trust.STSUtils;
import org.opensaml.common.xml.SAMLConstants;
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
				
//				Element el = templateElement.getOwnerDocument().createElementNS(STSUtils.WST_NS_05_12,
//						"OnBehalfOf");
//				templateElement.appendChild(el);
//				WSSecUsernameToken token = new WSSecUsernameToken();
//				token.setUserInfo("joe", "password");
//				token.prepare(templateElement.getOwnerDocument());
//				el.appendChild(token.getUsernameTokenElement());
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Thread.sleep(500000);
	}
}
