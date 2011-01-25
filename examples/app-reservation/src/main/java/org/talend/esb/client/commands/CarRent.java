package org.talend.esb.client.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "test", name = "hello", description = "Says hello")

public class CarRent extends OsgiCommandSupport {
	@Argument(index = 0, name = "pos", description = "Rent a car listed in search result of racSearch", required = true, multiValued = false)
    String pid;		
	@Override
	protected Object doExecute() throws Exception {
		System.out.println("Executing CarRent");
		return null;
	}
}
