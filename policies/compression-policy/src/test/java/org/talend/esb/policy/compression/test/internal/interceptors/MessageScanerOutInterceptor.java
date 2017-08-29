package org.talend.esb.policy.compression.test.internal.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.MessageSenderInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.compression.impl.CompressionOutInterceptor;
import org.talend.esb.policy.compression.impl.internal.CompressionConstants;

public class MessageScanerOutInterceptor extends
		AbstractPhaseInterceptor<Message> {

	private static final Logger LOG = LogUtils
			.getL7dLogger(MessageScanerOutInterceptor.class);

	private CompressionCachedOutputStreamCallback callback = null;

	private boolean throwPatternNotMatchedException = false;

	private String pattern = null;

	public MessageScanerOutInterceptor() {
		super(Phase.PREPARE_SEND);
		addAfter(MessageSenderInterceptor.class.getName());
		addBefore(CompressionOutInterceptor.class.getName());
		pattern = CompressionConstants.COMPRESSION_WRAPPER_TAG_LOCAL_NAME;
	}

	public MessageScanerOutInterceptor(String pattern) {
		super(Phase.PREPARE_SEND);
		addAfter(MessageSenderInterceptor.class.getName());
		addBefore(CompressionOutInterceptor.class.getName());
		this.pattern = pattern;
	}

	public MessageScanerOutInterceptor(String pattern,
			boolean throwPatternNotMatchedException) {
		super(Phase.PREPARE_SEND);
		addAfter(MessageSenderInterceptor.class.getName());
		addBefore(CompressionOutInterceptor.class.getName());
		this.pattern = pattern;
		this.throwPatternNotMatchedException = throwPatternNotMatchedException;
	}

	public void handleMessage(Message message) throws Fault {
		try {
			wrapOriginalOutputStream(message);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new Fault(e);
		}
	}

	public boolean isPatternMatched() {

		return callback.isPatternMatched();
	}

	public void wrapOriginalOutputStream(Message message) throws Fault {
		// remember the original output stream, we will write compressed
		// data to this later
		OutputStream os = message.getContent(OutputStream.class);
		if (os == null) {
			return;
		}

		// new stream to cache the original os
		CachedOutputStream wrapper = new CachedOutputStream();
		callback = new CompressionCachedOutputStreamCallback(os, pattern,
				throwPatternNotMatchedException);
		wrapper.registerCallback(callback);
		message.setContent(OutputStream.class, wrapper);
	}

	public static class CompressionCachedOutputStreamCallback implements
			CachedOutputStreamCallback {

		private final OutputStream origOutStream;

		private boolean patternMatched = false;

		private String pattern;

		private boolean throwPatternNotMatchedException = false;

		public CompressionCachedOutputStreamCallback(OutputStream os,
				String pattern, boolean throwPatternNotMatchedException) {
			this.origOutStream = os;
			this.pattern = pattern;
			this.throwPatternNotMatchedException = throwPatternNotMatchedException;
		}

		@Override
		public void onFlush(CachedOutputStream wrapper) {
		}

		@Override
		public void onClose(CachedOutputStream wrapper) {
			try {

				LOG.info("Checking request");

				if (pattern == null || pattern.isEmpty()) {
					LOG.info("Pattern is empty");
					return;
				}

				// InputStream with actual SOAP message
				InputStream wrappedIS = wrapper.getInputStream();

				Scanner scanner = new Scanner(wrappedIS);
				try {
					scanner.findWithinHorizon(pattern, 0);
					scanner.match();
					patternMatched = true;
				} catch (Exception ex) {
					patternMatched = false;
					if (throwPatternNotMatchedException) {
						throw new RuntimeException(
								"Message does not match with pattern {"
										+ pattern + "}");
					}
				} finally {
					scanner.close();
				}

				wrappedIS.reset();
				IOUtils.copy(wrappedIS, origOutStream);

			} catch (Exception e) {
				LOG.log(Level.SEVERE,
						"Processing of outgoing SOAP message has failed ", e);
			} finally {
				try {
					origOutStream.flush();
					origOutStream.close();
				} catch (IOException e) {
					LOG.warning("Cannot close stream after compression: "
							+ e.getMessage());
				} catch (IllegalStateException e) {
					// Exception caught and logged, as it is frequently hiding a
					// previously thrown exception.
					LOG.log(Level.SEVERE,
							"Irregular attempt at closing output stream ", e);
				}
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
}
