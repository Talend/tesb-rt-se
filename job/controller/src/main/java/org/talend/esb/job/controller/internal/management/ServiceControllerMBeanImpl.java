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
package org.talend.esb.job.controller.internal.management;

import java.util.List;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.talend.esb.job.controller.Controller;
import org.talend.esb.job.controller.management.ControllerMBean;

public class ServiceControllerMBeanImpl extends StandardMBean implements ControllerMBean {

    private Controller controller;

    public ServiceControllerMBeanImpl() throws NotCompliantMBeanException {
        super(ControllerMBean.class);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public List<String> list() throws Exception {
        return controller.listServices();
    }
}
