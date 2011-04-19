package org.talend.esb.job.command.completers;

import jline.console.completer.StringsCompleter;
import org.apache.karaf.shell.console.Completer;
import org.talend.esb.job.controller.Controller;

import java.util.List;

/**
 * A JLine completer for Talend job.
 */
public class JobCompleter implements Completer {

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public int complete(String buffer, int cursor, List candidates) {
        try {
            StringsCompleter delegate = new StringsCompleter();
            for (String name : controller.list()) {
                delegate.getStrings().add(name);
            }
            return delegate.complete(buffer, cursor, candidates);
        } catch (Exception e) {
            // nothing to do, no completion
        }
        return 0;
    }

}
