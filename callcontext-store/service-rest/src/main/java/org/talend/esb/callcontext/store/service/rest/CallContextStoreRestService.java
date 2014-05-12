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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface CallContextStoreRestService {

    @GET
    @Path("")
    @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
    Response checkAlive();

    @GET
    @Produces({"application/xml","application/json"})
    @Path("/callcontext/{callContextKey}")
    String lookup(@PathParam("callContextKey") String callContextKey);

    @DELETE
    @Path("/callcontext/{callContextKey}")
    void remove(@PathParam("callContextKey") String callContextKey);

    @PUT
    @Path("/callcontext/{callContextKey}")
    @Consumes({"application/xml","application/json"})
    void put(String callContext, @PathParam("callContextKey") String callContextKey);

}