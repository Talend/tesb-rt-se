/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.cm.ConfigurationException;

public class Configuration {

    private Map<String, String> properties = new HashMap<String, String>();

    public Configuration(Dictionary<?, ?> properties) throws ConfigurationException {
        if (properties != null) {
            Enumeration<?> keysEnum = properties.keys();
            while (keysEnum.hasMoreElements()) {
                String key = (String) keysEnum.nextElement();
                Object val = properties.get(key);
                if (! isString(val)) {
                    throw new ConfigurationException(key, "Value is not of type String.");
                }
                this.properties.put((String) key, (String) val);
            }
        }
    }

    public String[] getArguments() {
        List<String> args = new ArrayList<String>();
        
        for (Entry<String, String> configEntry : properties.entrySet()) {
            if (configEntry.getKey().equals("context")) {
                args.add("--context=" + configEntry.getValue());
            } else {
                args.add("--context_param" + configEntry.getKey() + "=" + configEntry.getValue());
            }
        }

        return args.toArray(new String[args.size()]);
    }
    
    boolean isString(Object obj) {
        return obj instanceof String;
    }

}
