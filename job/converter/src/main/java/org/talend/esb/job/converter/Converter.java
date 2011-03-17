package org.talend.esb.job.converter;

import java.io.File;
import java.io.IOException;

/**
 * Describe a Talend job converter behaviors.
 */
public interface Converter {

    /**
     * Convert a Talend job from an exported zip file to an OSGi bundle jar.
     *
     * @param jobZip the source Talend job zip.
     * @param deleteJobZip if true, delete the source job zip after conversion.
     * @throws IOException in case of conversion error.
     */
    public void convertToBundle(File jobZip, boolean deleteJobZip) throws Exception;

    /**
     * Convert a Talend job from an exported zip file to an OSGi bundle jar.
     *
     * @param jobZip the source Talend job zip.
     * @param jobClassName the job class name.
     * @param jobName the job name.
     * @param deleteJobZip if true, delete the source job zip after conversion.
     * @throws IOException in case of conversion error.
     */
    public void convertToBundle(File jobZip, String jobClassName, String jobName, boolean deleteJobZip) throws Exception;

}
