/*
 * #%L
 * Talend :: ESB :: Job :: API
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
package routines.system.api;

/**
 * A special interface for Talend ESB Route dedicated to allow
 * runtime to stop and shutdown running route
 *
 * @see org.apache.camel.ShutdownableService
 *
 * @author zubairov
 */
public interface TalendESBRoute extends TalendJob {

    /**
     * Stop the running route
     *
     * @throws Exception
     */
    void stop() throws Exception;

    /**
     * Shutdown the running route, which means it cannot be started again.
     *
     * @throws Exception
     */
    void shutdown() throws Exception;

}
