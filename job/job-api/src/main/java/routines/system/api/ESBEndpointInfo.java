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

import java.util.Map;

/**
 * Describes the generic Endpoint
 */
public interface ESBEndpointInfo {

    /**
     * Returns a component identifier that should be used
     * to be configured with given {@link ESBEndpointInfo}
     *
     * @return a non-null {@link String} unique for type of endpoint
     */
    String getEndpointKey();

    /**
     * Returns a URI String for the endpoint.
     * This URI should be understood by the consumer
     * with given {@link #getEndpointKey()}
     *
     * @return a non-null {@link String}, ideally a URI
     */
    String getEndpointUri();

    /**
     * Additional endpoint properties that
     * would be required to configure endpoint
     *
     * @return
     */
    Map<String, Object> getEndpointProperties();

}
