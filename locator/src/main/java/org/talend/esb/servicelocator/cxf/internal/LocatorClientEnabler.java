/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.ConduitSelector;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class LocatorClientEnabler {

    private static final Logger LOG = Logger.getLogger(LocatorClientEnabler.class.getPackage().getName());

    private static final String DEFAULT_STRATEGY = "defaultSelectionStrategy";

    private ServiceLocator locatorClient;

    private Map<String, LocatorSelectionStrategyFactory> locatorSelectionStrategies;

    private LocatorSelectionStrategyFactory locatorSelectionStrategyFactory;

    private String defaultLocatorSelectionStrategy;

    public void setServiceLocator(ServiceLocator serviceLocator) {
        locatorClient = serviceLocator;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client " + serviceLocator + " was set for LocatorClientRegistrar.");
        }
    }

    public void setBus(Bus bus) {
    }

    /**
     * Sets a map representing the locatorSelectionStrategies and sets locatorSelectionStrategy to the DEFAULT_STRATEGY.
     * @param locatorSelectionStrategies
     */
    public void setLocatorSelectionStrategies(
            Map<String, LocatorSelectionStrategyFactory> locatorSelectionStrategies) {
        this.locatorSelectionStrategies = locatorSelectionStrategies;
        this.locatorSelectionStrategyFactory = locatorSelectionStrategies.get(DEFAULT_STRATEGY);
    }

    /**
     * If the String argument locatorSelectionStrategy is as key in the map representing the locatorSelectionStrategies, the
     * corresponding strategy is selected, else it remains unchanged.
     * @param locatorSelectionStrategy
     */
    public void setLocatorSelectionStrategy(String locatorSelectionStrategy) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Strategy " + locatorSelectionStrategy
                    + " was set for LocatorClientRegistrar.");
        }
        if (locatorSelectionStrategies.containsKey(locatorSelectionStrategy)) {
            this.locatorSelectionStrategyFactory = locatorSelectionStrategies.get(locatorSelectionStrategy);
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "LocatorSelectionStrategy " + locatorSelectionStrategy
                        + " not registered at LocatorClientEnabler.");
            }
        }
    }

    /**
     * If the String argument defaultLocatorSelectionStrategy is as key in the map representing the locatorSelectionStrategies, the
     * corresponding strategy is selected and set as default strategy, else both the selected strategy and the default strategy remain
     * unchanged.
     * @param defaultLocatorSelectionStrategy
     */
    public void setDefaultLocatorSelectionStrategy(String defaultLocatorSelectionStrategy) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Default strategy " + defaultLocatorSelectionStrategy
                    + " was set for LocatorClientRegistrar.");
        }
        if (locatorSelectionStrategies.containsKey(defaultLocatorSelectionStrategy)) {
            this.locatorSelectionStrategyFactory = locatorSelectionStrategies.get(defaultLocatorSelectionStrategy);
            this.defaultLocatorSelectionStrategy = defaultLocatorSelectionStrategy;
            // setLocatorSelectionStrategy(defaultLocatorSelectionStrategy);
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "Default LocatorSelectionStrategy " + defaultLocatorSelectionStrategy
                        + " not registered at LocatorClientEnabler.");
            }
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

        if (selectionStrategy != null) {
            setLocatorSelectionStrategy(selectionStrategy);
        } else {
            setLocatorSelectionStrategy(defaultLocatorSelectionStrategy);
        }

        LocatorSelectionStrategy locatorSelectionStrategy = locatorSelectionStrategyFactory.getInstance();
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

    interface ConduitSelectorHolder {

        ConduitSelector getConduitSelector();

        void setConduitSelector(ConduitSelector selector);

    }

}
