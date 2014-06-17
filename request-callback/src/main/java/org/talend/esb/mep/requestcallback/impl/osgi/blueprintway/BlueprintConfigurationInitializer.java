package org.talend.esb.mep.requestcallback.impl.osgi.blueprintway;


import java.util.List;

import org.talend.esb.mep.requestcallback.feature.Configuration;
import org.talend.esb.mep.requestcallback.feature.ConfigurationInitializer;


public class BlueprintConfigurationInitializer {

    public BlueprintConfigurationInitializer(List<Configuration> configList) {
        ConfigurationInitializer.addConfigurations(configList);
    }
}