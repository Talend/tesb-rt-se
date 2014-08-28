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
 */package org.talend.esb.servicelocator.client.internal.zk;

import java.nio.charset.Charset;
import java.util.Date;

import org.apache.zookeeper.CreateMode;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.EndpointNode;
import org.talend.esb.servicelocator.client.internal.NodePath;

public class EndpointNodeImpl extends NodePath implements EndpointNode {
    
    public static final String LIVE = "live";
    
    public static final String EXPIRES = "expires";
    
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    private ZKBackend zkBackend;
    
    public EndpointNodeImpl(ZKBackend backend, ServiceNodeImpl serviceNode, String endpointName) {
        super(serviceNode, endpointName.toString());
        
        zkBackend = backend;
    }
    
    @Override
    public String getEndpointName() {
        return getNodeName();
    }

    public boolean exists() throws ServiceLocatorException, InterruptedException {
        return zkBackend.nodeExists(this);
    }

    public void ensureExists(byte[] content) throws ServiceLocatorException, InterruptedException {
        zkBackend.ensurePathExists(this, CreateMode.PERSISTENT, content);
    }
    
    public void ensureRemoved() throws ServiceLocatorException, InterruptedException {
        zkBackend.ensurePathDeleted(child(LIVE), false);
        zkBackend.ensurePathDeleted(child(EXPIRES), false);
        zkBackend.ensurePathDeleted(this, true);        
    }

    public void setLive(boolean persistent) throws ServiceLocatorException, InterruptedException {
        CreateMode mode = persistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL;
        NodePath endpointStatusNodePath = child(LIVE);
        zkBackend.ensurePathExists(endpointStatusNodePath, mode);
    }

    public void setOffline() throws ServiceLocatorException, InterruptedException {
        NodePath endpointStatusNodePath = child(LIVE);
        NodePath expNodePath = child(EXPIRES);
        zkBackend.ensurePathDeleted(endpointStatusNodePath, false);
        zkBackend.ensurePathDeleted(expNodePath, false);
    }

    public byte[] getContent() throws ServiceLocatorException, InterruptedException {
        return zkBackend.getContent(this);
    }

    public void setContent(byte[] content) throws ServiceLocatorException, InterruptedException {
        zkBackend.setNodeData(this, content);
    }

    public boolean isLive() throws ServiceLocatorException, InterruptedException { 
        return zkBackend.nodeExists(child(LIVE));
    }
    
    @Override
    public Date getExpiryTime() throws ServiceLocatorException, InterruptedException {
        NodePath expNodePath = child(EXPIRES);
        
        if (!zkBackend.nodeExists(expNodePath)) {
            return null;
        }
        
        byte[] content = zkBackend.getContent(expNodePath);
        
        String strTime = new String(content, UTF8);
        
        return new Date(Long.valueOf(strTime));
    }
    
    @Override
    public void setExpiryTime(Date expiryTime, boolean persistent) throws ServiceLocatorException, InterruptedException {
        NodePath expNodePath = child(EXPIRES);
        
        String strTime = Long.toString(expiryTime.getTime());
        byte[] content = strTime.getBytes(UTF8);
        
        CreateMode mode = persistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL;
        zkBackend.ensurePathExists(expNodePath, mode, content);
    }
}
