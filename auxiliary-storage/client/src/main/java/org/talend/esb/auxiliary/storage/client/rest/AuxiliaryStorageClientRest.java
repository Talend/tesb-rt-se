/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.auxiliary.storage.client.rest;

import java.net.ConnectException;
import java.util.Properties;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ProcessingException;
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


    public AuxiliaryStorageClientRest() {
        super();
    }

    public AuxiliaryStorageClientRest(Properties props) {
        super(props);
    }

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

    @Override
    public E getStoredObject(String key) {

        String ctx = lookupObject(key, true);
        return findAuxiliaryObjectFactory().unmarshallObject(ctx);
    }

    @Override
    public void removeStoredObject(String contextKey) {

        findAuxiliaryObjectFactory();
        deleteObject(contextKey, true);
    }

    @Override
    public String saveObject(E ctx) {
        return saveObject(ctx, true);
    }

    private String saveObject(final E ctx, final boolean retry) {

        AuxiliaryObjectFactory<E> auxObjectFactory = findAuxiliaryObjectFactory();
        String key = auxObjectFactory.createObjectKey(ctx);

        if (key == null || key.isEmpty()) {
            throw new IllegalParameterException("Object key can't be empty");
        }

        WebClient client = getWebClient()
                .type(auxObjectFactory.contentType())
                .path(CALL_PATH, key);

        try{
            Response resp = client.put(auxObjectFactory.marshalObject(ctx));
            int status = resp.getStatus();
            if (status >= 400) {
                if (status == 409) {
                    // HTTP conflict is considered as "call context is saved already".
                    return key;
                }
                if (!retry) {
                    if (status == 404) {
                        return null;
                    }
                    throw new IllegalStateException("Upload failed with HTTP status " + status);
                }
                if (null != client) {
                    client.reset();
                }
                switchServerURL(client.getBaseURI().toString());
                return saveObject(ctx, false);
            }
            return key;
        } catch(WebApplicationException e){
            handleWebException(e);
        } catch (Exception e) {
            if (retry && (e instanceof ConnectException
                    || e instanceof ProcessingException)) {
                if (null != client) {
                    client.reset();
                }
                switchServerURL(client.getBaseURI().toString(), e);
                return saveObject(ctx);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new IllegalStateException("Upload failed: ", e);
        } finally {
            if (null != client) {
                client.reset();
            }
        }
        return null;
    }

   private String lookupObject(final String contextKey, final boolean retry) {

       if (contextKey == null || contextKey.isEmpty()) {
           throw new IllegalParameterException("Object key can't be empty");
       }

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
        } catch (Exception e) {
            if (retry && (e instanceof ConnectException
                    || e instanceof ProcessingException)) {
                if (null != client) {
                    client.reset();
                }
                switchServerURL(client.getBaseURI().toString(), e);
                return lookupObject(contextKey, false);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new IllegalStateException("Lookup failed: ", e);
        } finally {
            if (null != client) {
                client.reset();
            }
        }

        return object;
    }

   private void deleteObject(final String key, final boolean retry) {

       if (key == null || key.isEmpty()) {
           throw new IllegalParameterException("Object key can't be empty");
       }

       WebClient client = getWebClient()
               .accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON)
               .path(CALL_PATH, key);

       try {
           client.delete();
       } catch (NotFoundException e) {
           return;
       } catch (WebApplicationException e) {
           handleWebException(e);
       } catch (Exception e) {
           if (retry && (e instanceof ConnectException
                   || e instanceof ProcessingException)) {
               if (null != client) {
                   client.reset();
               }
               switchServerURL(client.getBaseURI().toString(), e);
               deleteObject(key, false);
           }
           if (e instanceof RuntimeException) {
               throw (RuntimeException) e;
           }
           throw new IllegalStateException("Delete failed: ", e);
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
