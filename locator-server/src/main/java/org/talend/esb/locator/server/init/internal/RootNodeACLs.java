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
package org.talend.esb.locator.server.init.internal;

import static java.util.Arrays.asList;

import java.util.List;

import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;


/**
 * ZooKeeper ACL's to be used with the Service Locator
 *
 */
public class RootNodeACLs {

    public static final String LOCATOR_SCHEME = "sl";

    public static final Id READ_ROLE = new Id(LOCATOR_SCHEME, "SL_READ");

    public static final Id MAINTAIN_ROLE = new Id(LOCATOR_SCHEME, "SL_MAINTAIN");

    public static final Id ADMIN_ROLE = new Id(LOCATOR_SCHEME, "SL_ADMIN");

    public static final ACL READ_ACL = new ACL(Perms.READ, READ_ROLE);

    public static final ACL MAINTAIN_LOCATOR_ROOT_ACL =
            new ACL(Perms.READ | Perms.CREATE | Perms.DELETE, MAINTAIN_ROLE);

    public static final ACL WORLD_ZK_ROOT_ACL =
            new ACL(Perms.READ | Perms.CREATE | Perms.WRITE, Ids.ANYONE_ID_UNSAFE);

    public static final ACL ADMIN_ACL = new ACL(Perms.ALL, ADMIN_ROLE);

    public static final List<ACL> LOCATOR_ROOT_ACLS = asList(READ_ACL, MAINTAIN_LOCATOR_ROOT_ACL, ADMIN_ACL);

    public static final List<ACL> ZK_ROOT_ACLS = asList(WORLD_ZK_ROOT_ACL, ADMIN_ACL);

    private RootNodeACLs() {

    }
}
