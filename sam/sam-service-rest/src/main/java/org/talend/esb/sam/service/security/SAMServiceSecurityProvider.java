package org.talend.esb.sam.service.security;

import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.security.JAASAuthenticationFilter;
import org.apache.cxf.service.model.EndpointInfo;

@NoJSR250Annotations(unlessNull = "bus")
public class SAMServiceSecurityProvider {

    private JAXRSServerFactoryBean server;
    private String serviceAutentication;
    private String signatureProperties;
    private String signatureUsername;
    private String signaturePassword;

    public JAXRSServerFactoryBean getServer() {
        return server;
    }

    public void setServer(JAXRSServerFactoryBean server) {
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

        Bus currentBus = BusFactory.getThreadDefaultBus();
        ServerRegistry registry = currentBus.getExtension(ServerRegistry.class);
        List<Server> servers = registry.getServers();

        for (Server server : servers) {
            EndpointInfo ei = server.getEndpoint().getEndpointInfo();
            if (null != ei && ei.getAddress().length() == 4 && ei.getAddress().endsWith("sam")) {
                server.destroy();
            }
        }

        @SuppressWarnings("unchecked")
        List<Object> providers = (List<Object>) server.getProviders();

        if (EsbSecurityConstants.BASIC == esbSecurity) {
            JAASAuthenticationFilter authenticationFilter = new JAASAuthenticationFilter();
            authenticationFilter.setContextName("karaf");
            providers.add(authenticationFilter);
            server.setProviders(providers);
        } else if (EsbSecurityConstants.SAML == esbSecurity) {
            // TODO implement SAML security
        }

        server.create();

    }
}
