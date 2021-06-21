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
package org.talend.esb.locator.commands;

import javax.xml.namespace.QName;

import org.apache.karaf.shell.api.action.*;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.talend.esb.locator.completer.EndpointAddressCompleter;
import org.talend.esb.locator.completer.FullServiceNameCompleter;
import org.talend.esb.locator.tracker.ServiceLocatorTracker;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

@Command(scope = "tlocator",
        name = "register",
        description = "Registers endpoint at Service Locator.\n"
                + "\n\u001b[1;37mEXAMPLE\u001b[0m"
                + "\n\ttlocator:register -p \"{http://my.company.com/my-service-namespace}MyServiceName\" " +
                "http://my.server.com:8040/services/MyServiceName"
                + "\n\ttlocator:register MyServiceName http://another.server.com:8040/services/MyServiceName")
@Service
public class RegisterOperation implements Action {

    @Option(name = "-p",
            aliases = {"--persistent"},
            required = false,
            description = "Endpoint will be registered as always online. No heardbeat will be required.",
            multiValued = false)
    boolean persistent;

    @Argument(index = 0,
            name = "serviceName",
            description = "Service name for endpoint to be added. " +
                    "Must be fully qualified if adding a new / unknown service name. " +
                    "For adding an endpoint to a known service name local part of service name is sufficient.",
            required = true,
            multiValued = false)
    @Completion(FullServiceNameCompleter.class)
    String service;

    @Argument(index = 1,
            name = "URL",
            description = "Endpoint address to be registered at Service Locator",
            required = true,
            multiValued = false)
    @Completion(EndpointAddressCompleter.class)
    String endpoint;

    @Reference
    private ServiceLocator sl;

    @Override
    public Object execute() throws Exception {

        ServiceLocatorTracker slt = ServiceLocatorTracker.getInstance(sl);

        System.out.println();
        try {
            QName serviceName = slt.getServiceName(service);
            sl.register(serviceName, endpoint, persistent);
            System.out.println("Endpoint has been registered at Service Locator");
            slt.updateServiceList();
        } catch (ServiceLocatorException e) {
            System.err.println(e.getMessage());
        }
        System.out.println();

        return null;
    }

}
