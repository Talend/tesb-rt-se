package org.talend.esb.policy.compression.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.helpers.HttpHeaderHelper;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AttachmentInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.talend.esb.policy.compression.impl.internal.CompressionHelper;
import org.talend.esb.policy.compression.impl.internal.StreamPosition;

/**
 * Interceptor that uncompresses those incoming messages that have "gzip"
 * content-encoding. An instance of this class should be added as an in and
 * inFault interceptor on clients that need to talk to a service that returns
 * gzipped responses or on services that want to accept gzipped requests. For
 * clients, you probably also want to use
 * {@link org.apache.cxf.transports.http.configuration.HTTPClientPolicy#setAcceptEncoding}
 * to let the server know you can handle compressed responses.).
 */
public class CompressionInInterceptor extends
		AbstractPhaseInterceptor<Message> {

	private static final Logger LOG = LogUtils
			.getL7dLogger(CompressionInInterceptor.class);

	public CompressionInInterceptor() {
		super(Phase.RECEIVE);
		addBefore(AttachmentInInterceptor.class.getName());
	}

	public void handleMessage(Message message) throws Fault {
		try {

			// Perform compression
			decompressMessage(message);

			// Confirm policy processing
			AssertionInfo ai = CompressionPolicyBuilder.getAssertion(message);

			if (ai != null) {
				ai.setAsserted(true);
			}

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new Fault(e);
		}
	}

	public void decompressMessage(Message message)
			throws Fault {
		if (isGET(message)) {
			return;
		}
		// check for Content-Encoding header - we are only interested in
		// messages that say they are gzipped.
		Map<String, List<String>> protocolHeaders = CastUtils
				.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
		if (protocolHeaders != null) {
			List<String> contentEncoding = HttpHeaderHelper.getHeader(
					protocolHeaders, HttpHeaderHelper.CONTENT_ENCODING);
			if (contentEncoding == null) {
				contentEncoding = protocolHeaders
						.get(CompressionOutInterceptor.SOAP_JMS_CONTENTENCODING);
			}
			if (contentEncoding != null
					&& (contentEncoding.contains("gzip") || contentEncoding
							.contains("x-gzip"))) {
				try {
					LOG.fine("Uncompressing response");
					// Original stream with compressed body
					InputStream is = message.getContent(InputStream.class);
					if (is == null) {
						return;
					}

					// Loading content of original InputStream to cache
					CachedOutputStream cache = new CachedOutputStream();
					IOUtils.copy(is, cache);
					is.close();

					// Loading SOAP body content to separate stream
					CachedOutputStream soapBodyContent = new CachedOutputStream();
					StreamPosition p = null;
					try {
						p = CompressionHelper.loadSoapBodyContent(
								cache.getInputStream(), soapBodyContent);
					} catch (XMLStreamException e) {
						throw new Fault("GZIP decompression failed", LOG, e,
								e.getMessage());
					}

					// apply Base64 decoding for encoded soap body content
					final byte[] base64DecodedSoapBody = (new Base64())
							.decode(soapBodyContent.getBytes());

					// uncompress soap body 
					GZIPInputStream decompressedBody = new GZIPInputStream(
							new ByteArrayInputStream(base64DecodedSoapBody));
					
					// replace original soap body by compressed one 
					CachedOutputStream decompressedSoapMessage = new CachedOutputStream();
					CompressionHelper.replaceBodyInSOAP(cache.getBytes(), p, 
							decompressedBody, decompressedSoapMessage);

					message.setContent(InputStream.class,
							decompressedSoapMessage.getInputStream());

					// remove content encoding header as we've now dealt with it
					for (String key : protocolHeaders.keySet()) {
						if (key.equalsIgnoreCase("Content-Encoding")) {
							protocolHeaders.remove(key);
							break;
						}
					}

					if (isRequestor(message)) {
						// record the fact that is worked so future requests
						// will
						// automatically be FI enabled
						Endpoint ep = message.getExchange().getEndpoint();
						ep.put(CompressionOutInterceptor.USE_GZIP_KEY,
								CompressionOutInterceptor.UseGzip.YES);
					}
				} catch (IOException ex) {
					throw new Fault("Can not unzip", LOG, ex);
				}
			}
		}
	}
	


}
