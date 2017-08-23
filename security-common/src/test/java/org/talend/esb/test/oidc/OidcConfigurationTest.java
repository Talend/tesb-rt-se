package org.talend.esb.test.oidc;


import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.esb.security.oidc.OidcConfiguration;

import java.util.HashMap;
import java.util.Map;

public class OidcConfigurationTest {

    @Test
    public void test() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, "http://localhost:9080/oidc/oauth2/token");
        map.put(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, "http://localhost:9080/oidc/oauth2/introspect");
        map.put(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "aFSloIZSXHRQtA");
        map.put(OidcConfiguration.OIDC_SCOPE, "openid");

        OidcConfiguration conf = new OidcConfiguration(map);
        assertEquals("http://localhost:9080/oidc/oauth2/token", conf.getTokenEndpoint());
        assertEquals("http://localhost:9080/oidc/oauth2/introspect", conf.getValidationEndpoint());
        assertEquals("aFSloIZSXHRQtA", conf.getPublicClientId());
        assertEquals("openid", conf.getScope());

        Map<String,String> m2 = conf.getOidcProperties();
        assertNotNull(m2);
        assertTrue(m2.size() == 4);
        assertTrue(m2.get(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID).equals("aFSloIZSXHRQtA"));

        conf.setPublicClientId("pcid");
        assertEquals("pcid", conf.getPublicClientId());

        conf.setScope("scp");
        assertEquals("scp", conf.getScope());

        conf.setValidationEndpoint("vep");
        assertEquals("vep", conf.getValidationEndpoint());

        conf.setTokenEndpoint("tep");
        assertEquals("tep", conf.getTokenEndpoint());
    }

    @Test
    public void test2() {
        OidcConfiguration conf = new OidcConfiguration();
        assertEquals("aFSloIZSXHRQtA", conf.getPublicClientId());
        assertEquals("openid", conf.getScope());

        System.setProperty(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID, "oidcpci");
        assertEquals("oidcpci", conf.getPublicClientId());
        System.clearProperty(OidcConfiguration.OIDC_PUBLIC_CLIENT_ID);

        System.setProperty(OidcConfiguration.OIDC_SCOPE, "scope");
        assertEquals("scope", conf.getScope());
        System.clearProperty(OidcConfiguration.OIDC_SCOPE);

        System.setProperty(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION, "velo");
        assertEquals("velo", conf.getValidationEndpoint());
        System.clearProperty(OidcConfiguration.OIDC_VALIDATION_ENDPOINT_LOCATION);

        System.setProperty(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION, "telo");
        assertEquals("telo", conf.getTokenEndpoint());
        System.clearProperty(OidcConfiguration.OIDC_TOKEN_ENDPOINT_LOCATION);
    }
}
