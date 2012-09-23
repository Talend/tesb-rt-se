package org.talend.esb.locator.service.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.SecurityConstants;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.feature.Feature;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyRegistry;
import org.talend.esb.locator.service.LocatorServiceConstants;
import org.talend.esb.locator.service.PolicyProvider;
import org.talend.esb.locator.service.LocatorServiceConstants.EsbSecurity;
import org.apache.cxf.jaxws22.spring.JAXWS22SpringEndpointImpl;
import org.apache.ws.security.validate.JAASUsernameTokenValidator;

@NoJSR250Annotations(unlessNull = "bus")
public class PolicyProviderImpl implements PolicyProvider {

	private String policyToken;
	private String policySaml;
	private String signatureProperties;
	private String signatureUsername;
	private String signaturePassword;
	private String serviceAutentication;
	private JAXWS22SpringEndpointImpl locatorEndpoint;
	private PolicyBuilder policyBuilder;

	private static final String ENDPOINT_SIGNATURE_PASSWORD = "ws-security.signature.password";

	public void init() {

		final EsbSecurity esbSecurity = EsbSecurity
				.fromString((String) serviceAutentication);

		if (EsbSecurity.NO == esbSecurity)
			return;

		List<Policy> policies = new ArrayList<Policy>();

		if (EsbSecurity.TOKEN == esbSecurity) {
			policies.add(getTokenPolicy());
		} else if (EsbSecurity.SAML == esbSecurity) {
			policies.add(getSamlPolicy());
		}

		Bus currentBus = BusFactory.getThreadDefaultBus();
		ServerRegistry registry = currentBus.getExtension(ServerRegistry.class);
		List<Server> servers = registry.getServers();

		Map<String, Object> endpointProps = new HashMap<String, Object>();

		if (EsbSecurity.TOKEN == esbSecurity) {
			JAASUsernameTokenValidator jaasUTValidator = new JAASUsernameTokenValidator();
			jaasUTValidator.setContextName("karaf");
			endpointProps.put(SecurityConstants.USERNAME_TOKEN_VALIDATOR,
					jaasUTValidator);
		}

		if (EsbSecurity.SAML == esbSecurity) {
			endpointProps.put(SecurityConstants.SIGNATURE_PROPERTIES,
					getSignatureProperties());
			endpointProps.put(SecurityConstants.SIGNATURE_USERNAME,
					getSignatureUsername());
			endpointProps.put(ENDPOINT_SIGNATURE_PASSWORD,
					getSignaturePassword());
			endpointProps.put(SecurityConstants.CALLBACK_HANDLER,
					new WSPasswordCallbackHandler(getSignatureUsername(),
							getSignaturePassword()));
		}

		locatorEndpoint.setProperties(endpointProps);

		Server srv = null;

		for (Server sr : servers) {
			if (sr.getEndpoint().getService() == locatorEndpoint.getService())
				srv = sr;
		}

		List<Feature> activeFeatures = locatorEndpoint.getFeatures();
		WSPolicyFeature policyFeature = new WSPolicyFeature();

		activeFeatures.add(policyFeature);

		policyFeature.setPolicies(policies);
		policyFeature.initialize(srv, currentBus);

	}

	public void register(Bus cxf) {
		final PolicyRegistry policyRegistry = cxf.getExtension(
				PolicyEngine.class).getRegistry();
		policyRegistry.register(LocatorServiceConstants.ID_POLICY_TOKEN,
				getTokenPolicy());
		policyRegistry.register(LocatorServiceConstants.ID_POLICY_SAML,
				getSamlPolicy());
	}

	private Policy loadPolicy(String location) {
		InputStream is = null;
		try {
			is = new FileInputStream(location);
			return policyBuilder.getPolicy(is);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load policy", e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// just ignore
				}
			}
		}
	}

	public void setPolicyToken(String policyToken) {
		this.policyToken = policyToken;
	}

	public void setLocatorEndpoint(JAXWS22SpringEndpointImpl locatorEndpoint) {
		this.locatorEndpoint = locatorEndpoint;
	}

	public void setserviceAutentication(String serviceAutentication) {
		this.serviceAutentication = serviceAutentication;
	}

	public void setPolicySaml(String policySaml) {
		this.policySaml = policySaml;
	}

	@javax.annotation.Resource
	public void setBus(Bus bus) {
		policyBuilder = bus.getExtension(PolicyBuilder.class);
	}

	public Policy getTokenPolicy() {
		return loadPolicy(policyToken);
	}

	public Policy getSamlPolicy() {
		return loadPolicy(policySaml);
	}

	public void setSignatureProperties(String signatureProperties) {
		this.signatureProperties = signatureProperties;
	}

	public String getSignatureProperties() {
		return signatureProperties;
	}

	public void setSignatureUsername(String signatureUsername) {
		this.signatureUsername = signatureUsername;
	}

	public String getSignatureUsername() {
		return signatureUsername;
	}

	public void setSignaturePassword(String signaturePassword) {
		this.signaturePassword = signaturePassword;
	}

	public String getSignaturePassword() {
		return signaturePassword;
	}

}
