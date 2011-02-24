package org.talend.esb.locator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;

public class LocatorSelectionStrategy implements FailoverStrategy {

	private static final Logger LOG = Logger.getLogger(LocatorSelectionStrategy.class
			.getName());
	
	private ServiceLocator serviceLocator;
	
	private Random random = new Random();

	private Map<QName, String> primaryAddresses = new HashMap<QName, String>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAlternateAddresses(Exchange exchange) {
		QName serviceName = getServiceName(exchange);
		List<String> alternateAddresses= getEndpoints(serviceName);
		synchronized (this) {
			primaryAddresses.remove(serviceName);
		}
		return alternateAddresses;
	}

	@Override
	public String selectAlternateAddress(List<String> alternates) {
		String alternateAddress = null;
		if (alternates != null && ! alternates.isEmpty()) {
			int index = random.nextInt(alternates.size());
			alternateAddress = alternates.remove(index);
		}
		return alternateAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Endpoint> getAlternateEndpoints(Exchange exchange) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Endpoint selectAlternateEndpoint(List<Endpoint> alternates) {
		return null;
	}
	
	/**
	 * 
	 * @param exchange
	 * @return
	 */
	synchronized public String getPrimaryAddress(Exchange exchange) {
		QName serviceName = getServiceName(exchange);
		String primaryAddress = primaryAddresses.get(serviceName);

		if (primaryAddress == null) {
			List<String> availableAddresses = getEndpoints(serviceName);
			int index = random.nextInt(availableAddresses.size());
			primaryAddress = availableAddresses.get(index);
			primaryAddresses.put(serviceName, primaryAddress);
		}
		return primaryAddress;
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	private List<String> getEndpoints(QName serviceName) {
		List<String> endpoints = Collections.emptyList();
		try {
			endpoints = serviceLocator.lookup(serviceName);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to ServiceLocatorException", e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to InterruptedException", e);
			}
		}
		return endpoints;
	}
	
	private QName getServiceName(Exchange exchange) {
		return exchange.getEndpoint().getService().getName();
	}
}
