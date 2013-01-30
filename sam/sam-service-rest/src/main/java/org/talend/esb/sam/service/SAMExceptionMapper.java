package org.talend.esb.sam.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.talend.esb.sam.service.exception.SamServiceException;
import org.talend.esb.sam.service.exception.IllegalParameterException;
import org.talend.esb.sam.service.exception.ResourceNotFoundException;

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
