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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.talend.esb.callcontext.store.common.exception.CallContextAlreadyExistsException;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.CallContextStoreException;
import org.talend.esb.callcontext.store.common.exception.IllegalParameterException;


public class CallContextStoreExceptionMapper implements ExceptionMapper<CallContextStoreException> {

    @Override
    public Response toResponse(CallContextStoreException e) {

        Status status;
        if (e instanceof IllegalParameterException) {
            status = Status.BAD_REQUEST;
        } else if (e instanceof CallContextNotFoundException) {
            status = Status.NOT_FOUND;
        } else if (e instanceof CallContextAlreadyExistsException) {
            status = Status.CONFLICT;
        } else {
            status = Status.INTERNAL_SERVER_ERROR;
        }

        return Response.status(status).type("text/plain").entity(e.getMessage()).build();
    }

}
