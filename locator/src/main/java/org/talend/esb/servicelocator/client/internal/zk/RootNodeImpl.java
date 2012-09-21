/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.servicelocator.client.internal.zk;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.NodePath;
import org.talend.esb.servicelocator.client.internal.RootNode;
import org.talend.esb.servicelocator.client.internal.ServiceNode;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend.NodeMapper;

public class RootNodeImpl extends NodePath implements RootNode {

    private static final String ROOT_NODE_PATH = "cxf-locator";

    private static final NodeMapper<QName> TO_SERVICE_NAME = new NodeMapper<QName>() {
        @Override
        public QName map(String nodeName) {
            return QName.valueOf(nodeName);
        }
    };

    private ZKBackend zkBackend;
    
    public RootNodeImpl(ZKBackend backend) {
        super(ROOT_NODE_PATH);
        zkBackend = backend;
    }

    public boolean exists() throws ServiceLocatorException, InterruptedException {
        return zkBackend.nodeExists(this);
    }

    public void ensureExists() throws ServiceLocatorException, InterruptedException {
        zkBackend.ensurePathExists(this, CreateMode.PERSISTENT);
    }

    public ServiceNode getServiceNode(QName serviceName) {
        return new ServiceNodeImpl(zkBackend, this, serviceName);
    }
    
    public List<QName> getServiceNames()  throws ServiceLocatorException, InterruptedException {
        return zkBackend.getChildren(this, TO_SERVICE_NAME);
    }

}
