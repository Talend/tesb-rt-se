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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.common.exception.IllegalParameterException;


public class AuxiliaryStorageExceptionMapper implements ExceptionMapper<AuxiliaryStorageException> {

    @Override
    public Response toResponse(AuxiliaryStorageException e) {

        Status status;
        if (e instanceof IllegalParameterException) {
            status = Status.BAD_REQUEST;
        } else if (e instanceof ObjectNotFoundException) {
            status = Status.NOT_FOUND;
        } else if (e instanceof ObjectAlreadyExistsException) {
            status = Status.CONFLICT;
        } else {
            status = Status.INTERNAL_SERVER_ERROR;
        }

        return Response.status(status).type("text/plain").entity(e.getMessage()).build();
    }

}
