package org.talend.esb.client.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "test", name = "hello", description = "Says hello")

public class CarSearch extends OsgiCommandSupport {
	@Override
	protected Object doExecute() throws Exception {
		System.out.println("Executing CarSearch");
		return null;
	}
}