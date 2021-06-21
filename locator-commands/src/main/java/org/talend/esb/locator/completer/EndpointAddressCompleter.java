/*
 * #%L
 * Service Locator Commands
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.locator.completer;

import java.util.List;

import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.talend.esb.locator.tracker.ServiceLocatorTracker;
import org.talend.esb.servicelocator.client.ServiceLocator;

@Service
public class EndpointAddressCompleter implements Completer {

    @Reference
    private ServiceLocator sl;

    @Override
    public int complete(Session session, CommandLine commandLine, List<String> list) {

        ServiceLocatorTracker slt = ServiceLocatorTracker.getInstance(sl);

        String[] arguments = commandLine.getArguments();
        int paramCount = 0;
        for (String arg : arguments) {
            if (arg.startsWith("-")) {
                paramCount++;
            }
        }

        StringsCompleter delegate2 = new StringsCompleter();
        delegate2.getStrings().addAll(slt.getEndpoints(arguments[(arguments.length - paramCount > 2)
                ? arguments.length - 2
                : arguments.length - 1]));
        return delegate2.complete(session, commandLine, list);
    }
}
