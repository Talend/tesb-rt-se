package org.talend.esb.job.controller.internal;

import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

//@javax.jws.WebService(name = "TalendJobAsWebService", targetNamespace = "http://talend.org/esb/service/job")
//@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE, style = javax.jws.soap.SOAPBinding.Style.DOCUMENT, use = javax.jws.soap.SOAPBinding.Use.LITERAL)
@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.MESSAGE)
@javax.xml.ws.WebServiceProvider()
public abstract class ESBProviderBase implements
		javax.xml.ws.Provider<javax.xml.transform.Source> {
	private static final Logger LOG = Logger.getLogger(ESBProviderBase.class
			.getName());

	public static final String REQUEST_PAYLOAD = "PAYLOAD";
	public static final String REQUEST_SAM_PROPS = "SAM-PROPS";

	protected EventFeature eventFeature;

	@javax.annotation.Resource
	private javax.xml.ws.WebServiceContext context;

	private String authenticationType = "NO";

	public void setEventFeature(EventFeature eventFeature) {
		this.eventFeature = eventFeature;
	}

	// @javax.jws.WebMethod(exclude=true)
	public final Source invoke(Source request) {

		if (authenticationType.equals(ESBEndpointConstants.EsbSecurity.TOKEN)) {
			LOG.info("UsernameToken authentication required");
			try {
				login();
			} catch (LoginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return processResult(e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return processResult(e);
			}
		} else if (authenticationType.equals(ESBEndpointConstants.EsbSecurity.SAML)) {
			LOG.info("SAML authentication required");
		} else {
			LOG.info("No authentication required");
		}

		QName operationQName = (QName) context.getMessageContext().get(
				MessageContext.WSDL_OPERATION);
		LOG.info("Invoke operation '" + operationQName + "'");
		GenericOperation esbProviderCallback = getESBProviderCallback(operationQName
				.getLocalPart());
		if (esbProviderCallback == null) {
			throw new RuntimeException("Handler for operation "
					+ operationQName + " cannot be found");
		}
		try {
			Object result = esbProviderCallback.invoke(
					DOM4JMarshaller.sourceToDocument(request),
					isOperationRequestResponse(operationQName.getLocalPart()));

			// oneway
			if (result == null) {
				return null;
			}

			if (result instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) result;

				@SuppressWarnings("unchecked")
				Map<String, String> samProps = (Map<String, String>) map
						.get(REQUEST_SAM_PROPS);
				if (samProps != null && eventFeature != null) {
					LOG.info("SAM custom properties received: " + samProps);
					CustomInfoHandler ciHandler = new CustomInfoHandler();
					ciHandler.setCustomInfo(samProps);
					eventFeature.setHandler(ciHandler);
				}

				return processResult(map.get(REQUEST_PAYLOAD));
			} else {
				return processResult(result);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void login() throws LoginException, SecurityException {

		LOG.info("Authentication to data service started");
		MessageContext messageContext = context.getMessageContext();
		String username = (String) messageContext
				.get(BindingProvider.USERNAME_PROPERTY);
		char[] password = messageContext.get(BindingProvider.PASSWORD_PROPERTY)
				.toString().toCharArray();

		LoginContext loginContext = null;

		try {
			LOG.info("Creating LoginContext");
			loginContext = new LoginContext("KarafRealm",
					new ESBCallbackHandler(username, password));
		} catch (LoginException le) {
			LOG.info("Cannot create LoginContext. " + le.getMessage());
			LOG.info(le.getStackTrace().toString());
			throw le;
		} catch (SecurityException se) {
			LOG.info("Cannot create LoginContext. " + se.getMessage());
			LOG.info(se.getStackTrace().toString());
			throw se;
		}

		try {		
			loginContext.login();
		} catch (LoginException le) {
			LOG.info("Authentication failed for user: " + username);
			LOG.info(le.getStackTrace().toString());
			throw le;
		}

		LOG.info("Authentication succeeded for user: " + username);

	}

	private Source processResult(Object result) {
		if (result instanceof org.dom4j.Document) {
			return DOM4JMarshaller
					.documentToSource((org.dom4j.Document) result);
		} else if (result instanceof RuntimeException) {
			throw (RuntimeException) result;
		} else if (result instanceof Throwable) {
			throw new RuntimeException((Throwable) result);
		} else {
			throw new RuntimeException("Provider return incompatible object: "
					+ result.getClass().getName());
		}
	}

	public abstract GenericOperation getESBProviderCallback(String operationName);

	protected boolean isOperationRequestResponse(String operationName) {
		// is better way to get communication style?
		return (null != context.getMessageContext().get(
				MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS));
	}

}
