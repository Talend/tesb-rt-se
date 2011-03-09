package org.talend.esb.sam.agent.feature;

import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.talend.esb.sam.agent.producer.EventProducer;

public class MonitoringBusListener implements BusLifeCycleListener{

	private EventProducer eventProducer;
	
	public MonitoringBusListener(EventProducer eventProducer){
		this.eventProducer = eventProducer;
	}
	
	public void initComplete() {
		//ignore
	}

	public void preShutdown() {
		eventProducer.getEventCollector().stopSending();
	}

	public void postShutdown() {
		//ignore
	}
}
