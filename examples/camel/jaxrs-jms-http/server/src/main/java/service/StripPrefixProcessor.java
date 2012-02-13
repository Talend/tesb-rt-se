/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service;

import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * <p>
 * Currently the CXF JAX-RS code determines the base path of a JAX-RS endpoint by simply
 * looking at the defined address property. As the address is "jms:// ..." or "jetty://"
 * when using camel this does not work. So we cut off the base path from the URL before sending to CXF.
 * </p>
 */
public class StripPrefixProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String path = in.getHeader(Exchange.HTTP_PATH, String.class);
		String base = in.getHeader(Exchange.HTTP_BASE_URI, String.class);
		if (base == null) {
			base = exchange.getFromEndpoint().getEndpointUri();
                }
		if (base.startsWith("http")) {
		    // In case of a jetty http endpoint we only want the path part
		    URL url = new URL(base);
		    base = url.getPath();
		}
		if (path != null && path.startsWith(base)) {
		    // CXF expects to get only the part of the path after the base
		    String relPath = path.substring(base.length());
		    in.setHeader(Exchange.HTTP_PATH, relPath);
		}
	}

}
