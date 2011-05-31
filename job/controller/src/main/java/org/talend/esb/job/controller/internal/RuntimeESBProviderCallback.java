package org.talend.esb.job.controller.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;

class RuntimeESBProviderCallback implements ESBProviderCallback, ESBConsumer {

	private BlockingQueue<Object> requests = new LinkedBlockingQueue<Object>();

	private Object response = null;

	@Override
	public Object getRequest() throws ESBJobInterruptedException {
		try {
			System.out.println("!!! getRequest");
			Object obj = requests.take();
			System.out.println("obj="+obj.getClass().getName());
			return obj;
		} catch (InterruptedException e) {
			throw new ESBJobInterruptedException(e.getMessage(), e);
		}
	}

	@Override
	public void sendResponse(Object response) {
		System.out.println("!!! sendResponse="+response.getClass().getName());
		this.response = response;
	}

	@Override
	public Object invoke(Object payload) throws Exception {
		System.out.println("!!! invoke="+payload.getClass().getName());
		requests.put(payload);
		// TODO: 
		while(response == null) {
			Thread.sleep(100);
			System.out.println("!!! sleep");
		}
		return response;
	}
}