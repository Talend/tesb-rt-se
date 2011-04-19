package org.talend.esb.job.command;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.talend.esb.job.controller.Controller;
import java.util.List;

/**
 * List available Talend Jobs.
 */
@Command(scope = "job", name = "list", description = "Lists all existing Talend jobs.")
public class ListCommand extends OsgiCommandSupport {

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    protected Object doExecute() throws Exception {
        List<String> list = controller.list();
        for (String name:list) {
            System.out.println(name);
        }
        return null;
    }

}
