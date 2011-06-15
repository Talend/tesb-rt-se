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
package org.talend.esb.job.command.completers;

import jline.console.completer.StringsCompleter;
import org.apache.karaf.shell.console.Completer;
import org.talend.esb.job.controller.Controller;

import java.util.List;

/**
 * A JLine completer for Talend route.
 */
public class RouteCompleter implements Completer {

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public int complete(String buffer, int cursor, List candidates) {
        try {
            StringsCompleter delegate = new StringsCompleter();
            for (String name : controller.listRoutes()) {
                delegate.getStrings().add(name);
            }
            return delegate.complete(buffer, cursor, candidates);
        } catch (Exception e) {
            // nothing to do, no completion
        }
        return 0;
    }

}
