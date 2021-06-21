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

/**
 * Creates a DefaultSelectionStrategy instance, which
 * Keeps the endpoint as long as there is no failover.
 * In case of a fail over all strategies are equivalent - a random alternative
 * endpoint is selected.
 */
public class DefaultSelectionStrategyFactory implements LocatorSelectionStrategyFactory {

	@Override
	public LocatorSelectionStrategy getInstance() {
		return new DefaultSelectionStrategy();
	}

}
