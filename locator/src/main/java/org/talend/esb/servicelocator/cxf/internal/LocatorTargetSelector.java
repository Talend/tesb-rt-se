/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.clustering.FailoverTargetSelector;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;

public class LocatorTargetSelector extends FailoverTargetSelector {

	private static final Logger LOG = Logger
			.getLogger(LocatorTargetSelector.class.getPackage().getName());

	private static final String LOCATOR_PROTOCOL = "locator://";

	private static final String LOCATOR_SETADDRESS_FLAG = "setAddress.called";

	private boolean locatorProtocol;

	private LocatorSelectionStrategy strategy = new DefaultSelectionStrategy();

	@Override
	public void complete(Exchange exchange) {
		// This overridden method is workaround for TESB-12457
		// we should have IOException in exchange header to make failover
		// feature works as expected
		// in case of TOS generated client route we get it only in message
		// context
		Message outMessage = exchange.getOutMessage();
		if (outMessage != null) {
			Exception ex = outMessage.getContent(Exception.class);
			if (ex != null && exchange.get(Exception.class) == null)
				exchange.put(Exception.class, ex);
		}
		super.complete(exchange);
	}

	@Override
	public synchronized Conduit selectConduit(Message message) {
		setAddress(message);
		return super.selectConduit(message);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.cxf.clustering.FailoverTargetSelector#prepare(org.apache.cxf
	 * .message.Message)
	 */
	@Override
	public synchronized void prepare(Message message) {
		setAddress(message);
		super.prepare(message);
	}

	protected void setAddress(Message message) {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "setAddress attempt, " + " locatorProtocol = "
					+ locatorProtocol + " adress = "
					+ endpoint.getEndpointInfo().getAddress() + " ");
			LOG.log(Level.INFO, "Using strategy "
					+ strategy.getClass().getName() + ".");
		}

		if (message.getExchange().get(LOCATOR_SETADDRESS_FLAG) != Boolean.TRUE) {
			EndpointInfo ei = endpoint.getEndpointInfo();
			if (locatorProtocol || ei.getAddress().startsWith(LOCATOR_PROTOCOL)) {
				// bug in CXF - https://issues.apache.org/jira/browse/CXF-5225
				// uncomment this in case backport to version used CXF without
				// the fix
				// if (message.getExchange().getEndpoint() == null) {
				// message.getExchange().put(Endpoint.class, endpoint);
				// }
				if (LOG.isLoggable(Level.INFO)) {
					LOG.log(Level.INFO,
							"Found address with locator protocol, mapping it to physical address.");
					LOG.log(Level.INFO, "Using strategy "
							+ strategy.getClass().getName() + ".");
				}

				String physAddress = strategy.getPrimaryAddress(message
						.getExchange());

				if (physAddress != null) {
					ei.setAddress(physAddress);
					locatorProtocol = true;
					message.put(Message.ENDPOINT_ADDRESS, physAddress);
					if (LOG.isLoggable(Level.INFO)) {
						LOG.log(Level.INFO, "Physical adress " + physAddress);
					}

				} else {
					if (LOG.isLoggable(Level.SEVERE)) {
						LOG.log(Level.SEVERE,
								"Failed to map logical locator address to physical address.");
					}
					throw new IllegalStateException(
							"No endpoint found in Service Locator for service "
									+ endpoint.getService().getName());
				}

			}
			message.getExchange().put(LOCATOR_SETADDRESS_FLAG, Boolean.TRUE);
		} else {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE,
						"Setting address was skipped. It was already applied for this message");
			}
		}
	}

	public void setLocatorSelectionStrategy(
			LocatorSelectionStrategy locatorSelectionStrategy) {
		strategy = locatorSelectionStrategy;
		setStrategy(locatorSelectionStrategy);
	}

}
