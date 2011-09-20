package org.talend.esb.job.management;

import java.util.List;

/**
 * MBean to manipulate Talend jobs and routes.
 */
public interface JobMBean {

    /**
     * List the Talend jobs.
     *
     * @return the list of Talend jobs.
     * @throws Exception
     */
    List<String> jobs() throws Exception;

    /**
     * List the Talend routes.
     *
     * @return the list of Talend routes.
     * @throws Exception
     */
    List<String> routes() throws Exception;

    /**
     * List the Talend data services.
     *
     * @return the list of Talend data services.
     * @throws Exception
     */
    List<String> services() throws Exception;

    /**
     * Start a Talend job.
     *
     * @param jobName the name of the job to start.
     * @param args the arguments to provide to the job.
     * @throws Exception
     */
    void startJob(String jobName, String args) throws Exception;

    /**
     * Start a Talend route.
     *
     * @param routeName the name of the route to start.
     * @param args the arguments to provide to the route.
     * @throws Exception
     */
    void startRoute(String routeName, String args) throws Exception;

    /**
     * Start a Talend data service.
     *
     * @param serviceName the name of the data service to start.
     * @throws Exception
     */
    void startService(String serviceName) throws Exception;

    /**
     * Stop a Talend job.
     *
     * @param jobName the name of the Talend job to stop.
     * @throws Exception
     */
    void stopJob(String jobName) throws Exception;

    /**
     * Stop a Talend route.
     * .
     * @param routeName the name of the Talend route to stop.
     * @throws Exception
     */
    void stopRoute(String routeName) throws Exception;

    /**
     * Stop a Talend data service.
     *
     * @param serviceName the name of the Talend data service to stop.
     * @throws Exception
     */
    void stopService(String serviceName) throws Exception;

}
