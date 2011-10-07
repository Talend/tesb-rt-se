package org.talend.esb.job.controller.internal;

import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;

public class OperationTask extends RuntimeESBProviderCallback implements JobTask, GenericOperation {

    private TalendESBJob job;
    
    private String[] arguments;

    private ESBEndpointRegistry endpointRegistry;
    
    public OperationTask(TalendESBJob job, String[] arguments, ESBEndpointRegistry endpointRegistry) {
        this.job = job;
        this.arguments = arguments;
        this.endpointRegistry = endpointRegistry;
    }

    public void run() {
        job.setProviderCallback(this);
        job.setEndpointRegistry(endpointRegistry);

        while(true) {
            if (Thread.interrupted()) {
                prepareStop();
            }
            job.runJobInTOS(arguments);
            if (isStopped()) {
                return;
            }
        }
    }

}
