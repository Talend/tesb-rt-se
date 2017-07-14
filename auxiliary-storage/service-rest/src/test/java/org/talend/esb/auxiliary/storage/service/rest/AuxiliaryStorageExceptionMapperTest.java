package org.talend.esb.auxiliary.storage.service.rest;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.common.exception.IllegalParameterException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;

import javax.ws.rs.core.Response;

public class AuxiliaryStorageExceptionMapperTest {

    private static  AuxiliaryStorageExceptionMapper mapper;

    @BeforeClass
    public static void init() {
        mapper = new AuxiliaryStorageExceptionMapper();
    }

    @Test
    public void testIllegalParameter() {
        Response response = mapper.toResponse(new IllegalParameterException("This parameter is illegal!"));
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("text", response.getMediaType().getType());
        Assert.assertEquals("plain", response.getMediaType().getSubtype());
        Assert.assertEquals("This parameter is illegal!", response.getEntity().toString());
    }

    @Test
    public void testObjectNotFound() {
        Response response = mapper.toResponse(new ObjectNotFoundException("Object not found"));
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("text", response.getMediaType().getType());
        Assert.assertEquals("plain", response.getMediaType().getSubtype());
        Assert.assertEquals("Object not found", response.getEntity().toString());
    }

    @Test
    public void testAlreadyExists() {
        Response response = mapper.toResponse(new ObjectAlreadyExistsException("Already exists"));
        Assert.assertNotNull(response);
        Assert.assertEquals(409, response.getStatus());
        Assert.assertEquals("text", response.getMediaType().getType());
        Assert.assertEquals("plain", response.getMediaType().getSubtype());
        Assert.assertEquals("Already exists", response.getEntity().toString());
    }

    @Test
    public void testInternalError() {
        Response response = mapper.toResponse(new AuxiliaryStorageException("Totally out of order"));
        Assert.assertNotNull(response);
        Assert.assertEquals(500, response.getStatus());
        Assert.assertEquals("text", response.getMediaType().getType());
        Assert.assertEquals("plain", response.getMediaType().getSubtype());
        Assert.assertEquals("Totally out of order", response.getEntity().toString());
    }
}
