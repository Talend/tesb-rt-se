package org.apache.esb.sts.client;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.trust.STSUtils;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StsClient implements InitializingBean {

	private STSClient stsClient;
	private boolean isUsername;

	public void setStsClient(STSClient stsClient) {
		this.stsClient = stsClient;
	}
	
	public void setIsUsername(boolean isUsername) {
		this.isUsername = isUsername;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
					if(isUsername) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document doc = db.newDocument();
						Element templateElement = doc.createElement("template");
						doc.appendChild(templateElement);
						Element el = doc.createElementNS(STSUtils.WST_NS_05_12, "TokenType");
						templateElement.appendChild(el);
						el = doc.createElementNS(STSUtils.WST_NS_05_12, "OnBehalfOf");
						templateElement.appendChild(el);
						WSSecUsernameToken token = new WSSecUsernameToken();
						token.setUserInfo("joe", "password");
						token.prepare(doc);
						el.appendChild(token.getUsernameTokenElement());

						stsClient.setTemplate(doc.getDocumentElement());
					} else {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document doc = db.newDocument();
						Element templateElement = doc.createElement("template");
						doc.appendChild(templateElement);
						Element el = doc.createElementNS(STSUtils.WST_NS_05_12, "TokenType");
						templateElement.appendChild(el);
						el = doc.createElementNS(STSUtils.WST_NS_05_12, "OnBehalfOf");
						templateElement.appendChild(el);
						el = doc.createElementNS(STSUtils.WST_NS_05_12, "KeyType");
						el.appendChild(doc.createTextNode("PublicKey"));
						templateElement.appendChild(el);

						stsClient.setTemplate(doc.getDocumentElement());
				        Map<String, Object> outProps = new HashMap<String, Object>();
				        Crypto crypto = CryptoFactory.getInstance("clientKeystore.properties"); 
				        outProps.put(SecurityConstants.STS_TOKEN_CRYPTO, crypto);
						stsClient.setProperties(outProps);
						stsClient.setUseCertificateForConfirmationKeyInfo(true);
					}
					SecurityToken securityToken = stsClient.requestSecurityToken();
					System.out.println("securityToken.getId()="+securityToken.getId());
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
            URL busFile = STSClient.class.getResource("/META-INF/spring/beans.xml");
            Bus bus = bf.createBus(busFile.toString());
            SpringBusFactory.setDefaultBus(bus);
            Thread.sleep(500000);
        } catch (UndeclaredThrowableException ex) {
            ex.getUndeclaredThrowable().printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }  finally {
            System.exit(0);
        }
    }
}
