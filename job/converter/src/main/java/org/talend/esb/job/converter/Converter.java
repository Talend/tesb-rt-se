/*
 * #%L
 * Talend :: ESB :: Job :: Converter
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
