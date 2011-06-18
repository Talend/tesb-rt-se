/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal;

import java.util.concurrent.Callable;

import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;

/**
 * This {@link ESBProviderCallback} lazily initializes ESB provider
 * instance only after the {@link #getRequest()} method is first time called
 * 
 * @author zubairov
 */
public class LazyProviderCallbackDelegate implements ESBProviderCallback {

	private final Callable<ESBProviderCallback> callable;
	
	private ESBProviderCallback delegate;

	private final Runnable shutdownRunnable;
	
	/**
	 * Two parameters passed inside
	 * 
	 * @param callable will be called when {@link #getRequest()} is called to get {@link ESBProviderCallback}
	 * 	based on fully initialized {@link TalendESBJob#getEndpoint()} value
	 * @param shutdownAction will be executed after job execution is over
	 */
	public LazyProviderCallbackDelegate(Callable<ESBProviderCallback> callable, Runnable shutdownAction) {
		this.callable = callable;
		this.shutdownRunnable = shutdownAction;
	}

	@Override
	public synchronized Object getRequest() throws ESBJobInterruptedException {
		if (delegate == null) {
			try {
				delegate = callable.call();
			} catch (Exception e) {
				throw new RuntimeException("Can't get ESB Callback", e);
			}
		}
		return delegate.getRequest();
	}

	@Override
	public void sendResponse(Object response) {
		if (delegate != null) {
			delegate.sendResponse(response);
		}
	}
	
	/**
	 * Do proper shutdown actions if delegate was initialized
	 */
	public void shutdown() {
		shutdownRunnable.run();
	}

}
