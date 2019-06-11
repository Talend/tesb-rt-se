package org.talend.esb.policy.transformation.util.xslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.talend.esb.policy.transformation.TransformationAssertion;
import org.talend.esb.policy.transformation.TransformationPolicyBuilder;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.w3c.dom.Document;

public abstract class AbstractXSLTUtil {

    private static final Logger LOG = Logger.getLogger(AbstractXSLTUtil.class.getName());

    private String contextPropertyName;
    private final Templates xsltTemplate;


    public AbstractXSLTUtil(String xsltPath) {
        //loading XSLT resource from specified path
        InputStream xsltStream = null;
        String absoluteSchemaPath = null;
        CachedOutputStream cos = new CachedOutputStream();
        try {
            absoluteSchemaPath = loadResource(xsltPath, cos);
            xsltStream = cos.getInputStream();
            if (xsltStream == null) {
                throw new IllegalArgumentException("Cannot load XSLT from path: " + xsltPath);
            }

        }catch(Exception  ex){
            throw new IllegalArgumentException("Cannot load XSLT from path: " + xsltPath, ex);
        }


        XSLTResourceResolver resourceResolver = null;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            } catch (IllegalArgumentException ex) {
                LOG.fine("Property XMLConstants.ACCESS_EXTERNAL_DTD is not recognized");
            }
            try {
                factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            } catch (IllegalArgumentException ex) {
                LOG.fine("Property XMLConstants.ACCESS_EXTERNAL_STYLESHEET is not recognized");
            }
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            resourceResolver = new XSLTResourceResolver(absoluteSchemaPath,
                                                        xsltPath);
            factory.setURIResolver(resourceResolver);


            Document doc = StaxUtils.read(xsltStream);

            xsltTemplate = factory.newTemplates(new DOMSource(doc));
        } catch (TransformerConfigurationException e) {
            throw new IllegalArgumentException(
                                               String.format("Cannot create XSLT template from path: %s, error: ",
                                                             xsltPath, e.getException()), e);
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException(
                                               String.format("Cannot create XSLT template from path: %s, error: ",
                                                             xsltPath, e.getNestedException()), e);
        } finally {

            if (xsltStream != null) {
                try {
                    xsltStream.close();
                } catch (Exception e) {}
            }

            try {
                cos.close();
            } catch (IOException e) {}

            if(resourceResolver!=null){
                resourceResolver.cleanupCache();
            }
        }
    }



    class XSLTResourceResolver implements URIResolver {

        List<CachedOutputStream> cacheList = new ArrayList<CachedOutputStream>();

        String parentXSLTAbsolutePath = null;
        String parentXSLTProvidedPath = null;

        public XSLTResourceResolver(String parentXSLTAbsolutePath,
                                    String parentXSLTProvidedPath){
            this.parentXSLTAbsolutePath = parentXSLTAbsolutePath;
            this.parentXSLTProvidedPath = parentXSLTProvidedPath;

        }

        public Source resolve(String systemId, String baseURI) {

            boolean isRemoteLocation = (systemId != null &&
                (systemId.startsWith("http://") || systemId.startsWith("https://")));

            //Try to find path to parent XSLT directory
            String parentXSLTDir = "";
            if(parentXSLTAbsolutePath!=null && !isRemoteLocation){
                File file = new File(parentXSLTAbsolutePath);
                if(file.exists()){
                    parentXSLTDir = file.getParentFile().getAbsolutePath();
                }
            }

            // Try to resolve path to imported XSLT
            // using provided resource properties
            String resURL = null;

            if (systemId != null) {
                String XSLTLocation = "";
                if (baseURI != null) {
                    XSLTLocation = baseURI.substring(0,
                                                     baseURI.lastIndexOf("/") + 1);
                }

                if (!isRemoteLocation) {
                    resURL = XSLTLocation + systemId;
                } else {
                    resURL = systemId;
                }
            }

            CachedOutputStream cache = new CachedOutputStream();
            cacheList.add(cache);
            InputStream resourceStream = null;
            String actualXSLTURL = null;
            try{

                // Try to load XSLT using absolute path
                actualXSLTURL = resURL;
                loadResource(actualXSLTURL, cache);

                if(cache.size()==0 && parentXSLTDir!=null && !parentXSLTDir.isEmpty() && !isRemoteLocation){
                    // XSLT is not found
                    // Try to load XSLT using path to basic XSLT directory
                    // (which is referenced in policy) as root
                    actualXSLTURL = parentXSLTDir+File.separator + resURL;
                    loadResource(actualXSLTURL, cache);
                }
                resourceStream = cache.getInputStream();
            }catch (IOException ex){
                return null;
            }

            if (cache.size() != 0) {
                StreamSource source = new StreamSource(resourceStream);
                source.setSystemId(actualXSLTURL);
                return source;
            }else{
                StringBuilder message = new StringBuilder();
                message.append("Transformation: can not load internal XSLT with path {");
                if(systemId==null){
                    message.append(resURL);
                }else{
                    message.append(systemId);
                }
                message.append("}");

                throw new RuntimeException(message.toString());
            }
        }

        public void cleanupCache(){
            for (CachedOutputStream cache : cacheList) {
                try {
                    cache.close();
                } catch (IOException e) {}
            }
        }
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


    public void handleMessage(Message message) {
        try {
            performTransformation(message);
            confirmPolicyProcessing(message);
        }catch (RuntimeException e) {
            throw e;
        }catch (Exception e) {
            throw new Fault(e);
        }
    }


    abstract protected void performTransformation(Message message);


    private void confirmPolicyProcessing(Message message) {
        AssertionInfoMap aim = message.get(AssertionInfoMap.class);
        if (aim != null) {
            Collection<AssertionInfo> ais = aim
                .get(TransformationPolicyBuilder.TRANSFORMATION);

            if (ais != null) {
                for (AssertionInfo ai : ais) {
                    if (ai.getAssertion() instanceof TransformationAssertion) {
                        ai.setAsserted(true);
                    }
                }
            }
        }
    }

    public void setContextPropertyName(String propertyName) {
        contextPropertyName = propertyName;
    }

    protected boolean checkContextProperty(Message message) {
        return contextPropertyName != null
            && !MessageUtils.getContextualBoolean(message, contextPropertyName, false);
    }

    protected Templates getXSLTTemplate() {
        return xsltTemplate;
    }

    protected boolean isRequestor(Message message) {
        return MessageUtils.isRequestor(message);
    }

    protected boolean isGET(Message message) {
        String method = (String)message.get(Message.HTTP_REQUEST_METHOD);
        return "GET".equals(method) && message.getContent(XMLStreamReader.class) == null;
    }
}
