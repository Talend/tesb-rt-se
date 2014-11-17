package org.talend.esb.sam.service.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.security.JAASAuthenticationFilter;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.rs.security.saml.SamlHeaderInHandler;

@NoJSR250Annotations(unlessNull = "bus")
public class SAMServiceSecurityProvider {

    private JAXRSServerFactoryBean server;
    private String serviceAutentication;
    private String signatureProperties;
    private String signatureUsername;
    private String signaturePassword;

    private static final String ENDPOINT_SIGNATURE_PASSWORD = "ws-security.signature.password";

    public JAXRSServerFactoryBean getMonitoringEndpoint() {
        return server;
    }

    public void setMonitoringEndpoint(JAXRSServerFactoryBean server) {
        this.server = server;
    }

    public String getServiceAutentication() {
        return serviceAutentication;
    }

    public void setServiceAutentication(String serviceAutentication) {
        this.serviceAutentication = serviceAutentication;
    }

    public String getSignatureProperties() {
        return signatureProperties;
    }

    public void setSignatureProperties(String signatureProperties) {
        this.signatureProperties = signatureProperties;
    }

    public String getSignatureUsername() {
        return signatureUsername;
    }

    public void setSignatureUsername(String signatureUsername) {
        this.signatureUsername = signatureUsername;
    }

    public String getSignaturePassword() {
        return signaturePassword;
    }

    public void setSignaturePassword(String signaturePassword) {
        this.signaturePassword = signaturePassword;
    }

    public void init() {

        final EsbSecurityConstants esbSecurity = EsbSecurityConstants.fromString(serviceAutentication);

        if (EsbSecurityConstants.NO == esbSecurity) {
            return;
        }

        Bus serverBus = server.getBus();
        ServerRegistry registry = serverBus.getExtension(ServerRegistry.class);
        List<Server> servers = registry.getServers();

        for (Server sr : servers) {
            EndpointInfo ei = sr.getEndpoint().getEndpointInfo();
            if (null != ei && ei.getAddress().endsWith(server.getAddress())){
                registry.unregister(sr);
                sr.destroy();
            }
        }

        @SuppressWarnings("unchecked")
        List<Object> providers = (List<Object>) server.getProviders();

        Map<String, Object> endpointProperties = new HashMap<String, Object>();

        if (EsbSecurityConstants.BASIC == esbSecurity) {
            JAASAuthenticationFilter authenticationFilter = new JAASAuthenticationFilter();
            authenticationFilter.setContextName("karaf");
            providers.add(authenticationFilter);
            server.setProviders(providers);
        } else if (EsbSecurityConstants.SAML == esbSecurity) {
            endpointProperties.put(SecurityConstants.SIGNATURE_PROPERTIES, getSignatureProperties());
            endpointProperties.put(SecurityConstants.SIGNATURE_USERNAME, getSignatureUsername());
            endpointProperties.put(ENDPOINT_SIGNATURE_PASSWORD, getSignaturePassword());
            endpointProperties.put(SecurityConstants.CALLBACK_HANDLER, new WSPasswordCallbackHandler(
                    getSignatureUsername(), getSignaturePassword()));

            Map<String, Object> properties = server.getProperties();
            if (null == properties)
                properties = new HashMap<String, Object>();
            properties.putAll(endpointProperties);
            server.setProperties(properties);

            SamlHeaderInHandler samlHandler = new SamlHeaderInHandler();

            providers.add(samlHandler);
            server.setProviders(providers);
        }

        server.create();

    }
}
