package org.talend.esb.job.converter.internal;

import junit.framework.TestCase;
import org.talend.esb.job.converter.Converter;

import java.io.File;

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
        converter.convertToBundle(new File(this.getClass().getClassLoader().getResource("OSGiTIF_0.1.zip").toURI()), new File("target/test.jar"), null);
    }

}
