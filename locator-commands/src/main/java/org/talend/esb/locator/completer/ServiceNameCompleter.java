/*
 * #%L
 * Service Locator Monitor
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
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

package org.talend.esb.locator.completer;

import java.util.List;

import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.talend.esb.locator.tracker.ServiceLocatorTracker;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class ServiceNameCompleter extends StringsCompleter {

    private ServiceLocatorTracker slt;

    public ServiceNameCompleter(ServiceLocator serviceLocator) {
        super(true);
        slt = ServiceLocatorTracker.getInstance(serviceLocator);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int complete(String buffer, int cursor, List candidates) {
        synchronized (getStrings()) {
            getStrings().clear();
            getStrings().addAll(slt.getServiceNames(false));
            return super.complete(buffer, cursor, candidates);
        }
    }

}
