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
package org.talend.esb.auxiliary.storage.service.rest;

import java.net.URI;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.talend.esb.auxiliary.storage.common.AuxiliaryStorageServer;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;


public class AuxiliaryStorageRestServiceImpl implements AuxiliaryStorageRestService {

    @Context
    private MessageContext messageContext;

    AuxiliaryStorageServer auxiliaryStorageServer;


    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(AuxiliaryStorageRestServiceImpl.class.getPackage()
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
        StringBuffer response = new StringBuffer("Talend Auxiliary Storage REST Service:\n")
            .append(" - wsdl - ").append(baseUri).append("/auxstorage/{key}.\n");
        return Response.ok(response.toString()).type(MediaType.TEXT_PLAIN).build();
    }

    @Override
    public String lookup(final String key){

        if(auxiliaryStorageServer == null){
            throw new AuxiliaryStorageException("Auxiliary Storage Server is not set");
        }

        String ctx = auxiliaryStorageServer.lookupObject(key);
        if(ctx == null){
            throw new ObjectNotFoundException("Can not find object with key {"
                    + key +"}");
        }

        return ctx;
    }

    @Override
    public void remove(String key) {
        auxiliaryStorageServer.deleteObject(key);
    }

    @Override
    public void put(final String object, final String key) {
        if(auxiliaryStorageServer == null){
            throw new AuxiliaryStorageException("Auxiliary Storage Server is not set");
        }
        auxiliaryStorageServer.saveObject(object, key);
    }


    public AuxiliaryStorageServer getAuxiliaryStorageServer() {
        return auxiliaryStorageServer;
    }


    public void setAuxiliaryStorageServer(AuxiliaryStorageServer auxiliaryStorageServer) {
        this.auxiliaryStorageServer = auxiliaryStorageServer;
    }

    public void disconnect(){
        //TODO:
    }
}
