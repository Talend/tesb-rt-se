package org.talend.esb.policy.schemavalidate.interceptors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.common.xmlschema.LSInputImpl;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicyBuilder;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.AppliesToType;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.MessageType;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.ValidationType;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

public abstract class SchemaValidationPolicyAbstractInterceptor extends
		AbstractPhaseInterceptor<Message> {

	protected SchemaValidationPolicy policy = null;

	public SchemaValidationPolicyAbstractInterceptor(String phase) {
		super(phase);
	}

	public SchemaValidationPolicyAbstractInterceptor(String phase,
			SchemaValidationPolicy policy) {
		super(phase);
		this.policy = policy;
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		if (policy == null) {
			handleMessageWithAssertionInfo(message);
		} else {
			handleMessageWithoutAssertionInfo(message);
		}
	}

	protected void handleMessageWithAssertionInfo(Message message) throws Fault {
		AssertionInfoMap aim = message.get(AssertionInfoMap.class);
		if (null == aim) {
			return;
		}

		Collection<AssertionInfo> ais = aim
				.get(SchemaValidationPolicyBuilder.SCHEMA_VALIDATION);
		if (null == ais) {
			return;
		}

		for (AssertionInfo ai : ais) {
			if (ai.getAssertion() instanceof SchemaValidationPolicy) {
				SchemaValidationPolicy vPolicy = (SchemaValidationPolicy) ai
						.getAssertion();
				ValidationType vldType = vPolicy.getValidationType();
				AppliesToType appliesToType = vPolicy.getApplyToType();
				MessageType msgType = vPolicy.getMessageType();
				String customSchemaPath = vPolicy.getCustomSchemaPath();

				if (vldType != ValidationType.WSDLSchema) {
					ai.setAsserted(true);
				}

				if (shouldSchemaValidate(message, msgType, appliesToType)) {
					if (vldType == ValidationType.CustomSchema) {
						// load custom schema from external source
						try{
							loadCustomSchema(message, customSchemaPath, this.getClass());
						}catch(IOException ex){
							throw new RuntimeException("Can not load custom schema", ex);
						}						
					}
					// do schema validation by setting value to
					// "schema-validation-enabled" property
					validateBySettingProperty(message);
				}
				ai.setAsserted(true);
			}
			ai.setAsserted(true);
		}
	}

	protected void handleMessageWithoutAssertionInfo(Message message)
			throws Fault {

		ValidationType vldType = policy.getValidationType();
		AppliesToType appliesToType = policy.getApplyToType();
		MessageType msgType = policy.getMessageType();
		String customSchemaPath = policy.getCustomSchemaPath();

		if (shouldSchemaValidate(message, msgType, appliesToType)) {
			if (vldType == ValidationType.CustomSchema) {
				// load custom schema from external source
				try{
					loadCustomSchema(message, customSchemaPath, this.getClass());
				}catch(IOException ex){
					throw new RuntimeException("Can not load custom schema", ex);
				}
			}
			// do schema validation by setting value to
			// "schema-validation-enabled" property
			validateBySettingProperty(message);
		}
	}

	protected void loadCustomSchema(Message message, String customSchemaPath,
			@SuppressWarnings("rawtypes") Class c) throws IOException{

		if (customSchemaPath == null || customSchemaPath.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Path to custom schema is not set or empty");
		}

		String absoluteSchemaPath = null;
		
		CachedOutputStream cos = new CachedOutputStream();
		absoluteSchemaPath = loadResource(customSchemaPath, cos);
		InputStream customSchemaStream = cos.getInputStream();

		if (customSchemaStream == null) {
			throw new IllegalArgumentException(
					"Cannot load custom schema from path: " + customSchemaPath);
		}

		SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setResourceResolver(new SchemaResourceResolver(absoluteSchemaPath, customSchemaPath));

		Source src = new StreamSource(customSchemaStream);
		Schema customSchema;
		try {
			customSchema = factory.newSchema(src);
		} catch (SAXException e) {
			throw new IllegalArgumentException(
					"Cannot create custom schema from path: "
							+ customSchemaPath, e);
		}
		message.getExchange().getService().getServiceInfos().get(0)
				.setProperty(Schema.class.getName(), customSchema);
	}

	@SuppressWarnings("resource")
	private String loadResource(String path, OutputStream output) 
		throws IOException{

		InputStream resource = null;
		
		String absolutePath = null;

		// try to load resource from file system
		try {
			resource = new FileInputStream(path);
			if(resource!=null){
				absolutePath = path;
			}
		} catch (FileNotFoundException e) {
			resource = null;
		}

		if (resource == null) {
			// try to load resource from class loader root
			resource = ClassLoaderUtils.getResourceAsStream(path,
					this.getClass());
			if(resource!=null){
				URL url = ClassLoaderUtils.getResource(path, this.getClass());
				if(url!= null){
					absolutePath = url.getPath();	
				}
			}
		}

		if (resource == null) {
			// try to load schema as web resource
			try {
				URL url = new URL(path);
				resource = url.openStream();
			} catch (Exception e) {
			}
		}
		
		if(resource!=null){
			IOUtils.copyAndCloseInput(resource, output);
			return absolutePath;
		}
		
		return null;

	}

	class SchemaResourceResolver implements LSResourceResolver {
		
		String parentSchemaAbsolutePath = null;
		String parentSchemaProvidedPath = null;
		public SchemaResourceResolver(String parentSchemaAbsolutePath, 
				String parentSchemaProvidedPath){
			this.parentSchemaAbsolutePath = parentSchemaAbsolutePath;
			this.parentSchemaProvidedPath = parentSchemaProvidedPath;
			
		}
		
		public LSInput resolveResource(String type, String namespaceURI,
				String publicId, String systemId, String baseURI) {
			
			
			boolean isRemoteLocation = (systemId != null &&
					(systemId.startsWith("http://") || systemId.startsWith("https://")));
			
			//Try to find path to parent schema directory
			String parentSchemaDir = "";
			if(parentSchemaAbsolutePath!=null && !isRemoteLocation){
				File file = new File(parentSchemaAbsolutePath);
				if(file.exists()){
					parentSchemaDir = file.getParentFile().getAbsolutePath();
				}
			}

			// Try to resolve path to imported schema
			// using provided resource properties
			String resURL = null;
			
			if (systemId != null) {
				String schemaLocation = "";
				if (baseURI != null) {
					schemaLocation = baseURI.substring(0,
							baseURI.lastIndexOf("/") + 1);
				}

				if (!isRemoteLocation) {
					resURL = schemaLocation + systemId;
				} else {
					resURL = systemId;
				}
			} else if (namespaceURI != null) {
				// Schema location is not set
				// lets try to use namespace as
				// possible schema location
				resURL = namespaceURI;
				isRemoteLocation = true;
			}

			CachedOutputStream cache = new CachedOutputStream();
			InputStream resourceStream = null;
			String actualSchemaURL = null;
			try{
				
				// Try to load schema using absolute path
				actualSchemaURL = resURL;
				loadResource(actualSchemaURL, cache);
				
				if(cache.size()==0 && parentSchemaDir!=null && !parentSchemaDir.isEmpty() && !isRemoteLocation){
					// Schema is not found
					// Try to load schema using path to basic schema directory
					// (which is referenced in policy) as root
						actualSchemaURL = parentSchemaDir+File.separator + resURL;
						loadResource(actualSchemaURL, cache);
				}
				resourceStream = cache.getInputStream();
			}catch (IOException ex){
				return null;
			}
			
			if (cache.size() != 0) {
				LSInput resource = new LSInputImpl();
				resource.setSystemId(actualSchemaURL);
				resource.setPublicId(publicId);
				resource.setByteStream(resourceStream);
				return resource;
			}else{
				StringBuilder message = new StringBuilder();
				message.append("Schema validation: can not load internal schema with path {");
				if(systemId==null){
					message.append(resURL);
				}else{
					message.append(systemId);
				}
				message.append("}");
				
//				if(baseURI!=null && !baseURI.isEmpty()){
//					message.append(" which is referenced from schema {");
//					message.append(baseURI);
//					message.append("}");
//				}else if(parentSchemaProvidedPath!=null && !parentSchemaProvidedPath.isEmpty()){
//					message.append(" which is referenced from schema {");
//					message.append(parentSchemaProvidedPath);
//					message.append("}");
//				}
				throw new RuntimeException(message.toString());
			}
		}
	}

	protected boolean shouldSchemaValidate(Message message,
			MessageType msgType, AppliesToType appliesToType) {
		if (MessageUtils.isRequestor(message)) {
			if (MessageUtils.isOutbound(message)) { // REQ_OUT
				return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always) && (msgType == MessageType.request || msgType == MessageType.all));
			} else { // RESP_IN
				return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always) && (msgType == MessageType.response || msgType == MessageType.all));
			}
		} else {
			if (MessageUtils.isOutbound(message)) { // RESP_OUT
				return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always) && (msgType == MessageType.response || msgType == MessageType.all));
			} else { // REQ_IN
				return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always) && (msgType == MessageType.request || msgType == MessageType.all));
			}
		}
	}

	protected abstract void validateBySettingProperty(Message message);
}
