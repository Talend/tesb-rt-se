package org.talend.esb.auxiliary.storage.service.rest;


import org.junit.Assert;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.common.exception.IllegalParameterException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;

import javax.ws.rs.core.Response;


public class AuxiliaryStorageResponseExceptionMapperTest {

    private static final AuxiliaryStorageResponseExceptionMapper mapper = new AuxiliaryStorageResponseExceptionMapper();

    @Test
    public void testBadRequest() {
        Response response = Response.status(Response.Status.BAD_REQUEST).type("text/plain").entity("Very bad").build();
        Exception exception = mapper.fromResponse(response);
        Assert.assertNotNull(exception);
        Assert.assertEquals(IllegalParameterException.class, exception.getClass());
    }

    @Test
    public void testNotFound() {
        Response response = Response.status(Response.Status.NOT_FOUND).type("text/plain").entity("where is it?").build();
        Exception exception = mapper.fromResponse(response);
        Assert.assertNotNull(exception);
        Assert.assertEquals(ObjectNotFoundException.class, exception.getClass());
    }

    @Test
    public void testConflict() {
        Response response = Response.status(Response.Status.CONFLICT).type("text/plain").entity("argument that went bad").build();
        Exception exception = mapper.fromResponse(response);
        Assert.assertNotNull(exception);
        Assert.assertEquals(ObjectAlreadyExistsException.class, exception.getClass());
    }

    @Test
    public void testInternalServerError() {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type("text/plain").entity("500 ServletException").build();
        Exception exception = mapper.fromResponse(response);
        Assert.assertNotNull(exception);
        Assert.assertEquals(AuxiliaryStorageException.class, exception.getClass());
        Assert.assertEquals("Auxiliary storage error occured", exception.getMessage());
    }

    @Test
    public void testOther() {
        Response response = Response.status(Response.Status.GATEWAY_TIMEOUT).type("text/plain").entity("unknown").build();
        Exception exception = mapper.fromResponse(response);
        Assert.assertNotNull(exception);
        Assert.assertEquals(AuxiliaryStorageException.class, exception.getClass());
        Assert.assertEquals("An error occured", exception.getMessage());
    }
}
