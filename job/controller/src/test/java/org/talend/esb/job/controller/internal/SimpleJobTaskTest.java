/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import routines.system.api.TalendJob;

public class SimpleJobTaskTest {

    public static final String NAME = "name";
    @Test
    public void taskRunInvokesJobRun() {
        TalendJob job = createMock(TalendJob.class);
        expect(job.runJobInTOS(aryEq(new String[0]))).andReturn(0);
        replay(job);

        SimpleJobTask task = new SimpleJobTask(job, NAME);
        task.run();
        verify(job);
    }

    @Test
    public void adminConfigurationPassedAsArguments() throws Exception {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("context", "contextValue");

        String[] args = new String[]{"--context=contextValue"};

        TalendJob job = createMock(TalendJob.class);
        expect(job.runJobInTOS(aryEq(args))).andReturn(0);
        replay(job);

        SimpleJobTask task = new SimpleJobTask(job, NAME);
        task.updated(properties);
        task.run();
        verify(job);
    }
}
