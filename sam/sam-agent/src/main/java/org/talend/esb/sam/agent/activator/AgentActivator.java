/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.activator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.agent.util.Converter;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;


/**
 * This bundle activator used to implement the feature of get
 * the start/stop lifecycle event of TESB container.
 */
public class AgentActivator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(AgentActivator.class.getName());

    private MonitoringService monitoringService;
    private int retryNum;
    private long retryDelay;

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        if (!checkConfig(context)) {
            return;
        }

        if (monitoringService == null) {
            initWsClient(context);
        }

        EventType serverStartEvent = createEventType(EventEnumType.SERVER_START);
        putEvent(serverStartEvent);

        LOG.info("Send SERVER_START event to SAM Server successful!");
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        if (!checkConfig(context)) {
            return;
        }

        if (monitoringService == null) {
            initWsClient(context);
        }

        EventType serverStopEvent = createEventType(EventEnumType.SERVER_STOP);
        putEvent(serverStopEvent);

        LOG.info("Send SERVER_STOP event to SAM Server successful!");
    }

    /**
     * Creates the event type.
     *
     * @param type the EventEnumType
     * @return the event type
     */
    private EventType createEventType(EventEnumType type) {
        EventType eventType = new EventType();
        eventType.setTimestamp(Converter.convertDate(new Date()));
        eventType.setEventType(type);

        OriginatorType origType = new OriginatorType();
        origType.setProcessId(Converter.getPID());
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            origType.setIp(inetAddress.getHostAddress());
            origType.setHostname(inetAddress.getHostName());
        } catch (UnknownHostException e) {
            origType.setHostname("Unknown hostname");
            origType.setIp("Unknown ip address");
        }
        eventType.setOriginator(origType);

        String path = System.getProperty("karaf.home");
        CustomInfoType ciType = new CustomInfoType();
        CustomInfoType.Item cItem = new CustomInfoType.Item();
        cItem.setKey("path");
        cItem.setValue(path);
        ciType.getItem().add(cItem);
        eventType.setCustomInfo(ciType);

        return eventType;
    }

    /**
     * Put event.
     *
     * @param eventType the event type
     * @throws Exception the exception
     */
    private void putEvent(EventType eventType) throws Exception {
        List<EventType> eventTypes = Collections.singletonList(eventType);

        int i;
        for (i = 0; i < retryNum; ++i) {
            try {
                monitoringService.putEvents(eventTypes);
                break;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
            Thread.sleep(retryDelay);
        }

        if (i == retryNum) {
            LOG.warning("Could not send events to monitoring service after " + retryNum + " retries.");
            throw new Exception("Send SERVER_START/SERVER_STOP event to SAM Server failed");
        }

    }

    /**
     * Check config.
     *
     * @param context the context
     * @return true, if successful
     * @throws Exception the exception
     */
    private boolean checkConfig(BundleContext context) throws Exception {
        ServiceReference serviceRef = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin cfgAdmin = (ConfigurationAdmin)context.getService(serviceRef);
        Configuration config = cfgAdmin.getConfiguration("org.talend.esb.sam.agent");

        return "true".equalsIgnoreCase((String)config.getProperties().get("collector.lifecycleEvent"));
    }

    /**
     * Inits the ws client.
     *
     * @param context the context
     * @throws Exception the exception
     */
    private void initWsClient(BundleContext context) throws Exception {
        ServiceReference serviceRef = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin cfgAdmin = (ConfigurationAdmin)context.getService(serviceRef);
        Configuration config = cfgAdmin.getConfiguration("org.talend.esb.sam.agent");

        String serviceURL = (String)config.getProperties().get("service.url");
        retryNum = Integer.parseInt((String)config.getProperties().get("service.retry.number"));
        retryDelay = Long.parseLong((String)config.getProperties().get("service.retry.delay"));

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(org.talend.esb.sam.monitoringservice.v1.MonitoringService.class);
        factory.setAddress(serviceURL);
        monitoringService = (MonitoringService)factory.create();
    }

}
