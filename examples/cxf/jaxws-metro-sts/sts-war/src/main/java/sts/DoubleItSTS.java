/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package sts;

import javax.annotation.Resource;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

@WebServiceProvider(serviceName = "DoubleItSTSService",
    portName = "IDoubleItSTSServiceUT_Port",
    targetNamespace = "http://tempuri.org/",
    wsdlLocation = "WEB-INF/wsdl/DoubleItSTS/DoubleItSTSService.wsdl")
@ServiceMode(value = Mode.PAYLOAD)
public class DoubleItSTS extends com.sun.xml.ws.security.trust.sts.BaseSTSImpl 
    implements Provider<Source> {
    
    @Resource
    WebServiceContext context;

    public Source invoke(Source rstElement) {
        return super.invoke(rstElement);
    }

    protected MessageContext getMessageContext() {
        MessageContext msgCtx = context.getMessageContext();
        return msgCtx;
    }
}

