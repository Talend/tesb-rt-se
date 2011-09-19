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

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.talend.esb.job.controller.Controller;
import java.util.List;

/**
 * List available Talend Data Services.
 */
@Command(scope = "service", name = "list", description = "Lists all existing Talend services.")
public class ListServicesCommand extends OsgiCommandSupport {

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    protected Object doExecute() throws Exception {
        List<String> list = controller.listServices();
        for (String name:list) {
            System.out.println(name);
        }
        return null;
    }

}
