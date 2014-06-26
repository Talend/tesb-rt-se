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
package org.talend.esb.auxiliary.storage.client.rest;

import java.net.ConnectException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.auxiliary.storage.client.common.AuxiliaryStorageClient;
import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.common.exception.IllegalParameterException;


public class AuxiliaryStorageClientRest<E> extends AbstractAuxiliaryStorageClientRest<E> implements AuxiliaryStorageClient<E>  {

    private static final String CALL_PATH = "/auxstorage/{key}";

    AuxiliaryObjectFactory<E> factory;


    @Override
    public AuxiliaryObjectFactory<E> getAuxiliaryObjectFactory(){
        return this.factory;
    }

    @Override
    public void setAuxiliaryObjectFactory(AuxiliaryObjectFactory<E> factory){
        this.factory = factory;
    }

    private AuxiliaryObjectFactory<E> findAuxiliaryObjectFactory(){
     if(getAuxiliaryObjectFactory()==null){
         throw new IllegalParameterException("Auxiliary factory is null");
     }
     return getAuxiliaryObjectFactory();
    }

    public AuxiliaryStorageClientRest() {
        super();
    }

    @Override
    public E getStoredObject(String key) {

        String ctx = lookupObject(key);
        return findAuxiliaryObjectFactory().unmarshallObject(ctx);
    }

    @Override
    public void removeStoredObject(String contextKey) {

        findAuxiliaryObjectFactory();
        deleteObject(contextKey);
    }

    @Override
    public String saveObject(E ctx) {

        String key = findAuxiliaryObjectFactory().createObjectKey(ctx);

        WebClient client = getWebClient()
                .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
                .path(CALL_PATH, key);

        try{
            Response resp = client.put(findAuxiliaryObjectFactory().marshalObject(ctx));
            if (resp.getStatus() == 404) {
                if (null != client) {
                    client.reset();
                }
                switchServerURL(client.getBaseURI().toString());
                return saveObject(ctx);
            }

            return key;
        } catch(WebApplicationException e){
            handleWebException(e);
        } catch (Throwable e) {
            if (e instanceof ConnectException
                    || e instanceof ClientException) {
                if (null != client) {
                    client.reset();
                }
                switchServerURL(client.getBaseURI().toString());
                return saveObject(ctx);
            }
        } finally {
            if (null != client) {
                client.reset();
            }
        }
        return null;
    }


   private String lookupObject(final String contextKey){

       WebClient client = getWebClient()
               .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
               .path(CALL_PATH, contextKey);


       String object = null;
        try {
            object = client.get(String.class);
        } catch (NotFoundException e) {
            return null;
        } catch (WebApplicationException e) {
            handleWebException(e);
        } catch (Throwable e) {
            if (e instanceof ConnectException
                    || e instanceof ClientException) {
                if (null != client) {
                    client.reset();
                }
                switchServerURL(client.getBaseURI().toString());
                return lookupObject(contextKey);
            }
        } finally {
            if (null != client) {
                client.reset();
            }
        }

        return object;
    }

   private void deleteObject(final String key){

       WebClient client = getWebClient()
               .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
               .path(CALL_PATH, key);

       try {
           client.delete();
       } catch (NotFoundException e) {
           return;
       } catch (WebApplicationException e) {
           handleWebException(e);
       } catch (Throwable e) {
           if (e instanceof ConnectException
                   || e instanceof ClientException) {
               if (null != client) {
                   client.reset();
               }
               switchServerURL(client.getBaseURI().toString());
               deleteObject(key);
           }
       } finally {
           if (null != client) {
               client.reset();
           }
       }
   }


   /**
    * This method is supposed to be used as exception mapper
    * from <code>WebApplicationException</code>, sent in REST response,
    * to <code>AuxiliaryStorageException</code>.
    *
    * @param exception Exception to convert from.
    */
   private void handleWebException(WebApplicationException  exception)  {

       Response response = exception.getResponse();
       if (response == null) {
           throw new AuxiliaryStorageException("Mapping exception error: response is null");
       }

       int responseStatus = response.getStatus();

       if (Status.BAD_REQUEST.getStatusCode() == responseStatus) {
           throw new IllegalParameterException("Bad request server error");
       } else if (Status.NOT_FOUND.getStatusCode() == responseStatus) {
           throw new ObjectNotFoundException("Object not found in auxiliary storage");
       } else if (Status.CONFLICT.getStatusCode() == responseStatus) {
           throw new ObjectAlreadyExistsException("Object already exists in auxiliary storage");
       } else if (Status.INTERNAL_SERVER_ERROR.getStatusCode() == responseStatus) {
           throw new AuxiliaryStorageException("Internal server error");
       } else {
           throw new AuxiliaryStorageException("Unknown server error");
       }
   }
}
