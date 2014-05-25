package org.talend.esb.mep.requestcallback.feature;

import java.util.Dictionary;
import java.util.Map;

public interface Configuration extends Map<String, Object> {
	interface ChangeListener {
		void changed(Configuration configuration);
	}
	String getProperty(String key);
	Integer getIntegerProperty(String key);
	Long getLongProperty(String key);
	Boolean getBooleanProperty(String key);
	String getExpandedProperty(String key);
	void fillProperties(String prefix, Map<? super String, Object> properties);
	void fillExpandedProperties(String prefix, Map<? super String, Object> properties);
	void updateDynamicConfiguration(Map<?, ?> updateMap, boolean replaceCurrent);
	void updateDynamicConfiguration(Dictionary<?, ?> updateDict, boolean replaceCurrent);
	void refreshStaticConfiguration();
	ChangeListener getChangeListener();
	void setChangeListener(ChangeListener changeListener);
}
