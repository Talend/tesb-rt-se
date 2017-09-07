package org.talend.esb.policy.compression.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AttachmentInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.talend.esb.policy.compression.impl.internal.CompressionConstants;
import org.talend.esb.policy.compression.impl.internal.CompressionHelper;

/**
 * Interceptor that uncompresses those incoming messages.
 */
public class CompressionInInterceptor extends AbstractPhaseInterceptor<Message> {

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
			confirmPolicyProcessing(message);

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new Fault(e);
		}
	}
	
    protected void confirmPolicyProcessing(Message message) {
        AssertionInfoMap aim = message.get(AssertionInfoMap.class);
        if (aim != null) {
            Collection<AssertionInfo> ais = aim
                      .get(CompressionPolicyBuilder.COMPRESSION);

            if (ais != null) {
                for (AssertionInfo ai : ais) {
                    if (ai.getAssertion() instanceof CompressionAssertion) {
                        ai.setAsserted(true);
                    }
                }
            }
        }
   }

	public void decompressMessage(Message message) throws Fault {
		if (isGET(message)) {
			return;
		}
		
		final CachedOutputStream cache = new CachedOutputStream();
		final CachedOutputStream decompressedSoapMessage = new CachedOutputStream();

		try {
			LOG.fine("Uncompressing response");
			// Original stream with compressed body
			InputStream is = message.getContent(InputStream.class);
			if (is == null) {
				return;
			}

			// Loading content of original InputStream to cache
			IOUtils.copyAndCloseInput(is, cache);

			// Loading SOAP body content to separate stream
			CachedOutputStream soapBodyContent = new CachedOutputStream();

			Scanner scanner = new Scanner(cache.getInputStream()); 
			MatchResult bodyPosition =  null;
			try {
				bodyPosition = CompressionHelper.loadSoapBodyContent(soapBodyContent, scanner,
						CompressionConstants.COMPRESSED_SOAP_BODY_PATTERN);
			} catch (XMLStreamException e) {
				throw new Fault("Can not read compressed SOAP Body", LOG, e,
						e.getMessage());
			}

			if (bodyPosition==null) {
				// compressed SOAP body content is not found
				// skipping decompression
				message.setContent(InputStream.class, cache.getInputStream());

			} else {
				// compressed SOAP body content is found
				// apply Base64 decoding for encoded soap body content
				final byte[] base64DecodedSoapBody = (new Base64())
						.decode(soapBodyContent.getBytes());

				// uncompress soap body
				GZIPInputStream decompressedBody = new GZIPInputStream(
						new ByteArrayInputStream(base64DecodedSoapBody));

				// replace original soap body by compressed one
				CompressionHelper.replaceBodyInSOAP(cache.getBytes(), 
						bodyPosition,
						decompressedBody, decompressedSoapMessage, null, null, true);

				message.setContent(InputStream.class,
						decompressedSoapMessage.getInputStream());

			}

			if(message.getInterceptorChain() != null){
	            message.getInterceptorChain().add(new AbstractPhaseInterceptor<Message>(Phase.POST_INVOKE) {
					@Override
					public void handleMessage(Message message) throws Fault {
						closeCacheStreams(cache, decompressedSoapMessage);
					}
				});
			}
		} catch (Exception ex) {
			closeCacheStreams(cache, decompressedSoapMessage);
			throw new Fault("SOAP Body decompression failed", LOG, ex);
		}
	}

	private void closeCacheStreams(CachedOutputStream cache, CachedOutputStream decompressedSoapMessage){
		if (cache != null) {
			try {
				cache.close();
			} catch (IOException e) {
			}
		}
		if (decompressedSoapMessage != null) {
			try {
				decompressedSoapMessage.close();
			} catch (IOException e) {
			}
		}
	}
}
