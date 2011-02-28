package org.sopera.monitoring.event;


/**
 * MonitoringEventData which is available as a message property
 * 
 * @author owulff
 * 
 */
public interface MonitoringEventData {
  
	/**
	 * Accessor for the <b>flowId</b>.
	 * @return current value of FlowId
	*/
	String getFlowId();
	
	/**
	 * Mutator for the <b>flowId</b>.
	 * @param flowId new value for FlowId
	*/
	void setFlowId(String flowId);
    
}