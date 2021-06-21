/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.event;

/**
 * The EventTypeEnum used to define event type.
 */
public enum EventTypeEnum {
    REQ_IN,
    REQ_OUT,
    RESP_IN,
    RESP_OUT,
    FAULT_IN,
    FAULT_OUT,
    SERVER_START,
    SERVER_STOP,
    SERVICE_START,
    SERVICE_STOP,
    CLIENT_CREATE,
    CLIENT_DESTROY,
    JOB_START,
    JOB_STOP,
    LOG,
    UNKNOWN;
}
