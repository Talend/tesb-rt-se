package org.talend.esb.transport.jms;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.jms.JMSConduit;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.cxf.transport.jms.JMSDestination;

public class DurableSubscriptionFeature extends AbstractFeature {
	private String durableSubscriptionClientId;
	private String durableSubscriptionName;
	
    @Override
    public void initialize(Client client, Bus bus) {
        Conduit conduit = client.getConduit();
        if (conduit instanceof JMSConduit) {
        	JMSConfiguration jmsConfig = ((JMSConduit) conduit).getJmsConfig();
        	updateJMSConfig(jmsConfig);
        }
    }

    @Override
    public void initialize(Server server, Bus bus) {
        Destination destination = server.getDestination();
        if (destination instanceof JMSDestination) {
        	JMSConfiguration jmsConfig = ((JMSDestination) destination).getJmsConfig();
        	updateJMSConfig(jmsConfig);
        }
    }

	private void updateJMSConfig(JMSConfiguration jmsConfig) {
		if (jmsConfig != null) {
			if(this.durableSubscriptionClientId!=null){
				jmsConfig.setDurableSubscriptionClientId(durableSubscriptionClientId);
			}
		
			if(this.durableSubscriptionName!=null){
				jmsConfig.setDurableSubscriptionName(durableSubscriptionName);
				jmsConfig.setSubscriptionDurable(true);
			}
		}
	}
    
	public void setDurableSubscriptionClientId(String durableSubscriptionClientId) {
		this.durableSubscriptionClientId = durableSubscriptionClientId;
	}

	public void setDurableSubscriptionName(String durableSubscriptionName) {
		this.durableSubscriptionName = durableSubscriptionName;
	}

}
   
