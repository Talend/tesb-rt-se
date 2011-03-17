package org.talend.esb.job.deployer;

import junit.framework.TestCase;

import java.io.File;
import java.util.zip.ZipFile;

public class JobDeploymentListenerTest extends TestCase {

    public void testFindInZip() throws Exception {
        ZipFile file = new ZipFile(new File(this.getClass().getClassLoader().getResource("OSGiTIF_0.1.zip").toURI()));
        assertEquals(JobDeploymentListener.findInZip(file, "Default.properties"), true);
        assertEquals(JobDeploymentListener.findInZip(file, "systemRoutines.jar"), true);
        assertEquals(JobDeploymentListener.findInZip(file, "userRoutines.jar"), true);
        assertEquals(JobDeploymentListener.findInZip(file, "foobar.foo"), false);
    }

}
