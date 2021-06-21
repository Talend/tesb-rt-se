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
package org.talend.esb.servicelocator.client.internal.zk;

import static java.util.Arrays.asList;

import java.util.List;

import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;


/**
 * ZooKeeper ACL's to be used with the Service Locator
 *
 */
public class ServiceLocatorACLs {

    public static final String LOCATOR_SCHEME = "sl";

    public static final Id READ_ROLE = new Id(LOCATOR_SCHEME, "SL_READ");

    public static final Id MAINTAIN_ROLE = new Id(LOCATOR_SCHEME, "SL_MAINTAIN");

    public static final Id ADMIN_ROLE = new Id(LOCATOR_SCHEME, "SL_ADMIN");

    public static final ACL READ_ACL = new ACL(Perms.READ, READ_ROLE);

    public static final ACL MAINTAIN_ACL =
            new ACL(Perms.READ | Perms.CREATE | Perms.WRITE | Perms.DELETE, MAINTAIN_ROLE);

    public static final ACL ADMIN_ACL = new ACL(Perms.ALL, ADMIN_ROLE);

    public static final List<ACL> LOCATOR_ACLS = asList(READ_ACL, MAINTAIN_ACL, ADMIN_ACL);

    private ServiceLocatorACLs() {

    }
}
