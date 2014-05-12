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
package org.talend.esb.callcontext.store.client.rest;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.callcontext.store.client.common.CallContextStoreClient;
import org.talend.esb.callcontext.store.common.CallContextFactory;
import org.talend.esb.callcontext.store.common.exception.CallContextAlreadyExistsException;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.CallContextStoreException;
import org.talend.esb.callcontext.store.common.exception.IllegalParameterException;


public class CallContextStoreClientRest<E> extends AbstractCallContextStoreClientRest<E> implements CallContextStoreClient<E>  {

    private static final String CALL_PATH = "/callcontext/{contextKey}";
    
    CallContextFactory<E> factory;
    
    
    @Override
    public CallContextFactory<E> getCallContextFactory(){
 	   return this.factory;
    }   
    
    @Override
    public void setCallContextFactory(CallContextFactory<E> factory){
 	   this.factory = factory;
    } 
    
    private CallContextFactory<E> findCallContextFactory(){
 	if(getCallContextFactory()==null){
 		throw new IllegalParameterException("Call Context factory is null");
 	}
 	return getCallContextFactory();
    }    

    public CallContextStoreClientRest() {
        super();
    }
    
    @Override
    public E getCallContext(String contextKey) {
        WebClient client = getWebClient()
                .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
                .path(CALL_PATH, contextKey);

        String ctx = lookupCallContext(client);
        
        return findCallContextFactory().unmarshallCallContext(ctx);
    }

    @Override
    public void removeCallContext(String contextKey) {
    	
    	findCallContextFactory();
    	
        WebClient client = getWebClient()
                .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
                .path(CALL_PATH, contextKey);

        deleteCallContext(client);
    }

    @Override
    public String saveCallContext(E ctx) {
    	
    	String key = findCallContextFactory().createCallContextKey(ctx);
    	
        WebClient client = getWebClient()
                .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
                .path(CALL_PATH, key);
        
        try{
             client.put(findCallContextFactory().marshalCallContext(ctx));
             return key;
            }catch(WebApplicationException e){
            handleWebException(e);
        }finally {
            if (null != client) {
                client.reset();
            }
        }
        return null;
    }


   private String lookupCallContext(final WebClient client){
	   String callContext = null;
        try {
            callContext = client.get(String.class);
        } catch (NotFoundException e) {
            return null;
        } catch (WebApplicationException e) {
            handleWebException(e);
        } finally {
            if (null != client) {
                client.reset();
            }
        }

        return callContext;
    }

   private void deleteCallContext(final WebClient client){

       try {
           client.delete();
       } catch (NotFoundException e) {
           return;
       } catch (WebApplicationException e) {
           handleWebException(e);
       } finally {
           if (null != client) {
               client.reset();
           }
       }
   }


   /**
    * This method is supposed to be used as exception mapper
    * from <code>WebApplicationException</code>, sent in REST response,
    * to <code>CallContextStoreException</code>.
    *
    * @param exception Exception to convert from.
    */
   private void handleWebException(WebApplicationException  exception)  {

       Response response = exception.getResponse();
       if (response == null) {
           throw new CallContextStoreException("Mapping exception error: response is null");
       }

       int responseStatus = response.getStatus();

       if (Status.BAD_REQUEST.getStatusCode() == responseStatus) {
           throw new IllegalParameterException("Bad request server error");
       } else if (Status.NOT_FOUND.getStatusCode() == responseStatus) {
           throw new CallContextNotFoundException("Context not found");
       } else if (Status.CONFLICT.getStatusCode() == responseStatus) {
           throw new CallContextAlreadyExistsException("Context already exists");
       } else if (Status.INTERNAL_SERVER_ERROR.getStatusCode() == responseStatus) {
           throw new CallContextStoreException("Internal server error");
       } else {
           throw new CallContextStoreException("Unknown server error");
       }
   }
}
