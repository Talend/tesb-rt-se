package org.talend.esb.sam.service.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.talend.esb.sam.service.rest.exception.IllegalParameterException;
import org.talend.esb.sam.service.rest.exception.ResourceNotFoundException;
import org.talend.esb.sam.service.rest.exception.SamServiceException;

public class SAMExceptionMapper implements ExceptionMapper<SamServiceException> {

    @Override
    public Response toResponse(SamServiceException exception) {

        Status status;
        if (exception instanceof IllegalParameterException) {
            status = Status.BAD_REQUEST;
        } else if (exception instanceof ResourceNotFoundException) {
            status = Status.NOT_FOUND;
        } else {
            status = Status.INTERNAL_SERVER_ERROR;
        }

        return Response.status(status).type("text/plain").entity(exception.getMessage()).build();
    }

}
