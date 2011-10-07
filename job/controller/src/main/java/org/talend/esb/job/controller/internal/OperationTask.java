package org.talend.esb.job.controller.internal;

import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.TalendESBJob;

public class OperationTask extends RuntimeESBProviderCallback implements JobTask, GenericOperation {

    private TalendESBJob job;
    
    private String[] arguments;
    
    public OperationTask(TalendESBJob job, String[] arguments) {
        this.job = job;
        this.arguments = arguments;
    }

    public void run() {
        job.setProviderCallback(this);

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
