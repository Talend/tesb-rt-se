package org.talend.esb.policy.transformation.util.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.feature.transform.XSLTOutInterceptor;
import org.apache.cxf.feature.transform.XSLTUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AbstractOutDatabindingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.DelegatingXMLStreamWriter;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.wsdl.interceptors.BareOutInterceptor;

public class OutputXSLTUtil extends AbstractXSLTUtil {

        private static final Logger LOG = LogUtils.getL7dLogger(XSLTOutInterceptor.class);


        public OutputXSLTUtil(String xsltPath) {
            super(xsltPath);
        }


        @Override
        public void performTransformation(Message message) {

            if (checkContextProperty(message)) {
                return;
            }

            // 1. Try to get and transform XMLStreamWriter message content
            XMLStreamWriter xWriter = message.getContent(XMLStreamWriter.class);
            if (xWriter != null) {
                transformXWriter(message, xWriter);
            } else {
                // 2. Try to get and transform OutputStream message content
                OutputStream out = message.getContent(OutputStream.class);
                if (out != null) {
                    transformOS(message, out);
                } else {
                    // 3. Try to get and transform Writer message content (actually used for JMS TextMessage)
                    Writer writer = message.getContent(Writer.class);
                    if (writer != null) {
                        transformWriter(message, writer);
                    }
                }
            }
        }

        protected void transformXWriter(Message message, XMLStreamWriter xWriter) {
            CachedWriter writer = new CachedWriter();
            XMLStreamWriter delegate = StaxUtils.createXMLStreamWriter(writer);
            XSLTStreamWriter wrapper = new XSLTStreamWriter(getXSLTTemplate(), writer, delegate, xWriter);
            message.setContent(XMLStreamWriter.class, wrapper);
            message.put(AbstractOutDatabindingInterceptor.DISABLE_OUTPUTSTREAM_OPTIMIZATION,
                        Boolean.TRUE);

            TransformationOutEndingInterceptor si = new TransformationOutEndingInterceptor();
            message.getInterceptorChain().add(si);
        }

        protected void transformOS(Message message, OutputStream out) {
            CachedOutputStream wrapper = new CachedOutputStream();
            message.setContent(OutputStream.class, wrapper);
            TransformationOutEndingInterceptor si = new TransformationOutEndingInterceptor(getXSLTTemplate(), out);
            message.getInterceptorChain().add(si);
        }

        protected void transformWriter(Message message, Writer writer) {
            XSLTCachedWriter wrapper = new XSLTCachedWriter(getXSLTTemplate(), writer);
            message.setContent(Writer.class, wrapper);
            TransformationOutEndingInterceptor si = new TransformationOutEndingInterceptor();
            message.getInterceptorChain().add(si);
        }


        public static class XSLTStreamWriter extends DelegatingXMLStreamWriter {

            private final Templates xsltTemplate;
            private final CachedWriter cachedWriter;
            private final XMLStreamWriter origXWriter;

            public XSLTStreamWriter(Templates xsltTemplate, CachedWriter cachedWriter,
                                    XMLStreamWriter delegateXWriter, XMLStreamWriter origXWriter) {
                super(delegateXWriter);
                this.xsltTemplate = xsltTemplate;
                this.cachedWriter = cachedWriter;
                this.origXWriter = origXWriter;
            }

            public void performTransformation(Message message) {
                Reader transformedReader = null;
                try {
                    super.flush();
                    transformedReader = XSLTUtils.transform(xsltTemplate, cachedWriter.getReader());

                    StaxUtils.copy(new StreamSource(transformedReader), origXWriter);
                    message.setContent(XMLStreamWriter.class, origXWriter);

                } catch (XMLStreamException e) {
                    throw new Fault("STAX_COPY", LOG, e, e.getMessage());
                } catch (IOException e) {
                    throw new Fault("GET_CACHED_INPUT_STREAM", LOG, e, e.getMessage());
                } finally {
                    try {
                        if (transformedReader != null) {
                            transformedReader.close();
                        }
                        cachedWriter.close();
                        super.close();
                    } catch (Exception e) {
                        LOG.warning("Cannot close stream after transformation: " + e.getMessage());
                    }
                }
            }
        }


        public static class XSLTCachedWriter extends CachedWriter {
            private final Templates xsltTemplate;
            private final Writer origWriter;

            public XSLTCachedWriter(Templates xsltTemplate, Writer origWriter) {
                this.xsltTemplate = xsltTemplate;
                this.origWriter = origWriter;
            }

            public void performTransformation(Message message) {
                Reader transformedReader = null;
                try {
                    transformedReader = XSLTUtils.transform(xsltTemplate, getReader());
                    IOUtils.copyAndCloseInput(transformedReader, origWriter, IOUtils.DEFAULT_BUFFER_SIZE);
                    message.setContent(Writer.class, origWriter);
                } catch (IOException e) {
                    throw new Fault("READER_COPY", LOG, e, e.getMessage());
                }
            }
        }


        public static class TransformationOutEndingInterceptor extends AbstractPhaseInterceptor<Message> {

            private static final Logger LOG = LogUtils.getL7dLogger(TransformationOutEndingInterceptor.class);

            private Templates xsltTemplate = null;
            private OutputStream origStream = null;

            public TransformationOutEndingInterceptor() {
                super(Phase.MARSHAL);
                addAfter(BareOutInterceptor.class.getName());
            }

            public TransformationOutEndingInterceptor(Templates xsltTemplate, OutputStream origStream) {
                super(Phase.MARSHAL);
                addAfter(BareOutInterceptor.class.getName());

                this.xsltTemplate = xsltTemplate;
                this.origStream = origStream;
            }


            @Override
            public void handleMessage(Message message) throws Fault {
                if (message.getContent(XMLStreamWriter.class) != null) {
                    transformXMLStreamWriter(message);
                } else if (message.getContent(OutputStream.class) != null) {
                    transformOutputStream(message);
                } else if (message.getContent(Writer.class) != null) {
                    transformWriter(message);
                }
            }

            private void transformXMLStreamWriter(Message message) {
                XMLStreamWriter xmlWriter = message.getContent(XMLStreamWriter.class);
                if (xmlWriter == null) {
                    return;
                }
                if (!(xmlWriter instanceof XSLTStreamWriter)) {
                    return;
                }

                XSLTStreamWriter xsltWriter = (XSLTStreamWriter)xmlWriter;
                xsltWriter.performTransformation(message);
            }


            private void transformOutputStream(Message message) {
                OutputStream os = message.getContent(OutputStream.class);
                if (os == null) {
                    return;
                }
                if (!(os instanceof CachedOutputStream)) {
                    return;
                }

                CachedOutputStream wrapper = (CachedOutputStream)os;
                InputStream transformedStream = null;
                try {
                    transformedStream = XSLTUtils.transform(xsltTemplate, wrapper.getInputStream());
                    IOUtils.copy(transformedStream, origStream);
                    message.setContent(OutputStream.class, origStream);
                } catch (IOException e) {
                    throw new Fault("STREAM_COPY", LOG, e, e.getMessage());
                }finally{
                	try {
						wrapper.close();
					} catch (IOException e) {}
                }
            }

            private void transformWriter(Message message) {
                Writer writer = message.getContent(Writer.class);
                if (writer == null) {
                    return;
                }
                if (!(writer instanceof XSLTCachedWriter)) {
                    return;
                }

                XSLTCachedWriter cachedWriter = (XSLTCachedWriter)writer;
                cachedWriter.performTransformation(message);
            }
        }
}
