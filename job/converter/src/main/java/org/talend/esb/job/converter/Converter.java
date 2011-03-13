package org.talend.esb.job.converter;

import java.io.File;
import java.io.IOException;

/**
 * Describe a Talend job converter behaviors.
 */
public interface Converter {

    /**
     * Convert a Talend job from an exported zip file to a OSGi bundle jar.
     *
     * @param jobZip the source Talend job zip.
     * @param bundleJar the destination OSGi bundle jar.
     * @param workingDirectory working directory for the converter (could be null).
     * @throws IOException in case of conversion error.
     */
    public void convertToBundle(File jobZip, File bundleJar, File workingDirectory) throws IOException;

}
