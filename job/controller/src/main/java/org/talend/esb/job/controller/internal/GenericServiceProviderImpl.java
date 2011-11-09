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
package org.talend.esb.job.controller.internal;

import java.util.Dictionary;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.handler.MessageContext;

import org.osgi.service.cm.ConfigurationException;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.GenericServiceProvider;
import org.talend.esb.job.controller.JobLauncher;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

@javax.xml.ws.WebServiceProvider()
@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.PAYLOAD)
public class GenericServiceProviderImpl implements GenericServiceProvider,
        javax.xml.ws.Provider<javax.xml.transform.Source> {
    private static final Logger LOG = Logger.getLogger(GenericServiceProviderImpl.class.getName());

    private final JobLauncher jobLauncher;
    private final Map<String, String> operations;

    private EventFeature eventFeature;

    private Configuration configuration;

    @javax.annotation.Resource
    private javax.xml.ws.WebServiceContext context;

    public GenericServiceProviderImpl(final JobLauncher jobLauncher, final Map<String, String> operations) {
        this.jobLauncher = jobLauncher;
        this.operations = operations;
    }

    public void setEventFeature(EventFeature eventFeature) {
        this.eventFeature = eventFeature;
    }

    // @javax.jws.WebMethod(exclude=true)
    public final Source invoke(Source request) {
        QName operationQName = (QName) context.getMessageContext().get(
                MessageContext.WSDL_OPERATION);
        LOG.info("Invoke operation '" + operationQName + "'");
        GenericOperation esbProviderCallback = getESBProviderCallback(operationQName
                .getLocalPart());
        if (esbProviderCallback == null) {
            throw new RuntimeException("Handler for operation "
                    + operationQName + " cannot be found");
        }
        try {
            Object result = esbProviderCallback.invoke(
                    DOM4JMarshaller.sourceToDocument(request),
                    isOperationRequestResponse(operationQName.getLocalPart()));

            // oneway
            if (result == null) {
                return null;
            }

            if (result instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result;

                @SuppressWarnings("unchecked")
                Map<String, String> samProps = (Map<String, String>) map
                        .get(ESBEndpointConstants.REQUEST_SAM_PROPS);
                if (samProps != null && eventFeature != null) {
                    LOG.info("SAM custom properties received: " + samProps);
                    CustomInfoHandler ciHandler = new CustomInfoHandler();
                    ciHandler.setCustomInfo(samProps);
                    eventFeature.setHandler(ciHandler);
                }

                return processResult(map.get(ESBEndpointConstants.REQUEST_PAYLOAD));
            } else {
                return processResult(result);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        configuration = new Configuration(properties);
    }

    private Source processResult(Object result) {
        if (result instanceof org.dom4j.Document) {
            return DOM4JMarshaller
                    .documentToSource((org.dom4j.Document) result);
        } else if (result instanceof RuntimeException) {
            throw (RuntimeException) result;
        } else if (result instanceof Throwable) {
            throw new RuntimeException((Throwable) result);
        } else {
            throw new RuntimeException("Provider return incompatible object: "
                    + result.getClass().getName());
        }
    }

    private GenericOperation getESBProviderCallback(String operationName) {
        final String jobName = operations.get(operationName);
        if (jobName == null) {
            throw new IllegalArgumentException(
                    "Job for operation '" + operationName + "' not found");
        }

        final GenericOperation operation = jobLauncher.retrieveOperation(
            jobName, configuration.getArguments());

        return operation;
    }

    protected boolean isOperationRequestResponse(String operationName) {
        // is better way to get communication style?
        return (null != context.getMessageContext().get(
                MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS));
    }

}
