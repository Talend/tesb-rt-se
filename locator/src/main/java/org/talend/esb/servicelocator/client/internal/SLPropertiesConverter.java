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

import java.util.Collection;
import java.util.List;

import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.internal.endpoint.EntryType;
import org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;

public final class SLPropertiesConverter {

    private SLPropertiesConverter() { }

    public static ServiceLocatorPropertiesType toServiceLocatorPropertiesType(SLProperties props) {
        ObjectFactory of = new ObjectFactory();
        ServiceLocatorPropertiesType slPropertiesType = of.createServiceLocatorPropertiesType();
        List<EntryType> entries = slPropertiesType.getEntry();
        for (String name : props.getPropertyNames()) {
            entries.add(createEntry(props, name));
        }
        return slPropertiesType;
    }

    public static SLProperties toSLProperties(ServiceLocatorPropertiesType props) {
        SLPropertiesImpl slProperties = new SLPropertiesImpl();

        for (EntryType entry : props.getEntry()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            slProperties.addProperty(key, values);
        }

        return slProperties;
    }

    private static EntryType createEntry(SLProperties props, String name) {
        EntryType entry = new EntryType();
        entry.setKey(name);
        List<String> jaxbValues = entry.getValue();
        Collection<String> values = props.getValues(name);
        for (String value : values) {
            jaxbValues.add(value);
        }
        return entry;
    }
}
