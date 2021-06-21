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

import java.text.DateFormat;
import java.util.*;
import javax.xml.namespace.QName;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.talend.esb.locator.completer.ServiceNameCompleter;
import org.talend.esb.locator.tracker.ServiceLocatorTracker;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

@Command(scope = "tlocator", name = "list", description = "List Service Locator Endpoints")
@Service
public class ListOperation implements Action {

    @Option(name = "-v",
            aliases = {"--verbose"},
            required = false,
            description = "Verbose output. Prints all service and endpoint attributes.",
            multiValued = false)
    boolean verbose;

    @Option(name = "-ns",
            aliases = {"--namespace"},
            required = false,
            description = "Prints service name including namespace",
            multiValued = false)
    boolean printServiceNamespace;

    @Option(name = "-p",
            aliases = {"--protocol"},
            required = false,
            description = "Prints message protocol for endpoints",
            multiValued = false)
    boolean printProtocol;

    @Option(name = "-t",
            aliases = {"--transport"},
            required = false,
            description = "Prints transport protocol for endpoints",
            multiValued = false)
    boolean printTransport;

    @Option(name = "-d",
            aliases = {"--date"},
            required = false,
            description = "Prints date for endpoints: online/offline since...",
            multiValued = false)
    boolean printDate;

    @Option(name = "-ep",
            aliases = {"--properties", "--prop"},
            required = false,
            description = "Prints optional endpoint properties",
            multiValued = false)
    boolean printProperties;

    @Option(name = "-o",
            aliases = {"--offline-endpoints"},
            required = false,
            description = "Prints only services with at least one offline endpoint",
            multiValued = false)
    boolean offlineEndpointsOnly;

    @Option(name = "-O",
            aliases = {"--offline-services"},
            required = false,
            description = "Prints only services with no active endpoint",
            multiValued = false)
    boolean offlineServicesOnly;

    @Argument(index = 0,
            name = "filter",
            description = "Servicename filter. True if any part of the service name matches this filter. " +
                    "This filter is case sensitive.",
            required = false,
            multiValued = false)
    @Completion(ServiceNameCompleter.class)
    String filter;


    @Reference
    private ServiceLocator sl;

    @Override
    public Object execute() throws Exception {

        ServiceLocatorTracker slt = ServiceLocatorTracker.getInstance(sl);

        slt.updateServiceList();

        try {
            List<QName> services = new ArrayList<QName>(slt.getServiceQNames());

            if (services.isEmpty()) {
                System.out.println();
                System.out.println("No Services registered at Service Locator");
                System.out.println();
                return null;
            }

            sortServices(services);

            for (QName service : services) {
                if (filter != null && filter.length() > 0 && !service.toString().contains(filter)) {
                    // Filter is set but does not match
                    continue;
                }

                StringBuilder sb = new StringBuilder();

                List<SLEndpoint> endpoints = sl.getEndpoints(service);
                sortEndpoints(endpoints);

                int offlineEndpointsCount = 0;
                int onlineEndpointsCount = 0;
                for (SLEndpoint endpoint : endpoints) {
                    boolean alive = endpoint.isLive();
                    if (alive) {
                        onlineEndpointsCount++;
                    } else {
                        offlineEndpointsCount++;
                    }

                    if (!offlineEndpointsOnly || offlineEndpointsOnly && !alive) {
                        sb.append(" |-");
                        sb.append(alive
                                ? "\u001b[1;32m online \u001b[0m : "
                                : "\u001b[1;31m offline\u001b[0m : ");

                        String address = endpoint.getAddress();
                        sb.append(address);

                        if (printProtocol || verbose) {
                            String protocol = endpoint.getBinding().getValue();
                            sb.append(" : ").append(protocol);
                        }

                        if (printTransport || verbose) {
                            String transport = endpoint.getTransport().getValue();
                            sb.append(" : ").append(transport);
                        }

                        if (printDate || verbose) {
                            if (alive) {
                                long lastTimeStarted = endpoint.getLastTimeStarted();
                                sb.append(" : online since ").append(formatTimeStamp(lastTimeStarted));
                            } else {
                                long lastTimeStopped = endpoint.getLastTimeStopped();
                                sb.append(" : offline since ").append(formatTimeStamp(lastTimeStopped));
                            }
                        }
                        sb.append("\n");

                        if (printProperties || verbose) {
                            sb.append(printProperties(endpoint.getProperties()));
                        }
                    }
                }
                // Now add first line including endpoint count
                StringBuilder sbServiceName = new StringBuilder();
                if (printServiceNamespace || verbose) {
                    sbServiceName.append("{").append(service.getNamespaceURI()).append("}");
                }
                sbServiceName.append("\u001b[1;37m").append(service.getLocalPart()).append("\u001b[0m");
                sbServiceName.append(" (").append(onlineEndpointsCount).append("/").append(onlineEndpointsCount + offlineEndpointsCount).append(")");
                sbServiceName.append("\n");
                sb.insert(0, sbServiceName);

                // Now print complete StringBuilder content
                if (!offlineServicesOnly && !offlineEndpointsOnly // No offline filter applied
                        || offlineServicesOnly && onlineEndpointsCount == 0 // Only services with no active endpoint
                        || offlineEndpointsOnly && !offlineServicesOnly && offlineEndpointsCount > 0) // Only offline endpoints
                {
                    System.out.println();
                    System.out.println(sb);
                }
            }
        } catch (ServiceLocatorException e) {
            System.err.println(e.getMessage());
        }
        System.out.println();

        return null;
    }

    private void sortEndpoints(List<SLEndpoint> endpoints) {
        Collections.sort(endpoints, new Comparator<SLEndpoint>() {
            @Override
            public int compare(SLEndpoint o1, SLEndpoint o2) {
                if (o1 == null || o1.getAddress() == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                }
                return o1.getAddress().compareTo(o2.getAddress());
            }
        });
    }

    private void sortServices(List<QName> services) {
        Collections.sort(services, new Comparator<QName>() {
            @Override
            public int compare(QName o1, QName o2) {
                if (o1 == null || o1.getLocalPart() == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                }
                return o1.getLocalPart().compareTo(o2.getLocalPart());
            }
        });
    }

    private String printProperties(SLProperties properties) {
        StringBuilder sb = new StringBuilder();
        Collection<String> keys = properties.getPropertyNames();
        if (keys.isEmpty()) {
            // sb.append("  |- no properties set");
        } else {
            for (String key : keys) {
                String values = properties.getValues(key).toString();
                sb.append("  |- " + key + " : " + values.substring(1, values.length() - 1)).append("\n");
            }
        }
        return sb.toString();
    }

    private String formatTimeStamp(long timestamp) {
        String timeStampStr;
        if (timestamp >= 0) {
            Calendar timeStarted = Calendar.getInstance();
            DateFormat df = DateFormat.getDateTimeInstance();
            timeStarted.setTimeInMillis(timestamp);
            timeStampStr = df.format(timeStarted.getTime());
        } else {
            timeStampStr = "";
        }
        return timeStampStr;
    }
}
