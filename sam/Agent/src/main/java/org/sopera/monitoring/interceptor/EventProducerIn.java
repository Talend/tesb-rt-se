package org.sopera.monitoring.interceptor;

import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.sopera.monitoring.producer.EventProducer;

/**
 * In interceptor for receiving a message and creating an event.
 * 
 * @author cschmuelling
 * 
 * @param <T>
 */
public class EventProducerIn extends AbstractEventProducer {

	private static Logger logger = Logger
			.getLogger(EventProducerIn.class.getName());

	private static final String CONTENT_KEY = EventProducerIn.class
			.getName() + "_CONTENT_KEY";

	/**
	 * A second intercepter is needed for receiving all event data. In the
	 * receive phase the content is logged and in the second intercepter it will
	 * be processed.
	 */
	private SecondEventProducerIn secondPhaseProducer;

	public EventProducerIn(InterceptorType type,
			EventProducer eventProducer) {
		super(Phase.RECEIVE, type, eventProducer);
		this.secondPhaseProducer = new SecondEventProducerIn(eventProducer,
				type);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		logger.info("Eventproducer called");

		// Content cannot be read in fault_in interceptor
		if (!getType().equals(InterceptorType.IN_FAULT)) {
			String content = getContent(message);

			// Store message content in context
			// Incoming content can'not be read in InterceptorType FAULT_IN.
			// Read it in IN
			if (message.get(CONTENT_KEY) == null
					|| "".equals(message.get(CONTENT_KEY)))
				message.put(CONTENT_KEY, content);
		}
		message.getInterceptorChain().add(secondPhaseProducer);
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

	protected String getContent(Message message) {
		logger.finest("Try to read content in in interceptor");

		String ct = (String) message.get(Message.CONTENT_TYPE);
		String encoding = (String) message.get(Message.ENCODING);

		InputStream is = message.getContent(InputStream.class);
		if (is != null) {
			CachedOutputStream bos = new CachedOutputStream();
			try {
				IOUtils.copy(is, bos);

				bos.flush();
				is.close();

				message.setContent(InputStream.class, bos.getInputStream());

				StringBuilder builder = new StringBuilder();
				writePayload(builder, bos, encoding, ct);

				bos.close();

				return builder.toString();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Handle the event processing
	 * 
	 * @author cschmuelling
	 * 
	 */
	private class SecondEventProducerIn extends AbstractEventProducer {

		private final EventProducer producer;

		public SecondEventProducerIn(EventProducer producer,
				InterceptorType type) {
			super(Phase.PRE_INVOKE, type, producer);
			this.producer = producer;
		}

		@Override
		public void handleMessage(Message message) throws Fault {
			if (InterceptorType.IN_FAULT.equals(getType()) || eventProducer.isLogMessageContent())
				producer.handleMessage(message, getType(),
						getContentFromContext(message));
			else
				producer.handleMessage(message, getType(), null);
		}

		private String getContentFromContext(Message message) {
			Object object = message.get(CONTENT_KEY);
			if (object != null && object instanceof String) {
				return (String) object;
			} else
				return null;
		}
	}
}
