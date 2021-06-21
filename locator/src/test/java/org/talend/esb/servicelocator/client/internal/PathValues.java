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
package org.talend.esb.servicelocator.client.internal;


public interface PathValues {

    String SERVICE_NAME_1 = "{http:%2F%2Fexample.com%2Fservices}service1";

    String SERVICE_NAME_2 = "{http:%2F%2Fexample.com%2Fservices}service2";

    String SERVICE_PATH_1 = "/cxf-locator/" + SERVICE_NAME_1;

    String SERVICE_PATH_2 = "/cxf-locator/" + SERVICE_NAME_2;

    String ENDPOINT_NODE_1 = "http:%2F%2Fep.com%2Fendpoint1";

    String ENDPOINT_NODE_2 = "http:%2F%2Fep.com%2Fendpoint2";

    String ENDPOINT_PATH_11 = SERVICE_PATH_1 + "/" + ENDPOINT_NODE_1;

    String ENDPOINT_PATH_12 = SERVICE_PATH_1 + "/" + ENDPOINT_NODE_2;

    String ENDPOINT_PATH_22 = SERVICE_PATH_2 + "/" + ENDPOINT_NODE_2;

    String STATUS_NODE = "live";

    String ENDPOINT_STATUS_PATH_11 = ENDPOINT_PATH_11 + "/" + STATUS_NODE;

    String ENDPOINT_STATUS_PATH_12 = ENDPOINT_PATH_12 + "/" + STATUS_NODE;

}
