package org.talend.esb.transport.jms;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractTransportFactory;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.ConduitInitiator;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.jms.JMSConduit;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.cxf.transport.jms.JMSDestination;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.talend.esb.transport.jms.wsdl11.JMSOldConfigHolder;

@NoJSR250Annotations(unlessNull = { "bus" })
public class TesbJmsTransportFactory extends AbstractTransportFactory implements ConduitInitiator,
    DestinationFactory {

    public static final List<String> TRANSPORT_IDS 
        = Arrays.asList(
            "http://cxf.apache.org/transports/jms",
            "http://cxf.apache.org/transports/jms/configuration",
            "http://schemas.xmlsoap.org/soap/jms",
            "http://www.w3.org/2010/soapjms"            
        );

    private static final Set<String> URI_PREFIXES = new HashSet<String>();
    static {
        URI_PREFIXES.add("jms://");
        URI_PREFIXES.add("jms:");
    }

    public TesbJmsTransportFactory() {
        super(TRANSPORT_IDS);
    }
    public TesbJmsTransportFactory(Bus b) {
        super(TRANSPORT_IDS, b);
    }
    
    @Resource(name = "cxf")
    public void setBus(Bus bus) {
        super.setBus(bus);
    }	
    public Conduit getConduit(EndpointInfo endpointInfo) throws IOException {
        return getConduit(endpointInfo, endpointInfo.getTarget());
    }

    /**
     * {@inheritDoc}
     */
    public Conduit getConduit(EndpointInfo endpointInfo, EndpointReferenceType target) throws IOException {
        JMSOldConfigHolder old = new JMSOldConfigHolder();
        JMSConfiguration jmsConf = old.createJMSConfigurationFromEndpointInfo(bus,
                                                                              endpointInfo,
                                                                              target,
                                                                              true);
        return new JMSConduit(endpointInfo, target, jmsConf, bus);
    }

    /**
     * {@inheritDoc}
     */
    public Destination getDestination(EndpointInfo endpointInfo) throws IOException {
        JMSOldConfigHolder old = new JMSOldConfigHolder();
        JMSConfiguration jmsConf = old.createJMSConfigurationFromEndpointInfo(bus, endpointInfo, null, false);
        return new JMSDestination(bus, endpointInfo, jmsConf);
    }

}
