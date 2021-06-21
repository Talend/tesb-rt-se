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
 */package org.talend.esb.servicelocator.client.internal.zk;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.EndpointNode;
import org.talend.esb.servicelocator.client.internal.NodePath;
import org.talend.esb.servicelocator.client.internal.ServiceNode;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend.NodeMapper;

public class ServiceNodeImpl extends NodePath implements ServiceNode {

    private static final NodeMapper<String> ID_MAPPER = new NodeMapper<String>() {
        public  String map(String endpoint) {
            return endpoint;
        }
    };

    private ZKBackend zkBackend;

    public ServiceNodeImpl(ZKBackend backend, RootNodeImpl rootNode, QName serviceName) {
        super(rootNode, serviceName.toString());

        zkBackend = backend;
    }

    @Override
    public QName getServiceName() {
        return QName.valueOf(getNodeName());
    }

    public boolean exists() throws ServiceLocatorException, InterruptedException {
        return zkBackend.nodeExists(this);
    }

    public void ensureExists() throws ServiceLocatorException, InterruptedException {
        zkBackend.ensurePathExists(this, CreateMode.PERSISTENT);
    }

    public void ensureRemoved() throws ServiceLocatorException, InterruptedException {
        zkBackend.ensurePathDeleted(this, true);
    }

    public List<String> getEndpointNames() throws ServiceLocatorException, InterruptedException {
        return zkBackend.getChildren(this, ID_MAPPER);
    }

    public List<EndpointNode> getEndPoints() throws ServiceLocatorException, InterruptedException {
        NodeMapper<EndpointNode> toEndpointNode = new NodeMapper<EndpointNode>() {
            public  EndpointNode map(String endpoint) {
                return new EndpointNodeImpl(zkBackend, ServiceNodeImpl.this, endpoint);
            }
        };
        return zkBackend.getChildren(this, toEndpointNode);
    }

    public EndpointNode getEndPoint(String endpoint) {
        return new EndpointNodeImpl(zkBackend, this, endpoint);
    }

}
