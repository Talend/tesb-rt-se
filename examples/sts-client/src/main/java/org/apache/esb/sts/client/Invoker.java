package org.apache.esb.sts.client;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
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
					SecurityToken securityToken = stsClient.requestSecurityToken();
					System.out.println("securityToken="+securityToken);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, 5000);

	}

}
