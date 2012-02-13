/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.example.customerservice.NoSuchCustomerException;

public class NoCustomerExceptionMapper implements ExceptionMapper<NoSuchCustomerException> {

    @Override
    public Response toResponse(NoSuchCustomerException ex) {
        return Response.noContent().build();
    }

}
