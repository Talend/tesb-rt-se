package org.talend.esb.job.converter.internal;

import java.io.File;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.talend.esb.job.converter.Converter;

/**
 * Unit tests on the default Talend job transformer implementation.
 */
public class ConverterImplTest extends TestCase {

    private ConverterImpl converter;
    private File talendJobZip;

    public void setUp() throws Exception {
        converter = new ConverterImpl();
        talendJobZip = new File(this.getClass().getClassLoader().getResource("OSGiTIF_0.1.zip").toURI());
    }

    /**
     * Unit test on the zip entry lookup with suffix.
     *
     * @throws Exception in case of the lookup failure.
     */
    public void testSearchEntriesWithSuffix() throws Exception {
        List<ZipEntry> entries = converter.searchEntriesWithSuffix(new ZipFile(talendJobZip), ".sh");
        assertEquals(1, entries.size());
    }

    /**
     * Unit test on the parse job class name.
     *
     * @throws Exception in case of parsing failure.
     */
    public void testParseJobClassName() throws Exception {
        ZipFile zip = new ZipFile(talendJobZip);
        List<ZipEntry> entries = converter.searchEntriesWithSuffix(zip, ".sh");
        String java = converter.parseJobClassName(zip.getInputStream(entries.get(0)));
        assertEquals("talenddemosjava.osgitif_0_1.OSGiTIF", java);
    }

    /**
     * Unit test on the java command lookup.
     *
     * @throws Exception in case of lookup failure.
     */
    public void testJavaCommandLookup() throws Exception {
        ZipFile zip = new ZipFile(talendJobZip);
        assertEquals("talenddemosjava.osgitif_0_1.OSGiTIF", converter.javaCommandLookup(zip));
    }

    /**
     * Unit test on the version from class name extraction.
     *
     * @throws Exception in case of extraction failure.
     */
    public void testExtractVersionFromClassName() throws Exception {
        String className = "talenddemosjava.osgitif_0_1.OSGiTIF";
        assertEquals("0.1", converter.extractVersionFromClassName(className));
    }

    /**
     * Unit test on the name from class name extraction.
     *
     * @throws Exception in case of extraction failure.
     */
    public void testExtractNameFromClassName() throws Exception {
        String className = "talenddemosjava.osgitif_0_1.OSGiTIF";
        assertEquals("OSGiTIF", converter.extractNameFromClassName(className));
    }

    /**
     * Unit test on the Talend job transform.
     *
     * @throws Exception in case of transformation failure.
     */
    public void testTransform() throws Exception {
        converter.convertToBundle(talendJobZip, true);
    }

}
