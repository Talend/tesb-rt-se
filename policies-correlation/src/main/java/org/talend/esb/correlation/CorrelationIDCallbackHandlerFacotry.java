package org.talend.esb.correlation;

public class CorrelationIDCallbackHandlerFacotry {

	public static CorrelationIDCallbackHandler createHandler(final String correlationId) {
		return new CorrelationIDCallbackHandler() {
			
			@Override
			public String getCorrelationId() {
				return correlationId;
			}
		};
	}
}
