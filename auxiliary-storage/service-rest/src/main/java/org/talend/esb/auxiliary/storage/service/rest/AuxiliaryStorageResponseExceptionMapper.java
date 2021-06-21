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

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.common.exception.IllegalParameterException;

public class AuxiliaryStorageResponseExceptionMapper implements ResponseExceptionMapper<AuxiliaryStorageException> {

    @Override
    public AuxiliaryStorageException fromResponse(Response r) {

        if (Status.BAD_REQUEST.getStatusCode() == r.getStatus()) {
            return new IllegalParameterException("This is one");
        } else if (Status.NOT_FOUND.getStatusCode() == r.getStatus()) {
            return new ObjectNotFoundException("Object was not found in auxiliary storage");
        } else if (Status.CONFLICT.getStatusCode() == r.getStatus()) {
            return new ObjectAlreadyExistsException("Object already exists in auxiliary storage");
        } else if (Status.INTERNAL_SERVER_ERROR.getStatusCode() == r.getStatus()) {
            return new AuxiliaryStorageException("Auxiliary storage error occured");
        } else {
            return new AuxiliaryStorageException("An error occured");
        }
    }

}
