package org.talend.esb.policy.transformation.interceptor.xslt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.talend.esb.policy.transformation.TransformationAssertion.AppliesToType;
import org.talend.esb.policy.transformation.TransformationAssertion.MessageType;
import org.talend.esb.policy.transformation.TransformationAssertion;
import org.talend.esb.policy.transformation.TransformationPolicyBuilder;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.w3c.dom.Document;

public abstract class AbstractHttpAwareXSLTInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final TransformerFactory TRANSFORM_FACTORIY = TransformerFactory.newInstance();

    private String contextPropertyName;
    private final Templates xsltTemplate;

    private MessageType msgType;
    private AppliesToType appliesToType;

    public AbstractHttpAwareXSLTInterceptor(String phase, Class<?> before, Class<?> after, String xsltPath) {
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


    @Override
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


    protected boolean shouldSchemaValidate(Message message) {
        if (MessageUtils.isRequestor(message)) {
            if (MessageUtils.isOutbound(message)) { // REQ_OUT
                return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.request || msgType == MessageType.all));
            } else { // RESP_IN
                return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.response || msgType == MessageType.all));
            }
        } else {
            if (MessageUtils.isOutbound(message)) { // RESP_OUT
                return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.response || msgType == MessageType.all));
            } else { // REQ_IN
                return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.request || msgType == MessageType.all));
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

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public void setAppliesToType(AppliesToType appliesToType) {
        this.appliesToType = appliesToType;
    }
}
