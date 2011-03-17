package org.talend.esb.job.converter.internal;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.talend.esb.job.converter.Converter;

/**
 * Unit tests on the default Talend job transformer implementation.
 */
public class ConverterImplTest extends TestCase {

    private Converter converter;

    public void setUp() throws Exception {
        converter = new ConverterImpl();
    }

    /**
     * Unit test on the Talend job transform.
     *
     * @throws Exception in case of transformation failure.
     */
    public void testTransform() throws Exception {
    	File workdir = new File("target/workdir");
    	workdir.mkdirs();
        converter.convertToBundle(new File(this.getClass().getClassLoader().getResource("OSGiTIF_0.1.zip").toURI()), new File("target/test.jar"), 
        		workdir);
        String[] foundFiles = workdir.list();
        Assert.assertEquals("The working dir should be cleaned up after the run. Files found: ", 0, foundFiles.length);
    }

}
