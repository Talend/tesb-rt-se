package org.talend.esb.examples.ebook.jpasupport;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.service.coordinator.Coordinator;

@Singleton
public class CoordinationFeature extends AbstractFeature {

    @Inject @OsgiService
    Coordinator coordinator;
    
    public CoordinationFeature() {
        super();
    }
    
    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        CoordinationInterceptor in = new CoordinationInterceptor(coordinator);
        CoordinationEndInterceptor out = new CoordinationEndInterceptor();
        provider.getInInterceptors().add(in);
        provider.getOutInterceptors().add(out);
    }

}
