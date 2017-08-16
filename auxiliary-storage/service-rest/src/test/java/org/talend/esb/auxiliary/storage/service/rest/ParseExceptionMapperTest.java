package org.talend.esb.auxiliary.storage.service.rest;


import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class ParseExceptionMapperTest {

    @Test
    public void testMapper() {
        ParseExceptionMapper mapper = new ParseExceptionMapper();
        RuntimeException exception = new RuntimeException("Strange thing happened during the test");
        Response response = mapper.toResponse(exception);
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("text", response.getMediaType().getType());
        Assert.assertEquals("plain", response.getMediaType().getSubtype());
        Assert.assertEquals("Strange thing happened during the test", response.getEntity().toString());
    }
}
