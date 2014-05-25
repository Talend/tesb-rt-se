package org.talend.esb.mep.requestcallback.impl;

import java.util.LinkedList;
import java.util.List;

import org.talend.esb.mep.requestcallback.feature.Configuration;
import org.talend.esb.mep.requestcallback.feature.Configuration.ChangeListener;


public class MultiChangeListener implements ChangeListener {

	private final List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();

	@Override
	public void changed(Configuration configuration) {
		for (ChangeListener cl : changeListeners) {
			cl.changed(configuration);
		}
	}

	public boolean addChangeListener(ChangeListener changeListener) {
		return changeListeners.add(changeListener);
	}

	public boolean removeChangeListener(ChangeListener changeListener) {
		return changeListeners.remove(changeListener);
	}
 }
