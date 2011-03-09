package org.talend.esb.sam.agent.event;


/**
 * MonitoringEventData which is available as a message property
 * 
 * @author owulff
 * 
 */
public class MonitoringEventDataImpl implements MonitoringEventData {
	
	private String flowId = null;
	
	/**
	 * Accessor for the <b>flowId</b>.
	 * @return current value of FlowId
	*/
	public String getFlowId()
	{
		return this.flowId;	
	}
	
	
	/**
	 * Mutator for the <b>flowId</b>.
	 * @param flowId new value for FlowId
	*/
	public void setFlowId(String flowId)
	{
		if (this.flowId != null) throw new IllegalStateException("flowId already set");
		this.flowId = flowId;
		
	}
    
}