/*
 * #%L
 * Talend :: ESB :: Job :: Deployer
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
package org.talend.esb.job.deployer;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.job.converter.Converter;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A Talend Job deployment listener that listens for Talend Job zip deployments.
 */
public class JobDeploymentListener implements ArtifactUrlTransformer {

    private Converter converter;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDeploymentListener.class);

    public boolean canHandle(File artifact) {
        // only handle .zip files
        if (!artifact.getPath().endsWith(".zip")) {
            LOGGER.debug("Artifact {} is not a zip.", artifact.getName());
            return false;
        }
        // looking for a Talend zip structure
        // aka containing systemRoutines and userRoutines
        // and Default.properties
        ZipFile zip = null;
        try {
            zip = new ZipFile(artifact);
            if (JobDeploymentListener.findInZip(zip, "systemRoutines")
                && JobDeploymentListener.findInZip(zip, "userRoutines")
                && JobDeploymentListener.findInZip(zip, "Default.properties")) {
                LOGGER.debug("Talend job zip detected {}", artifact.getName());
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return false;
    }

    public URL transform(URL artifact) throws Exception {
        File jobZip = new File(artifact.toURI());
        converter.convertToBundle(jobZip, true);
        return null;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    protected static boolean findInZip(ZipFile zip, String name) {
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().contains(name)) {
                return true;
            }
        }
        return false;
    }

}
