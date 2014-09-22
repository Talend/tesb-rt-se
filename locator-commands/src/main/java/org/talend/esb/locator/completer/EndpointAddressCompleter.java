/*
 * #%L
 * Service Locator Commands
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
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

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.completer.ArgumentCompleter;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.apache.karaf.shell.console.jline.CommandSessionHolder;
import org.talend.esb.locator.tracker.ServiceLocatorTracker;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class EndpointAddressCompleter extends StringsCompleter {

    private ServiceNameCompleter serviceNameCompleter;

    private ServiceLocatorTracker slt;

    public EndpointAddressCompleter(ServiceLocator serviceLocator) {
        super(true);
        slt = ServiceLocatorTracker.getInstance(serviceLocator);
        serviceNameCompleter = new ServiceNameCompleter(serviceLocator);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int complete(String buffer, int cursor, List candidates) {
        CommandSession session = CommandSessionHolder.getSession();
        ArgumentCompleter.ArgumentList list = (ArgumentCompleter.ArgumentList)session.get(ArgumentCompleter.ARGUMENTS_LIST);
        String[] arguments = list.getArguments();
        int index = list.getCursorArgumentIndex();
        int paramCount = 0;
        for (String arg : arguments) {
            if (arg.startsWith("-")) {
                paramCount++;
            }
        }
        switch (index - paramCount) {
        case 1:
            // Needed because of bug in karaf
            return serviceNameCompleter.complete(buffer, cursor, candidates);
        case 2:
            buffer = (arguments.length - paramCount > 2)
                ? arguments[arguments.length - 1]
                : null;
            synchronized (getStrings()) {
                getStrings().clear();
                getStrings().addAll(slt.getEndpoints(arguments[(arguments.length - paramCount > 2) ? arguments.length - 2 : arguments.length - 1]));
                return super.complete(buffer, cursor, candidates);
            }
        default:
            return -1;
        }
    }
}
