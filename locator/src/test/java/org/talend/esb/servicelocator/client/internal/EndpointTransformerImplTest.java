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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.*;

import java.util.List;

import javax.xml.transform.dom.DOMResult;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EndpointTransformerImplTest {

    @Test
    public void writePropertiesToPropertiesEmpty() throws Exception {
        
        EndpointTransformerImpl transformer = new EndpointTransformerImpl();
        DOMResult result = new DOMResult();
        transformer.writePropertiesTo(PROPERTIES_EMPTY, result);

        Document doc = (Document) result.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root, 
            hasXPath("/sl:ServiceLocatorProperties",
                WSA_SL_NS_CONTEXT));
        assertThat(root, 
                hasXPath("/sl:ServiceLocatorProperties/node()",
                    WSA_SL_NS_CONTEXT, equalTo("")));
    }

//    @Test
    public void getEndpointsEndpointIsNotLive() throws Exception {
    }
}
