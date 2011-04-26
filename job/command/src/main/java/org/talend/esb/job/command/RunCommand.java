package org.talend.esb.job.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.talend.esb.job.controller.Controller;

/**
 * Run a Talend job identified by name.
 */
@Command(scope = "job", name = "run", description ="Run a Talend job")
public class RunCommand extends OsgiCommandSupport {

    @Option(name = "-a", aliases = {"--args"}, description = "Arguments to use when running the Talend job", required = false, multiValued = false)
    String args;

    @Argument(index = 0, name = "name", description = "The name of the Talend job to run", required = true, multiValued = false)
    String job = null;

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Object doExecute() throws Exception {
        String[] arguments = null;
        if (args != null) {
            arguments = args.split(" ");
        }
        if (arguments == null) {
            arguments = new String[0];
        }
        controller.run(job, arguments);
        return null;
    }

}
