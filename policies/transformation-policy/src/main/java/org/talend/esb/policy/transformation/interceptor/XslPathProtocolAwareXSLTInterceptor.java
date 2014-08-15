package org.talend.esb.policy.transformation.interceptor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.feature.transform.XSLTInInterceptor;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.StaxUtils;
import org.w3c.dom.Document;

public abstract class XslPathProtocolAwareXSLTInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final TransformerFactory TRANSFORM_FACTORIY = TransformerFactory.newInstance();

    private String contextPropertyName;
    private final Templates xsltTemplate;

    public XslPathProtocolAwareXSLTInterceptor(String phase, Class<?> before, Class<?> after, String xsltPath) {
        super(phase);
        if (before != null) {
            addBefore(before.getName());
        }
        if (after != null) {
            addAfter(after.getName());
        }

        InputStream xsltStream;
        HttpURLConnection urlConnection = null;

        if (xsltPath.startsWith("http://")) {
            try {
                URL url = new URL(xsltPath);
                urlConnection = (HttpURLConnection) url.openConnection();
                xsltStream = urlConnection.getInputStream();
            } catch (Exception e) {
                xsltStream = null;
            }

        } else {
            try {
                xsltStream = new FileInputStream(xsltPath);
            } catch (FileNotFoundException e) {
                xsltStream = null;
            }

            if (xsltStream == null) {
                xsltStream = ClassLoaderUtils.getResourceAsStream(xsltPath, this.getClass());
            }
        }


        try {
            if (xsltStream == null) {
                throw new IllegalArgumentException("Cannot load XSLT from path: " + xsltPath);
            }
            Document doc = StaxUtils.read(xsltStream);

            xsltTemplate = TRANSFORM_FACTORIY.newTemplates(new DOMSource(doc));
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

            if (urlConnection != null) {
                urlConnection.disconnect();
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

}
