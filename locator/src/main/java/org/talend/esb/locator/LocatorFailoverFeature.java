package org.talend.esb.locator;

import java.util.List;

import org.apache.cxf.clustering.FailoverFeature;

public class LocatorFailoverFeature extends FailoverFeature {
	public LocatorFailoverFeature(List<String> adresses) {
		super();
		LocatorFailoverStrategy lfs = new LocatorFailoverStrategy();
//		lfs.setAlternateAddresses(adresses);
		setStrategy(lfs);
	}
	
	
}
