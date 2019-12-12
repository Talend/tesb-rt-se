package org.talend.esb.test.oidc.internal;

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

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OidcAccessTokenValidatorEmpowerIDTest {

    private static final String AUTHORIZATION = "Authorization";

    private static OidcAccessTokenValidator validator;
    private static Server server = null;
    private static Message msg;
    private static Map<String, List<Object>> headersMap;

    @BeforeClass
    public static void init() {
        //--> Test avec EmpowerId dans l'environnement d'int
        //startValidationService();
        //createValidator();
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
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, "https://idpqa.fr.ch/oauth/v2/token");
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, "https://idpqa.fr.ch/oauth/v2/tokeninfo");
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "71cf1295-e12e-46df-9967-25c42fcfa189");
        map.put(OidcConfiguration.OIDC_CLIENT_SECRET,"7bb255c6-974b-4518-8238-0fe4a99af6f2");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");
        map.put(OidcConfiguration.OIDC_CACHE_ENABLE,"false");

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


    public void testValidTokenWithoutCache100x() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, "https://idpqa.fr.ch/oauth/v2/token");
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, "https://idpqa.fr.ch/oauth/v2/tokeninfo");
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "71cf1295-e12e-46df-9967-25c42fcfa189");
        map.put(OidcConfiguration.OIDC_CLIENT_SECRET,"7bb255c6-974b-4518-8238-0fe4a99af6f2");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");
        map.put(OidcConfiguration.OIDC_CACHE_ENABLE,"false");

        new OidcClientUtils(map);
        validator = new OidcAccessTokenValidator();

        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer ZnQrdUdzREpyaGRJZTdrUkFsS0tWY3pYK2RNRGxRRFhmZ2ZkdUFDR1dNbEg3Y0psUUxpaTBRMUhtMXM4V1ZRQ0hMYklOZlRwbDBhLzc0RER6VWtiNmFyMVNrK0dsbDZseDVZQm5ZYUQ1OVJ4a1VMUStLNCsvbi8wVkVtTVIrTzB5VlU5aHdFYzloQ1BWS1k2akxKU1gybEdZekhZUk1sU1lQZlZmaU0yakEwbkttOUUyOWF0K2VlSFpCNEZaSm");
        try {
            for(int i=0;i<100;i++) {
                validator.filter(ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertFalse(ctx.isAborted());
    }

   @Test
    public void testValidTokenWithCache100x() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, "https://idpqa.fr.ch/oauth/v2/token");
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, "https://idpqa.fr.ch/oauth/v2/tokeninfo");
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "71cf1295-e12e-46df-9967-25c42fcfa189");
        map.put(OidcConfiguration.OIDC_CLIENT_SECRET,"7bb255c6-974b-4518-8238-0fe4a99af6f2");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");
        map.put(OidcConfiguration.OIDC_CACHE_ENABLE,"true");

        new OidcClientUtils(map);
        validator = new OidcAccessTokenValidator();

        ContainerRequestContextTest ctx = new ContainerRequestContextTest();
        setAuthzHeader("Bearer ZnQrdUdzREpyaGRJZTdrUkFsS0tWY3pYK2RNRGxRRFhmZ2ZkdUFDR1dNbEg3Y0psUUxpaTBRMUhtMXM4V1ZRQ0hMYklOZlRwbDBhLzc0RER6VWtiNmFyMVNrK0dsbDZseDVZQm5ZYUQ1OVJ4a1VMUStLNCsvbi8wVkVtTVIrTzB5VlU5aHdFYzloQ1BWS1k2akxKU1gybEdZekhZUk1sU1lQZlZmaU0yakEwbkttOUUyOWF0K2VlSFpCNEZaSm");
        try {
            for(int i=0;i<100;i++) {
                validator.filter(ctx);
            }
            validator.filter(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        //assertFalse(ctx.isAborted());
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
