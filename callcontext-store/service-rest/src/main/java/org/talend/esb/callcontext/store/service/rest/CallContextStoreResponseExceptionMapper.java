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

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.talend.esb.callcontext.store.common.exception.CallContextAlreadyExistsException;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.CallContextStoreException;
import org.talend.esb.callcontext.store.common.exception.IllegalParameterException;

public class CallContextStoreResponseExceptionMapper implements ResponseExceptionMapper<CallContextStoreException> {

    @Override
    public CallContextStoreException fromResponse(Response r) {

        if (Status.BAD_REQUEST.getStatusCode() == r.getStatus()) {
            return new IllegalParameterException("This is one");
        } else if (Status.NOT_FOUND.getStatusCode() == r.getStatus()) {
            return new CallContextNotFoundException("Call context was not found");
        } else if (Status.CONFLICT.getStatusCode() == r.getStatus()) {
            return new CallContextAlreadyExistsException("Call context already exists");
        } else if (Status.INTERNAL_SERVER_ERROR.getStatusCode() == r.getStatus()) {
            return new CallContextStoreException("Call context store error occured");
        } else {
            return new CallContextStoreException("An error occured");
        }
    }

}
