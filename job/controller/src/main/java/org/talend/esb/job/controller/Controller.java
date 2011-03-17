package org.talend.esb.job.controller;

import org.osgi.framework.Bundle;
import routines.TalendJob;

import java.util.List;

/**
 * Interface describing Talend job controller behaviors.
 */
public interface Controller {

    public void start(Bundle job) throws Exception;

    public void stop(Bundle job) throws Exception;

    public List<Bundle> list() throws Exception;

    public List<TalendJob> listServices() throws Exception;

}
