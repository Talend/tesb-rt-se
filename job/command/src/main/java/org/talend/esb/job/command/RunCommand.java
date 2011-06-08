/*
 * #%L
 * Talend :: ESB :: Job :: Command
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
