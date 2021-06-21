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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.cxf.endpoint.ConduitSelectorHolder;
import org.springframework.beans.factory.annotation.Value;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;

@Named
@Singleton
public class LocatorClientEnabler {

    private static final Logger LOG = Logger.getLogger(LocatorClientEnabler.class.getPackage().getName());

    private static final String DEFAULT_STRATEGY = "defaultSelectionStrategy";

    @Inject
    private ServiceLocator locatorClient;

    @Inject
    private LocatorSelectionStrategyMap locatorSelectionStrategyMap;

    private String defaultLocatorSelectionStrategy;

    public void setServiceLocator(ServiceLocator serviceLocator) {
        locatorClient = serviceLocator;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client " + serviceLocator + " was set for LocatorClientRegistrar.");
        }

    }

    /**
     * Set all supported Selection Strategies
     * @param locatorSelectionStrategyMap
     */
    public void setLocatorSelectionStrategyMap(LocatorSelectionStrategyMap locatorSelectionStrategyMap) {
        this.locatorSelectionStrategyMap = locatorSelectionStrategyMap;
    }

    /**
     * If the String argument locatorSelectionStrategy is as key in the map representing the locatorSelectionStrategies, the
     * corresponding strategy is selected, else it remains unchanged.
     * @param locatorSelectionStrategy
     */
    private LocatorSelectionStrategy getLocatorSelectionStrategy(String locatorSelectionStrategy) {
        if (null == locatorSelectionStrategy) {
            return locatorSelectionStrategyMap.get(DEFAULT_STRATEGY).getInstance();
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Strategy " + locatorSelectionStrategy
                    + " was set for LocatorClientRegistrar.");
        }

        if (locatorSelectionStrategyMap.containsKey(locatorSelectionStrategy)) {
            return locatorSelectionStrategyMap.get(locatorSelectionStrategy).getInstance();
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "LocatorSelectionStrategy " + locatorSelectionStrategy
                        + " not registered at LocatorClientEnabler.");
            }
            return locatorSelectionStrategyMap.get(DEFAULT_STRATEGY).getInstance();
        }
    }

    /**
     * If the String argument defaultLocatorSelectionStrategy is as key in the map representing the locatorSelectionStrategies, the
     * corresponding strategy is selected and set as default strategy, else both the selected strategy and the default strategy remain
     * unchanged.
     * @param defaultLocatorSelectionStrategy
     */
    @Value("${locator.strategy}")
    public void setDefaultLocatorSelectionStrategy(String defaultLocatorSelectionStrategy) {
        this.defaultLocatorSelectionStrategy = defaultLocatorSelectionStrategy;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Default strategy " + defaultLocatorSelectionStrategy
                    + " was set for LocatorClientRegistrar.");
        }
    }

    public void enable(ConduitSelectorHolder conduitSelectorHolder) {
        enable(conduitSelectorHolder, null);
    }

    public void enable(ConduitSelectorHolder conduitSelectorHolder, SLPropertiesMatcher matcher) {
        enable(conduitSelectorHolder, matcher, null);
    }

    /**
     * The selectionStrategy given as String argument is selected as locatorSelectionStrategy.
     * If selectionStrategy is null, the defaultLocatorSelectionStrategy is used instead.
     * Then the new locatorSelectionStrategy is connected to the locatorClient and the matcher.
     * A new LocatorTargetSelector is created, set to the locatorSelectionStrategy and then set
     * as selector in the conduitSelectorHolder.
     *
     * @param conduitSelectorHolder
     * @param matcher
     * @param selectionStrategy
     */
    public void enable(ConduitSelectorHolder conduitSelectorHolder, SLPropertiesMatcher matcher,
            String selectionStrategy) {
        LocatorTargetSelector selector = new LocatorTargetSelector();
        selector.setEndpoint(conduitSelectorHolder.getConduitSelector().getEndpoint());

        String actualStrategy = selectionStrategy != null ? selectionStrategy : defaultLocatorSelectionStrategy;

        LocatorSelectionStrategy locatorSelectionStrategy = getLocatorSelectionStrategy(actualStrategy);
        locatorSelectionStrategy.setServiceLocator(locatorClient);
        if (matcher != null) {
            locatorSelectionStrategy.setMatcher(matcher);
        }
        selector.setLocatorSelectionStrategy(locatorSelectionStrategy);

        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "Client enabled with strategy "
                    + locatorSelectionStrategy.getClass().getName() + ".");
        }
        conduitSelectorHolder.setConduitSelector(selector);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Successfully enabled client " + conduitSelectorHolder
                    + " for the service locator");
        }
    }

}
