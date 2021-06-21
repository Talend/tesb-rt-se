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
 * A factory interface to create to create specific kind of {@link TalendESBJob}. The factor allows the
 * Talend Runtime to create several instances of the job and to enable concurrent access.
 */
public interface TalendESBJobFactory {

    /**
     * Creates a new {@link TalendESBJob}. All instances returned must be different and of the same type.
     *
     * @return a new {@link ESBEndpointInfo} instance,  must not be <code>null</code>.
     */
    TalendESBJob newTalendESBJob();
}
