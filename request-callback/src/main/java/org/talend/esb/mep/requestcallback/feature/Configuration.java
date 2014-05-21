package org.talend.esb.mep.requestcallback.feature;

import java.util.Dictionary;
import java.util.Map;

public interface Configuration extends Map<String, Object> {
	void updateDynamicConfiguration(Map<?, ?> updateMap, boolean replaceCurrent);
	void updateDynamicConfiguration(Dictionary<?, ?> updateDict, boolean replaceCurrent);
	void refreshStaticConfiguration();
}
