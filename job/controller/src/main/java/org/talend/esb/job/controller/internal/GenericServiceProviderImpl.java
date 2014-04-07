/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.ServiceModelUtil;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.dom4j.io.SAXReader;
import org.osgi.service.cm.ConfigurationException;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.GenericServiceProvider;
import org.talend.esb.job.controller.JobLauncher;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;
import org.xml.sax.SAXException;

import routines.system.api.ESBProviderCallback;

@javax.xml.ws.WebServiceProvider()
@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.PAYLOAD)
public class GenericServiceProviderImpl implements GenericServiceProvider,
        javax.xml.ws.Provider<javax.xml.transform.Source> {
    private static final Logger LOG = Logger.getLogger(GenericServiceProviderImpl.class.getName());

    private final JobLauncher jobLauncher;
    private final Map<String, String> operations;

    private EventFeature eventFeature;
    private boolean extractHeaders;

    private Configuration configuration;

    @javax.annotation.Resource
    private javax.xml.ws.WebServiceContext context;

    public GenericServiceProviderImpl(final JobLauncher jobLauncher, final Map<String, String> operations) {
        this.jobLauncher = jobLauncher;
        this.operations = operations;
        configuration = new Configuration();
    }

    public void setEventFeature(EventFeature eventFeature) {
        this.eventFeature = eventFeature;
    }

    public void setExtractHeaders(boolean extractHeaders) {
        this.extractHeaders = extractHeaders;
    }

    // @javax.jws.WebMethod(exclude=true)
    public final Source invoke(Source request) {
        QName operationQName = (QName) context.getMessageContext().get(
                MessageContext.WSDL_OPERATION);
        LOG.info("Invoke operation '" + operationQName + "'");
        GenericOperation esbProviderCallback = 
             getESBProviderCallback(operationQName.getLocalPart());
        
        if (esbProviderCallback == null) {
            throw new RuntimeException("Handler for operation "
                    + operationQName + " cannot be found");
        }
        try {
            ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
            StaxUtils.copy(request, os);
            org.dom4j.Document requestDoc = new SAXReader()
                    .read(new ByteArrayInputStream(os.toByteArray()));

            //workaround for CXF-5169
            MessageContext mContext = context.getMessageContext();
            WrappedMessageContext wmc = (WrappedMessageContext)mContext;
            Message message = wmc.getWrappedMessage();
            Object validationObj = message.getContextualProperty(Message.SCHEMA_VALIDATION_ENABLED);
            boolean shouldValidate = false;
            if (validationObj instanceof String) {
                String validationStr = (String)validationObj;
                shouldValidate = validationStr.equalsIgnoreCase("true") || validationStr.equalsIgnoreCase("OUT");
            } else if (validationObj instanceof SchemaValidationType) {
                SchemaValidationType validationType = (SchemaValidationType)validationObj;
                shouldValidate = (validationType == SchemaValidationType.BOTH || 
                        validationType == SchemaValidationType.OUT);
            }

            Schema schema = null;
            if (shouldValidate) {
                Service service = ServiceModelUtil.getService(message.getExchange());
                schema = EndpointReferenceUtils.getSchema(service.getServiceInfos().get(0),
                        message.getExchange().getBus());
            }

            if (null != schema) {
                Source source = DOM4JMarshaller.documentToSource((org.dom4j.Document) requestDoc);
                schema.newValidator().validate(source);
            }

            Object payload;
            if (extractHeaders) {
                Map<String, Object> esbRequest = new HashMap<String, Object>();
                esbRequest.put(ESBProviderCallback.HEADERS_SOAP, context.getMessageContext().get(Header.HEADER_LIST));
                esbRequest.put(ESBProviderCallback.HEADERS_HTTP, context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS));
                esbRequest.put(ESBProviderCallback.REQUEST, requestDoc);
                esbRequest.put(CorrelationIDFeature.MESSAGE_CORRELATION_ID, context.getMessageContext().get(CorrelationIDFeature.MESSAGE_CORRELATION_ID));
                payload = esbRequest;
            } else {
                payload = requestDoc;
            }
            Object result = esbProviderCallback.invoke(payload,
                    isOperationRequestResponse(operationQName.getLocalPart()));

            // oneway
            if (result == null) {
                return null;
            }

            if (result instanceof Map<?, ?>) {
                Map<String, Object> map = CastUtils.cast((Map<?, ?>) result);

                Map<String, String> samProps = CastUtils.cast((Map<?, ?>) map
                        .get(ESBEndpointConstants.REQUEST_SAM_PROPS));
                if (samProps != null && eventFeature != null) {
                    LOG.info("SAM custom properties received: " + samProps);
                    CustomInfoHandler ciHandler = new CustomInfoHandler();
                    ciHandler.setCustomInfo(samProps);
                    eventFeature.setHandler(ciHandler);
                }

                return processResult(map.get(ESBEndpointConstants.REQUEST_PAYLOAD), schema);
            } else {
                return processResult(result, schema);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        configuration.setProperties(properties);
    }

    private Source processResult(Object result, Schema schema) {
    	Source source = null;
        if (result instanceof org.dom4j.Document) {
        	try {
	            //workaround for CXF-5169
                if (null != schema) {
                    source = DOM4JMarshaller.documentToSource((org.dom4j.Document) result);
                    schema.newValidator().validate(source);
                    source = null;
                }

			    source = DOM4JMarshaller.documentToSource((org.dom4j.Document) result);

			} catch (org.dom4j.DocumentException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (SAXException e) {
				throw new RuntimeException(e);
			}
        } else if (result instanceof RuntimeException) {
            throw (RuntimeException) result;
        } else if (result instanceof Throwable) {
            throw new RuntimeException((Throwable) result);
        } else {
            throw new RuntimeException("Provider return incompatible object: "
                    + result.getClass().getName());
        }
        return source;
    }

    private GenericOperation getESBProviderCallback(String operationName) {
        final String jobName = operations.get(operationName);
        if (jobName == null) {
            throw new IllegalArgumentException(
                    "Job for operation '" + operationName + "' not found");
        }

        String[] args;
        try {
            args = configuration.awaitArguments();
        } catch (InterruptedException e) {
            throw new RuntimeException(
                    "Request was interrupted when waiting for the configuration parameters.",
                    e);
        }
        return jobLauncher.retrieveOperation(jobName, args);
    }

    private boolean isOperationRequestResponse(String operationName) {
        // is better way to get communication style?
        return null != context.getMessageContext().get(
                MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
    }

}
