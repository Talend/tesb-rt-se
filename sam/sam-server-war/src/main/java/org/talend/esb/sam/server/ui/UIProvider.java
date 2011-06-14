/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.sam.server.ui;

import com.google.gson.JsonObject;

/**
 * Interface of the data provider for SAM UI
 *
 * @author zubairov
 */
public interface UIProvider {

	/**
	 * Retrieve an aggregated list of events
	 * starting with offset
	 * maximum number of items is limit
	 *
	 * @param offset
	 * @param limit
	 * @param attributes
	 * @return
	 */
	JsonObject getEvents(long offset, String baseURL, CriteriaAdapter criteria);

	/**
	 * Returns a detailed information about flow with given ID or null if no such flow was found
	 *
	 * @param flowID
	 * @param baseURL
	 * @return
	 */
	JsonObject getFlowDetails(String flowID, String baseURL);

	/**
	 * Returns a detailed information about event with given ID or null if no such event was found
	 *
	 * @param eventID
	 * @return
	 */
	JsonObject getEventDetails(String eventID);


}
