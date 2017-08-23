package org.talend.esb.test.oidc;

import static org.junit.Assert.*;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.*;
import org.talend.esb.security.oidc.OidcClientUtils;
import org.talend.esb.security.oidc.OidcConfiguration;
import org.talend.esb.test.oidc.internal.OidcTokenService;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OidcClientUtilsTest {

    private static Server server = null;

    @BeforeClass
    public static void startTokenService() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(OidcTokenService.class);
        sf.setAddress(OidcTokenService.ENDPOINT);
        sf.create();
        server = sf.getServer();
    }

    @AfterClass
    public static void stopTokenService() {
        if (server != null) {
            try {
                server.destroy();
            } catch (Throwable t) {}
        }
    }

    @Test
    public void testConstruction() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, "http://localhost:9080/oidc/oauth2/token");
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, "http://localhost:9080/oidc/oauth2/introspect");
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "aFSloIZSXHRQtA");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");

        new OidcClientUtils(map);
        assertEquals("http://localhost:9080/oidc/oauth2/introspect", OidcClientUtils.getValidationEndpoint());
        assertEquals("http://localhost:9080/oidc/oauth2/token", OidcClientUtils.getTokenEndpoint());
        assertEquals("openid", OidcClientUtils.getScope());
        assertEquals("aFSloIZSXHRQtA", OidcClientUtils.getPublicClientID());

        OidcConfiguration conf = OidcClientUtils.getOidcConfiguration();
        assertNotNull(conf);
        assertEquals("http://localhost:9080/oidc/oauth2/token", conf.getTokenEndpoint());

        conf.setPublicClientId("pcid");

        new OidcClientUtils(conf);
        assertEquals("http://localhost:9080/oidc/oauth2/introspect", OidcClientUtils.getValidationEndpoint());
        assertEquals("http://localhost:9080/oidc/oauth2/token", OidcClientUtils.getTokenEndpoint());
        assertEquals("openid", OidcClientUtils.getScope());
        assertEquals("pcid", OidcClientUtils.getPublicClientID());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObtainBearerNegative1() throws Exception{
        OidcConfiguration conf = null;
        OidcClientUtils.oidcClientBearer("user", "password", conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObtainBearerNegative2() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        OidcClientUtils.oidcClientBearer(null, "password", conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObtainBearerNegative3() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        OidcClientUtils.oidcClientBearer("", "password", conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObtainBearerNegative4() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        OidcClientUtils.oidcClientBearer("user", null, conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerNegative5() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(null);
        OidcClientUtils.oidcClientBearer("user", "password", conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerNegative6() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint("");
        OidcClientUtils.oidcClientBearer("user", "password", conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerNegative7() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        conf.setPublicClientId("");
        OidcClientUtils.oidcClientBearer("user", "password", conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerInvalidJson() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        OidcClientUtils.oidcClientBearer(OidcTokenService.USER_INVALID_JSON, "password", conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerServletException() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        OidcClientUtils.oidcClientBearer(OidcTokenService.USER_ERROR_500, "password", conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerServletException2() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        OidcClientUtils.oidcClientBearer(OidcTokenService.USER_ERROR_500_2, "password", conf);
    }

    @Test(expected = Exception.class)
    public void testObtainBearerServletWrongTokenType() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        OidcClientUtils.oidcClientBearer(OidcTokenService.USER_WRONG_TOKEN_TYPE, "password", conf);
    }

    @Test
    public void testObtainBearer() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        String token = OidcClientUtils.oidcClientBearer("user", "password", conf);
        assertNotNull(token);
        assertEquals("Bearer Access Granted!", token);
    }

    @Test
    public void testObtainBearer2() throws Exception{
        OidcConfiguration conf = new OidcConfiguration();
        conf.setTokenEndpoint(OidcTokenService.ENDPOINT);
        new OidcClientUtils(conf);
        String token = OidcClientUtils.oidcClientBearer("user", "password");
        assertNotNull(token);
        assertEquals("Bearer Access Granted!", token);
    }

    @Test
    public void testObtainBearer3() throws Exception{
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, OidcTokenService.ENDPOINT);
        String token = OidcClientUtils.oidcClientBearer("user", "password", map);
        assertNotNull(token);
        assertEquals("Bearer Access Granted!", token);
    }


    private void proba() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(OidcTokenService.class);
        sf.setAddress(OidcTokenService.ENDPOINT);
        sf.create();


        WebClient wc = WebClient.create(OidcTokenService.ENDPOINT).type("application/x-www-form-urlencoded");
        Response response = wc.post("grant_type=password&scope=oidc");
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        try {
            System.out.println(IOUtils.readStringFromStream((InputStream) response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
