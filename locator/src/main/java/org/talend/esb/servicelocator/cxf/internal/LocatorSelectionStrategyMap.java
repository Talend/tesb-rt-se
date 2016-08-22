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
    
    @Value("${locator.reloadAdressesCount}")
    private int reloadAdressesCount;
    
    @PostConstruct
    public void init() {
        RandomSelectionStrategyFactory randomSelectionStrategyFactory = new RandomSelectionStrategyFactory();
        randomSelectionStrategyFactory.setReloadAdressesCount(reloadAdressesCount);
        this.put("randomSelectionStrategy", randomSelectionStrategyFactory);

        EvenDistributionSelectionStrategyFactory evenDistributionSelectionStrategyFactory =
                new EvenDistributionSelectionStrategyFactory();
        evenDistributionSelectionStrategyFactory.setReloadAdressesCount(reloadAdressesCount);
        this.put("evenDistributionSelectionStrategy", evenDistributionSelectionStrategyFactory);

        this.put("defaultSelectionStrategy", new DefaultSelectionStrategyFactory());
    }
}
