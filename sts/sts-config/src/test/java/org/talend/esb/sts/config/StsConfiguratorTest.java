package org.talend.esb.sts.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.junit.Test;

public class StsConfiguratorTest {

	@Test
	public void testStsConfiguratorUseMessageLogging() throws Exception {

		StsConfigurator config = new StsConfigurator(getBus());

		config.setUseMessageLogging("true");

		config.init();

	}

	@Test
	public void testStsConfigurator() throws Exception {

		StsConfigurator config = new StsConfigurator(getBus());

		config.setUseMessageLogging(null);

		config.init();

	}

	private Bus getBus() {
		Collection<Feature> features = new ArrayList<Feature>();
		features.add(new LoggingFeature());

		List<Interceptor<? extends Message>> inInterceptors = new ArrayList<Interceptor<? extends Message>>();
		inInterceptors.add(new LoggingInInterceptor());

		List<Interceptor<? extends Message>> inFaultInterceptors = new ArrayList<Interceptor<? extends Message>>();
		inFaultInterceptors.add(new LoggingInInterceptor());

		List<Interceptor<? extends Message>> outInterceptors = new ArrayList<Interceptor<? extends Message>>();
		outInterceptors.add(new LoggingOutInterceptor());

		List<Interceptor<? extends Message>> outFaultInterceptors = new ArrayList<Interceptor<? extends Message>>();
		outFaultInterceptors.add(new LoggingOutInterceptor());

		Bus bus = createMock(Bus.class);
		expect(bus.getFeatures()).andReturn(features).anyTimes();
		expect(bus.getInInterceptors()).andReturn(inInterceptors).anyTimes();
		expect(bus.getOutInterceptors()).andReturn(outInterceptors).anyTimes();
		expect(bus.getInFaultInterceptors()).andReturn(inFaultInterceptors).anyTimes();
		expect(bus.getOutFaultInterceptors()).andReturn(outFaultInterceptors).anyTimes();

		replay(bus);

		return bus;
	}

}