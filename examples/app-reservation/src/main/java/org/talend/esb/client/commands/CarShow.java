package org.talend.esb.client.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.BundleContext;

@Command(scope = "test", name = "hello", description = "Says hello")

public class CarShow extends OsgiCommandSupport {
	@Override
	protected Object doExecute() throws Exception {
		System.out.println("Executing CarShow");
		
		BundleContext bc = this.getBundleContext();
		return null;
	}
}
