/*
 * #%L
 * Talend ESB :: Camel Talend Job Component
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
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

package org.talend.camel;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.IntrospectionSupport;

/**
 * <p>
 * Represents the component that manages {@link TalendEndpoint}.
 * </p>
 */
public class TalendComponent extends DefaultComponent {

    public TalendComponent() {
        super();
    }

    public TalendComponent(CamelContext context) {
        super(context);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
        throws Exception {
        final TalendEndpoint endpoint = new TalendEndpoint(uri, remaining, this);
        setProperties(endpoint, parameters);
        // extract the properties.xxx and set them as properties
        Map<String, Object> properties =
                IntrospectionSupport.extractProperties(parameters, "endpointProperties.");
        if (properties != null) {
            Map<String, String> endpointProperties = new HashMap<String, String>(properties.size());
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                endpointProperties.put(entry.getKey(), entry.getValue().toString());
            }
            endpoint.setEndpointProperties(endpointProperties);
        }
        return endpoint;
    }

}
