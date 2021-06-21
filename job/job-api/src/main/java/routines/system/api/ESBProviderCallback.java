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
 * This interface is used by provider component
 * to get a request from the ESB
 * and to write a response back to ESB.
 */
public interface ESBProviderCallback {

    String HEADERS_SOAP = "HEADERS_SOAP";

    String HEADERS_HTTP = "HEADERS_HTTP";

    String REQUEST = "REQUEST";

    /**
     * Returns a request to the Job.
     * This method is <em>blocking</em> it will
     * block Job execution until request will arrive.
     *
     * @return
     */
    Object getRequest() throws ESBJobInterruptedException;

    /**
     * This method will be used by Job to send
     * a response or fault.
     *
     * @param response
     */
    void sendResponse(Object response);

}
