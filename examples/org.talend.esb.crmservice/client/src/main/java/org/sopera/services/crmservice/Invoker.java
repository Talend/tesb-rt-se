package org.sopera.services.crmservice;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.sopera.services.crm.types.CustomerDetailsType;
import org.sopera.services.crmservice.CRMService;

public class Invoker implements InitializingBean {
	 private static final Log LOG = LogFactory.getLog(Invoker.class);
	private Integer delayBeforeSending = 5000;
	private CRMService cRMService;
	private CustomerDetailsType customer;
	
	public CRMService getCRMService() {
		return cRMService;
	}

	public void setCRMService(CRMService cRMService) {
		this.cRMService = cRMService;
	}

	public Integer getDelayBeforeSending() {
		return delayBeforeSending;
	}

	public void setDelayBeforeSending(Integer delayBeforeSending) {
		this.delayBeforeSending = delayBeforeSending;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(cRMService);
		 Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                try {
	                    performRequest();
	                } catch (Exception ex) {
	                    throw new RuntimeException(ex);
	                }
	            }


	        }, delayBeforeSending);
	}
	private void performRequest() {
		LOG.info("Performing invocation on ...");
		/*Implementation*/		
		LOG.info("Result of runing is....");
	}
}

