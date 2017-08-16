package org.talend.esb.servicelocator.cxf.internal;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;

@Named
@Singleton
public class LocatorSelectionStrategyMap extends HashMap<String, LocatorSelectionStrategyFactory> {

    private static final long serialVersionUID = -8736337620917142309L;

    @Value("${locator.reloadAddressesCount}")
    private int reloadAddressesCount;

    @PostConstruct
    public void init() {
        RandomSelectionStrategyFactory randomSelectionStrategyFactory = new RandomSelectionStrategyFactory();
        randomSelectionStrategyFactory.setReloadAddressesCount(reloadAddressesCount);
        this.put("randomSelectionStrategy", randomSelectionStrategyFactory);

        EvenDistributionSelectionStrategyFactory evenDistributionSelectionStrategyFactory =
                new EvenDistributionSelectionStrategyFactory();
        evenDistributionSelectionStrategyFactory.setReloadAddressesCount(reloadAddressesCount);
        this.put("evenDistributionSelectionStrategy", evenDistributionSelectionStrategyFactory);

        this.put("defaultSelectionStrategy", new DefaultSelectionStrategyFactory());
    }
}
