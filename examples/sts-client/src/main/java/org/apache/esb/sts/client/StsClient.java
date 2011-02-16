package org.apache.esb.sts.client;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.trust.STSUtils;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.opensaml.common.xml.SAMLConstants;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StsClient implements InitializingBean {

	private STSClient stsClient;
	private boolean isUsername;
	private boolean isSaml11;

	public void setStsClient(STSClient stsClient) {
		this.stsClient = stsClient;
	}

	public void setIsUsername(boolean isUsername) {
		this.isUsername = isUsername;
	}

	public void setIsSaml11(boolean isSaml11) {
		this.isSaml11 = isSaml11;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				String tokenType = isSaml11
					? SAMLConstants.SAML1_NS
					: SAMLConstants.SAML20_NS;
				try {
					if (isUsername) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document doc = db.newDocument();
						Element templateElement = doc.createElement("template");
						doc.appendChild(templateElement);
						Element el = doc.createElementNS(STSUtils.WST_NS_05_12,
								"TokenType");
						el.appendChild(doc
								.createTextNode(tokenType));
						templateElement.appendChild(el);
						el = doc.createElementNS(STSUtils.WST_NS_05_12,
								"OnBehalfOf");
						templateElement.appendChild(el);
						WSSecUsernameToken token = new WSSecUsernameToken();
						token.setUserInfo("joe", "password");
						token.prepare(doc);
						el.appendChild(token.getUsernameTokenElement());

						stsClient.setTemplate(doc.getDocumentElement());
					} else {
						stsClient.setUseCertificateForConfirmationKeyInfo(true);
						Document doc = DOMUtils.createDocument();
						Element templateElement = doc.createElement(
								"template");
						Element el = doc.createElementNS(STSUtils.WST_NS_05_12,
								"TokenType");
						el.appendChild(doc
								.createTextNode(tokenType));
						templateElement.appendChild(el);
						Element keyTypeElement = doc.createElementNS(
								STSUtils.WST_NS_05_12, "KeyType");
						keyTypeElement.appendChild(doc
								.createTextNode("PublicKey"));
						templateElement.appendChild(keyTypeElement);
						stsClient.setTemplate(templateElement);
					}
					SecurityToken securityToken = stsClient
							.requestSecurityToken();
					System.out.println("securityToken.getId()="
							+ securityToken.getId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 1000);

	}

	public static void main(String args[]) throws Exception {
		try {
			SpringBusFactory bf = new SpringBusFactory();
			URL busFile = STSClient.class
					.getResource("/META-INF/spring/beans.xml");
			Bus bus = bf.createBus(busFile.toString());
			SpringBusFactory.setDefaultBus(bus);
			Thread.sleep(500000);
		} catch (UndeclaredThrowableException ex) {
			ex.getUndeclaredThrowable().printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
}
