package org.talend.esb.job.controller.internal;

import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.TalendESBJob;

public class OperationTask extends RuntimeESBProviderCallback implements Runnable, GenericOperation {

    private TalendESBJob job;

    private JobLauncherImpl jobLauncher;
    
    public OperationTask(TalendESBJob job, boolean requestResponse, JobLauncherImpl jobLauncher) {
        super(requestResponse);
        
        this.job = job;
        this.jobLauncher = jobLauncher;
    }
    
    public void run() {
        job.setProviderCallback(this);
        job.setEndpointRegistry(jobLauncher);

        while(true) {
            if (Thread.interrupted()) {
                prepareStop();
            }
            job.runJobInTOS(new String [0]);
            if (isStopped()) {
                return;
            }
        }
    }

}
