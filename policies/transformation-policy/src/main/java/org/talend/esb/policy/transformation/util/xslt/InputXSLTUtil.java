package org.talend.esb.policy.transformation.util.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.feature.transform.XSLTInInterceptor;
import org.apache.cxf.feature.transform.XSLTUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.staxutils.StaxUtils;

public class InputXSLTUtil extends AbstractXSLTUtil {

     private static final Logger LOG = LogUtils.getL7dLogger(XSLTInInterceptor.class);

        public InputXSLTUtil(String xsltPath) {
            super(xsltPath);
        }

        public InputXSLTUtil(String phase, Class<?> before, Class<?> after, String xsltPath) {
            super(xsltPath);
        }

        @Override
        public void performTransformation(Message message) {

            if (!isRequestor(message) && isGET(message) || checkContextProperty(message)) {
                return;
            }

            // 1. Try to get and transform XMLStreamReader message content
            XMLStreamReader xReader = message.getContent(XMLStreamReader.class);
            if (xReader != null) {
                transformXReader(message, xReader);
            } else {
                // 2. Try to get and transform InputStream message content
                InputStream is = message.getContent(InputStream.class);
                if (is != null) {
                    transformIS(message, is);
                } else {
                    // 3. Try to get and transform Reader message content (actually used for JMS TextMessage)
                    Reader reader = message.getContent(Reader.class);
                    if (reader != null) {
                        transformReader(message, reader);
                    }
                }
            }
        }

        protected void transformXReader(Message message, XMLStreamReader xReader) {
            CachedOutputStream cachedOS = new CachedOutputStream();
            try {
                StaxUtils.copy(xReader, cachedOS);
                InputStream transformedIS = XSLTUtils.transform(getXSLTTemplate(), cachedOS.getInputStream());
                XMLStreamReader transformedReader = StaxUtils.createXMLStreamReader(transformedIS);
                message.setContent(XMLStreamReader.class, transformedReader);
            } catch (XMLStreamException e) {
                throw new Fault("STAX_COPY", LOG, e, e.getMessage());
            } catch (IOException e) {
                throw new Fault("GET_CACHED_INPUT_STREAM", LOG, e, e.getMessage());
            } finally {
                try {
                    StaxUtils.close(xReader);
                } catch (XMLStreamException e) {
                    LOG.warning("Cannot close stream after transformation: " + e.getMessage());
                }
                try {
                    cachedOS.close();
                } catch (IOException e) {
                    LOG.warning("Cannot close stream after transformation: " + e.getMessage());
                }
            }
        }

        protected void transformIS(Message message, InputStream is) {
            try {
                InputStream transformedIS = XSLTUtils.transform(getXSLTTemplate(), is);
                message.setContent(InputStream.class, transformedIS);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.warning("Cannot close stream after transformation: " + e.getMessage());
                }
            }
        }

        protected void transformReader(Message message, Reader reader) {
            try {
                Reader transformedReader = XSLTUtils.transform(getXSLTTemplate(), reader);
                message.setContent(Reader.class, transformedReader);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.warning("Cannot close stream after transformation: " + e.getMessage());
                }
            }
        }
}
