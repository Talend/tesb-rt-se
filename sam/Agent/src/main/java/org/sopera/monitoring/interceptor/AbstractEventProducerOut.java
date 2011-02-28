package org.sopera.monitoring.interceptor;

import java.io.OutputStream;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.sopera.monitoring.producer.EventProducer;

/**
 * Out interceptor for creating an event.
 * 
 * @author cschmuelling
 * 
 * @param <T>
 */
public class AbstractEventProducerOut<T extends Message> extends
		AbstractEventProducer<T> {

	private static Logger logger = Logger
			.getLogger(AbstractEventProducerOut.class.getName());

	public AbstractEventProducerOut(InterceptorType type,
			EventProducer eventProducer) {
		super(Phase.PRE_STREAM, type, eventProducer);
	}

	@Override
	public void handleMessage(T message) throws Fault {
		if(!getType().equals(InterceptorType.OUT_FAULT) && !eventProducer.isLogMessageContent()){
			getEventProducer().handleMessage(message, getType(), null);
			return;
		}
		Set<Class<?>> formats = message.getContentFormats();
		final OutputStream os = message.getContent(OutputStream.class);
		if (os == null) {
			logger.info("There is no proceccable content in outgoing message.");
			getEventProducer().handleMessage(message, getType(), null);
			return;
		}

		final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(
				os);
		message.setContent(OutputStream.class, newOut);
		newOut.registerCallback(new EventCallback(message, os,
				getEventProducer(), getType()));
	}

	private class EventCallback implements CachedOutputStreamCallback {

		private final Message message;
		private final OutputStream origStream;
		private final EventProducer eventProducer;
		private final InterceptorType type;

		public EventCallback(final Message msg, final OutputStream os,
				EventProducer eventProducer, InterceptorType type) {
			this.message = msg;
			this.origStream = os;
			this.eventProducer = eventProducer;
			this.type = type;
		}

		public void onFlush(CachedOutputStream cos) {

		}

		public void onClose(CachedOutputStream cos) {

			String encoding = (String) message.get(Message.ENCODING);
			String ct = (String) message.get(Message.CONTENT_TYPE);

			StringBuilder builder = new StringBuilder();
			try {
				writePayload(builder, cos, encoding, ct);
			} catch (Exception ex) {
				// ignore
			}

			try {
				// empty out the cache
				cos.lockOutputStream();
				cos.resetOut(null, false);
			} catch (Exception ex) {
				// ignore
			}
			message.setContent(OutputStream.class, origStream);

			eventProducer.handleMessage(message, type, builder.toString());
		}
	}

	protected void writePayload(StringBuilder builder, CachedOutputStream cos,
			String encoding, String contentType) throws Exception {

		// TODO maybe add limit here
		if (StringUtils.isEmpty(encoding)) {
			cos.writeCacheTo(builder);
		} else {
			cos.writeCacheTo(builder, encoding);
		}
	}
}
