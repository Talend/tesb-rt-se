package org.talend.esb.policy.compression.test.internal.interceptors;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import javax.xml.stream.XMLStreamException;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.compression.impl.CompressionInInterceptor;
import org.talend.esb.policy.compression.impl.internal.CompressionHelper;

public class MessageScanerInInterceptor extends
		AbstractPhaseInterceptor<Message> {

	private static final Logger LOG = LogUtils
			.getL7dLogger(MessageScanerInInterceptor.class);

	private String pattern = null;

	private boolean patternMatched = false;

	private boolean throwPatternNotMatchedException = false;

	public MessageScanerInInterceptor() {
		super(Phase.RECEIVE);
		addBefore(CompressionInInterceptor.class.getName());
	}

	public MessageScanerInInterceptor(String pattern) {
		super(Phase.RECEIVE);
		addBefore(CompressionInInterceptor.class.getName());
		this.pattern = pattern;
	}

	public MessageScanerInInterceptor(String pattern,
			boolean throwPatternNotMatchedException) {
		super(Phase.RECEIVE);
		addBefore(CompressionInInterceptor.class.getName());
		this.pattern = pattern;
		this.throwPatternNotMatchedException = throwPatternNotMatchedException;
	}

	public void handleMessage(Message message) throws Fault {
		try {

			// Perform compression
			checkMessage(message);

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new Fault(e);
		}
	}

	public void checkMessage(Message message) throws Fault {
		if (isGET(message)) {
			return;
		}

		try {
			LOG.info("Checking response");

			if (pattern == null || pattern.isEmpty()) {
				LOG.info("Pattern is empty");
				return;
			}

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

			Scanner scanner = new Scanner(cache.getInputStream());

			MatchResult patternPosition = null;
			try {
				patternPosition = CompressionHelper.loadSoapBodyContent(
						soapBodyContent, scanner, pattern);
				if (patternPosition == null) {
					patternMatched = false;

					if (throwPatternNotMatchedException) {
						throw new RuntimeException(
								"Message does not match with pattern {"
										+ pattern + "}");
					}
				} else {
					patternMatched = true;
				}
			} catch (XMLStreamException e) {
				throw new Fault("Can not load SOAP Body content for scaner",
						LOG, e, e.getMessage());
			}

		} catch (Exception ex) {
			throw new Fault("SOAP Body decompresion failed", LOG, ex);
		}

	}

	public boolean isPatternMatched() {
		return patternMatched;
	}
	
	public void setThrowPatternNotMatchedException(
			boolean throwPatternNotMatchedException) {
		this.throwPatternNotMatchedException = throwPatternNotMatchedException;
	}

}
