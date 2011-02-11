package org.apache.esb.sts.client;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.springframework.beans.factory.InitializingBean;

public class Invoker implements InitializingBean {

	private STSClient stsClient;

	public void setStsClient(STSClient stsClient) {
		this.stsClient = stsClient;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
			        Map<String, Object> outProps = new HashMap<String, Object>();
			        // Manual WSS4J interceptor process
			        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
			        outProps.put(WSHandlerConstants.USER, "joe");
			        outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
			        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
			                ClientPasswordCallback.class.getName());

			        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
			        stsClient.getOutInterceptors().add(wssOut);
			        
					SecurityToken securityToken = stsClient.requestSecurityToken();
					System.out.println("securityToken="+securityToken);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 5000);

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
