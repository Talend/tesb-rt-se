package org.sopera.services.reservationservice.client;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.sopera.services.reservationservice.ReservationService;

public class Invoker implements InitializingBean {
	 private static final Log LOG = LogFactory.getLog(Invoker.class);
	private Integer delayBeforeSending = 5000;
	private ReservationService reservationService;
	public ReservationService getReservationService() {
		return reservationService;
	}

	public void setReservationService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public Integer getDelayBeforeSending() {
		return delayBeforeSending;
	}

	public void setDelayBeforeSending(Integer delayBeforeSending) {
		this.delayBeforeSending = delayBeforeSending;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(reservationService);
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
		LOG.info("Performing reservation invocation on ...");
		/*Implementation*/		
		LOG.info("Result reservation of runing is....");
	}
}
