/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.client;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend;

public class ServiceLocatorMain {

    private ServiceLocatorImpl sl;

    private ZKBackend backend;

    private PrintStream out;

    public ServiceLocatorMain() {
        backend = new ZKBackend();
        sl = new ServiceLocatorImpl();
        sl.setBackend(backend);
    }

    public void setLocatorEndpoints(String locatorEndpoints) {
        backend.setLocatorEndpoints(locatorEndpoints);
    }

    public void exec(OutputStream outputStream) throws InterruptedException, ServiceLocatorException {
        this.out = new PrintStream(outputStream);

        sl.connect();
        printServices();
        sl.disconnect();
    }

    private void printServices() throws InterruptedException, ServiceLocatorException {
        List<QName> services = sl.getServices();
        boolean first = true;

        for (QName service : services) {
            if (!first) {
                out.println(" |");
            }
            out.println(service);
            printEndpoints(service);
            first = false;
        }
    }

    private void printEndpoints(QName service) throws InterruptedException, ServiceLocatorException {
        List<SLEndpoint> endpoints = sl.getEndpoints(service);

        for (SLEndpoint endpoint : endpoints) {
            out.println(" |--- " + endpoint.getAddress());
            out.println(" |    |-- " + (endpoint.isLive() ? "running" : "stopped"));
            out.println(" |    |-- last time started " + formatTimeStamp(endpoint.getLastTimeStarted()));
            out.println(" |    |-- last time stopped " + formatTimeStamp(endpoint.getLastTimeStopped()));
            out.println(" |    |-- transport " + endpoint.getTransport());
            out.println(" |    |-- protocol " + endpoint.getBinding());
            printProperties(endpoint.getProperties());
        }
    }

    private String formatTimeStamp(long timestamp) {
        String timeStampStr;
        if (timestamp >= 0) {
            Calendar timeStarted = Calendar.getInstance();
            DateFormat dFormat = DateFormat.getDateInstance();
            DateFormat tFormat = DateFormat.getTimeInstance();
            timeStarted.setTimeInMillis(timestamp);
            timeStampStr = dFormat.format(timeStarted.getTime()) + " "
                    + tFormat.format(timeStarted.getTime());
        } else {
            timeStampStr = "n/a";
        }
        return timeStampStr;
    }

    private void printProperties(SLProperties props) {
        Collection<String> names = props.getPropertyNames();
        if (names.isEmpty()) {
            out.println(" |    |-- no properties defined");
        } else {
            for (String name : props.getPropertyNames()) {
                props.getValues(name);
                out.println(" |    |-- key: " + name + ", values: " + props.getValues(name));
            }
        }
    }

    public static void main(String[] args) {

        ServiceLocatorMain main = new ServiceLocatorMain();
        if (!parseOptions(args, main)) {
            usage();
            return;
        }
        try {
            main.exec(System.out);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ServiceLocatorException e) {
            e.printStackTrace();
        }

    }

    private static boolean parseOptions(String[] args, ServiceLocatorMain main) {
        List<String> argList = Arrays.asList(args);
        Iterator<String> argIter = argList.iterator();
        while (argIter.hasNext()) {
            String opt = argIter.next();
            try {
                if ("-endpoints".equals(opt)) {
                    main.setLocatorEndpoints(argIter.next());
                } else {
                    System.err.println("Error: unknown option " + opt);
                    return false;
                }
            } catch (NoSuchElementException e) {
                System.err.println("Error: no argument found for option " + opt);
                return false;
            }
        }
        return true;
    }

    static void usage() {
        System.err.println("ServiceLocatorMain -endpoints host:port[,host:port]*");
    }
}
