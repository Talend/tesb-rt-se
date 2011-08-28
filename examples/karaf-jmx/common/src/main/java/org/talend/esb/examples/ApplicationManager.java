package org.talend.esb.examples;

public interface ApplicationManager {
	
	public void connect();
	
	public void disconnect();
	
	public void	installFeature(String festureName);
	
	public void	uninstallFeature(String featureName); 
	
}
