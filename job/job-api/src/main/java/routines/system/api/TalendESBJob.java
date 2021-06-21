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
 * A JOB interface for Jobs that are using tESB Components
 */
public interface TalendESBJob extends TalendJob {

    /**
     * Returns {@link ESBEndpointInfo} instance
     * that describes the endpoint implemented by given Job.
     *
     * This method should return <code>null</code> if given Job
     * does not have any tESB provider component.
     *
     * @return {@link ESBEndpointInfo} or null if no provider is configured for the Job
     */
    ESBEndpointInfo getEndpoint();

    /**
     * Injecting a {@link ESBEndpointRegistry} to allow
     * tESB Consumer components to lookup and call ESB providers.
     *
     * @param callback
     */
    void setEndpointRegistry(ESBEndpointRegistry registry);

    /**
     * Injecting a {@link ESBProviderCallback} to allow
     * tESB Provider components read requests sent to the
     * {@link Job} and write responses from the {@link Job}
     *
     * @param callback
     */
    void setProviderCallback(ESBProviderCallback callback);

}
