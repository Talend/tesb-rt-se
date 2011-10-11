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

import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.handler.MessageContext;

import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

//@javax.jws.WebService(name = "TalendJobAsWebService", targetNamespace = "http://talend.org/esb/service/job")
//@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE,
//style = javax.jws.soap.SOAPBinding.Style.DOCUMENT, use = javax.jws.soap.SOAPBinding.Use.LITERAL)
@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.PAYLOAD)
@javax.xml.ws.WebServiceProvider()
public abstract class ESBProviderBase implements
    javax.xml.ws.Provider<javax.xml.transform.Source> {
    
    public static final String REQUEST_PAYLOAD = "PAYLOAD";

    public static final String REQUEST_SAM_PROPS = "SAM-PROPS";

    private static final Logger LOG = Logger.getLogger(ESBProviderBase.class
            .getName());


    protected EventFeature eventFeature;

    @javax.annotation.Resource
    private javax.xml.ws.WebServiceContext context;

    private boolean isAuthenticationRequired;

    public void setEventFeature(EventFeature eventFeature) {
        this.eventFeature = eventFeature;
    }

    // @javax.jws.WebMethod(exclude=true)
    public final Source invoke(Source request) {

        if (isAuthenticationRequired) {
            login();
        }

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
                .get(REQUEST_SAM_PROPS);
                if (samProps != null && eventFeature != null) {
                    LOG.info("SAM custom properties received: " + samProps);
                    CustomInfoHandler ciHandler = new CustomInfoHandler();
                    ciHandler.setCustomInfo(samProps);
                    eventFeature.setHandler(ciHandler);
                }

                return processResult(map.get(REQUEST_PAYLOAD));
            } else {
                return processResult(result);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void login() {

        LoginContext loginContext = null;

        try {
            loginContext = new LoginContext("KarafJaas",
                    new ESBCallbackHandler());
        } catch (LoginException le) {
            System.err
            .println("Cannot create LoginContext. " + le.getMessage());
        } catch (SecurityException se) {
            System.err
            .println("Cannot create LoginContext. " + se.getMessage());
        }

        try {
            loginContext.login();
        } catch (LoginException le) {
            System.err.println("Authentication failed: ");
            System.err.println("  " + le.getMessage());
        }

        System.out.println("Authentication succeeded!");

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

    public abstract GenericOperation getESBProviderCallback(String operationName);

    protected boolean isOperationRequestResponse(String operationName) {
        // is better way to get communication style?
        return null != context.getMessageContext().get(
                MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
    }

}
