/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.client.internal;

import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.internal.endpoint.BindingType;
import org.talend.esb.servicelocator.client.internal.endpoint.TransportType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestContent.*;
import static org.talend.esb.servicelocator.TestValues.*;

public class ContentHolderTest {

    public static final byte[] EMPTY_CONTENT = new byte[0];
    
    private  byte[] content;

    private SLPropertiesImpl props;

    private ContentHolder holder;
    
    @Before
    public void setUp() throws Exception {
        props = new SLPropertiesImpl();
        props.addProperty(NAME_1, VALUE_1, VALUE_2);

        content = createContent(ENDPOINT_1, LAST_TIME_STARTED, LAST_TIME_STOPPED, BindingType.JAXRS, TransportType.HTTP, props);
    }

    @Test
    public void getAddress() {
        holder = new ContentHolder(content);
        assertEquals(ENDPOINT_1, holder.getAddress());
    }

    @Test
    public void getLastTimeStarted() {
        holder = new ContentHolder(content);
        assertEquals(LAST_TIME_STARTED, holder.getLastTimeStarted());
    }

    @Test
    public void getLastTimeStopped() {
        holder = new ContentHolder(content);
        assertEquals(LAST_TIME_STOPPED, holder.getLastTimeStopped());
    }

    @Test
    public void getBinding() {
        holder = new ContentHolder(content);
        assertEquals(org.talend.esb.servicelocator.client.BindingType.JAXRS, holder.getBinding());
    }

    @Test
    public void getTransport() {
        holder = new ContentHolder(content);
        assertEquals(org.talend.esb.servicelocator.client.TransportType.HTTP, holder.getTransport());
    }

    @Test
    public void getProperties() {
        holder = new ContentHolder(content);

        SLProperties properties = holder.getProperties();
        assertTrue(properties.hasProperty(NAME_1));
        assertThat(properties.getValues(NAME_1), containsInAnyOrder(VALUE_1, VALUE_2));
    }

    @Test
    public void getAddressEmptyContent() throws Exception {
        holder = new ContentHolder(EMPTY_CONTENT);
        
        assertNull(holder.getAddress());
    }

    @Test
    public void getAddressInvalidContent() {
        holder = new ContentHolder(createContentInvalidEPR());
        
        assertNull(holder.getAddress());
    }

    @Test
    public void getPropertiesNotDefinedInContent() {
        content = createContent(ENDPOINT_1, LAST_TIME_STARTED, LAST_TIME_STOPPED, null);
        holder = new ContentHolder(content);
        
        SLProperties properties = holder.getProperties();
        assertThat(properties.getPropertyNames(), hasSize(0));
    }

    @Test
    public void getLastTimeStartedEmptyContent() {
        holder = new ContentHolder(EMPTY_CONTENT);

        assertEquals(-1, holder.getLastTimeStarted());
    }

    @Test
    public void getLastTimeStoppedEmptyContent() {
        holder = new ContentHolder(EMPTY_CONTENT);

        assertEquals(-1, holder.getLastTimeStopped());
    }
}
