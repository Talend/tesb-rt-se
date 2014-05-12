/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.callcontext.store.service.rest;

import java.net.URI;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.talend.esb.callcontext.store.common.CallContextStoreServer;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.CallContextStoreException;


public class CallContextStoreRestServiceImpl implements CallContextStoreRestService {
	
    @Context
    private MessageContext messageContext;
	
	CallContextStoreServer callContextStoreServer;
	

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CallContextStoreRestServiceImpl.class.getPackage()
        .getName());

	
    @Override
    public Response checkAlive() {
        for (MediaType acceptedType : messageContext.getHttpHeaders().getAcceptableMediaTypes()) {
            if (!acceptedType.isWildcardType() && !acceptedType.isWildcardSubtype()
                && MediaType.TEXT_HTML_TYPE.isCompatible(acceptedType)) {
                return Response.ok(getClass().getResourceAsStream("/index.html"),
                                   MediaType.TEXT_HTML_TYPE).build();
            }
        }

        URI baseUri = messageContext.getUriInfo().getBaseUriBuilder().build();
        StringBuffer response = new StringBuffer("Talend Call Context Store REST Service:\n")
            .append(" - wsdl - ").append(baseUri).append("/callcontext/{callContextKey}.\n");
        return Response.ok(response.toString()).type(MediaType.TEXT_PLAIN).build();
    }	

	@Override
	public String lookup(final String callContextKey){
		
		if(callContextStoreServer == null){
			throw new CallContextStoreException("Call Context Store Server is not set");
		}
		
		String ctx = callContextStoreServer.lookupCallContext(callContextKey);
		if(ctx == null){
			throw new CallContextNotFoundException("Can not find Call Context with key {"
					+ callContextKey +"}");
		}
		return ctx;		
	}

	@Override
	public void remove(String callContextKey) {
		callContextStoreServer.deleteCallContext(callContextKey);
	}

	@Override
	public void put(final String callContext, final String key) {
		if(callContextStoreServer == null){
			throw new CallContextStoreException("Call Context Store Server is not set");
		}
		callContextStoreServer.saveCallContext(callContext, key);
	}
	
	public CallContextStoreServer getCallContextStoreServer() {
		return callContextStoreServer;
	}

	public void setCallContextStoreServer(
			CallContextStoreServer callContextStoreServer) {
		this.callContextStoreServer = callContextStoreServer;
	}
	
	public void disconnect(){
		//TODO:
	}
}
