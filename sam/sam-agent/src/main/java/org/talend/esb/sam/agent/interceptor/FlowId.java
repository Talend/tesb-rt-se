/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.sam.agent.interceptor;

public class FlowId {
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
