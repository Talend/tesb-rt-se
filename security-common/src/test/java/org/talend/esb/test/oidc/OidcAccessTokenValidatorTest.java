package org.talend.esb.test.oidc;

import static org.junit.Assert.*;


import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.impl.ContainerRequestContextImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.security.oidc.OidcAccessTokenValidator;
import org.talend.esb.security.oidc.OidcClientUtils;
import org.talend.esb.security.oidc.OidcConfiguration;
import org.talend.esb.test.oidc.internal.OidcTokenService;
import org.talend.esb.test.oidc.internal.OidcValidationService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OidcAccessTokenValidatorTest {

    private static final String AUTHORIZATION = "Authorization";

    private static OidcAccessTokenValidator validator;
    private static Server server = null;
    private static Message msg;
    private static Map<String, List<Object>> headersMap;

    @BeforeClass
    public static void init() {
        startValidationService();
        createValidator();
        createMessage();
    }

    @AfterClass
    public static void stopValidationService() {
        if (server != null) {
            try {
                server.destroy();
            } catch (Throwable t) {}
        }
    }

    @After
    public void cleanHeaders() {
        headersMap.clear();
    }

    private static void startValidationService() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(OidcValidationService.class);
        sf.setAddress(OidcValidationService.ENDPOINT);
        sf.create();
        server = sf.getServer();
    }

    private static void createValidator() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, OidcTokenService.ENDPOINT);
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, OidcValidationService.ENDPOINT);
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "aFSloIZSXHRQtA");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");

        new OidcClientUtils(map);
        validator = new OidcAccessTokenValidator();
    }

    private static void createMessage() {
        Exchange  exchange = new ExchangeImpl();
        exchange.put("jaxrs.filter.properties", new HashMap<String, String>());

        msg = new MessageImpl();
        msg.setExchange(exchange);

        headersMap = new HashMap<String, List<Object>>();
        msg.put(Message.PROTOCOL_HEADERS,headersMap);
    }

    @Test
    public void testNegative1() {
        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        try {
            validator.filter(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(ctx.isAborted());
    }

    @Test
    public void testNegative2() {
        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Not a Bearer");
        try {
            validator.filter(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(ctx.isAborted());
    }

    @Test
     public void testNegative3() {
        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer ");
        try {
            validator.filter(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(ctx.isAborted());
    }

    @Test(expected = RuntimeException.class)
    public void testNegative4() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, OidcTokenService.ENDPOINT);
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, null);
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "aFSloIZSXHRQtA");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");

        OidcAccessTokenValidator validator2 = new OidcAccessTokenValidator(new OidcConfiguration(map));

        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer  1234-5444");

        validator2.filter(ctx);
    }

    @Test
    public void testInvalidToken() {
        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer invalid_token_234");
        try {
            validator.filter(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(ctx.isAborted());
    }

    @Test
    public void testValidToken() {
        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer valid_token");
        try {
            validator.filter(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertFalse(ctx.isAborted());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidRespone() throws Exception{
        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer invalid_json");

        validator.filter(ctx);
    }



    private void setAuthzHeader(String headerValue) {
        List<Object> l = new LinkedList<Object>();
        l.add(headerValue);
        headersMap.put(AUTHORIZATION, l);
    }


    private class ContainerRequestContextTest extends ContainerRequestContextImpl {

        private boolean aborted = false;

        public ContainerRequestContextTest() {
            super(msg, false, false);
        }

        @Override
        public void abortWith(Response response) {
            aborted = true;
        }

        public boolean isAborted() {
            return aborted;
        }
    }


}
