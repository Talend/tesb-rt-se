/*
 * #%L
 * Even Distribution Service Locator Selection Strategy
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

import org.apache.cxf.message.Exchange;

/**
 * Performs a client side round robin strategy. In case of a fail over all
 * strategies are equivalent. A random alternative endpoint is selected. If
 * multiple clients use EvenDistributionSelectionStrategy it could happen that
 * all clients choose subsequently the same endpoints since the locator
 * instances for each client operate independently. RandomSelectionStrategy
 * avoids this problem.
 */
public class EvenDistributionSelectionStrategy extends LocatorSelectionStrategy {

	public EvenDistributionSelectionStrategy() {
		locatorCache.setStrategyId("evenDistributionSelectionStrategy");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.talend.esb.servicelocator.cxf.internal.LocatorSelectionStrategy#
	 * getPrimaryAddress(org.apache.cxf.message.Exchange)
	 */
	@Override
	public String getPrimaryAddress(Exchange exchange) {
		return locatorCache.getPrimaryAddressNext(getServiceName(exchange));
	}

}
