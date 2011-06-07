package org.talend.esb.job.controller.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import routines.system.api.ESBEndpointInfo;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class TalendJobServiceListener implements ServiceListener{

    private TalendJobLauncher jobLauncher = null;
    
    public TalendJobServiceListener(TalendJobLauncher jobLauncher){
        this.jobLauncher = jobLauncher;
    }
    
      @Override
    public void serviceChanged(ServiceEvent event) {
        if(event.getType() == ServiceEvent.UNREGISTERING){
            String type = (String)event.getServiceReference().getProperty("type");
            if(type != null && type.equalsIgnoreCase("job")) {
                BundleContext bundleContext = event.getServiceReference().getBundle().getBundleContext();
                TalendJob talendJob = (TalendJob)bundleContext.getService(event.getServiceReference());
                if(talendJob instanceof TalendESBJob){
                    final ESBEndpointInfo endpoint = ((TalendESBJob)talendJob).getEndpoint();
                    if (null != endpoint) {
                        jobLauncher.destroyESBProvider(endpoint.getEndpointProperties());
                    }
                }
            }
        }
        
    }

}
