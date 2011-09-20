package org.talend.esb.job.management.internal;

import org.talend.esb.job.controller.Controller;
import org.talend.esb.job.management.JobMBean;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import java.util.List;

/**
 * Implementation of the Talend Job MBean.
 */
public class JobMBeanImpl extends StandardMBean implements JobMBean {

    private Controller controller;

    public JobMBeanImpl() throws NotCompliantMBeanException {
        super(JobMBean.class);
    }

    public Controller getController() {
        return this.controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public List<String> jobs() throws Exception {
        return controller.listJobs();
    }

    public List<String> routes() throws Exception {
        return controller.listRoutes();
    }

    public List<String> services() throws Exception {
        return controller.listServices();
    }

    public void startJob(String jobName, String args) throws Exception {
        String[] arguments = null;
        if (args != null) {
            arguments = args.split(" ");
        }
        if (arguments == null) {
            arguments = new String[0];
        }
        controller.run(jobName, arguments);
    }

    public void startRoute(String routeName, String args) throws Exception {
        String[] arguments = null;
        if (args != null) {
            arguments = args.split(" ");
        }
        if (arguments == null) {
            arguments = new String[0];
        }
        controller.run(routeName, arguments);
    }

    public void startService(String serviceName) throws Exception {
        controller.runService(serviceName);
    }

    public void stopJob(String jobName) throws Exception {
        controller.stop(jobName);
    }

    public void stopRoute(String routeName) throws Exception {
        controller.stop(routeName);
    }

    public void stopService(String serviceName) throws Exception {
        controller.stopService(serviceName);
    }

}
