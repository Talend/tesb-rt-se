package org.talend.esb.mep.requestcallback.feature;

import java.util.Dictionary;
import java.util.Map;

public interface Configuration extends Map<String, Object> {
	void updateDynamicConfiguration(Map<?, ?> updateMap);
	void updateDynamicConfiguration(Dictionary<?, ?> updateDict);
	void refreshStaticConfiguration();
}
