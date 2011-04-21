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
package org.talend.esb.job.converter.internal;

import java.io.*;
import java.util.*;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.job.converter.Converter;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Jar;

/**
 * Default implementation of a job transformer.
 */
public class ConverterImpl implements Converter {

    private Logger logger = LoggerFactory.getLogger(ConverterImpl.class);

    public void convertToBundle(File sourceJob, String jobName, String jobClassName, boolean deleteSourceJob) throws Exception {
        logger.info("Converting Talend job into OSGi bundle ...");

        logger.debug("Creating Talend job OSGi jar ...");

        long timestamp = System.currentTimeMillis();
        File uncompressDir = new File("convert" + timestamp);
        logger.debug("Create working directory {}", uncompressDir.getPath());
        uncompressDir.mkdirs();

        String outputName = sourceJob.getName();
        if (outputName.length() > 0 && outputName.endsWith(".zip")) {
            outputName = outputName.substring(0, outputName.length()-4);
        }
        File osgiJobLocation = new File(sourceJob.getParentFile(), outputName + ".jar");

        logger.debug("Unzip Talend job {} ...", osgiJobLocation);
        ZipFile jobZipFile = new ZipFile(sourceJob);
        Enumeration<? extends ZipEntry> jobZipEntries = jobZipFile.entries();
        while (jobZipEntries.hasMoreElements()) {
            ZipEntry jobZipEntry = jobZipEntries.nextElement();
            if (!jobZipEntry.isDirectory() && jobZipEntry.getName().endsWith(".jar")) {

                logger.debug("Unzip {}", jobZipEntry.getName());

                String name = jobZipEntry.getName();
                int index = name.lastIndexOf("/");
                if (index != 0) {
                    name = name.substring(index);
                }

                InputStream inputStream = jobZipFile.getInputStream(jobZipEntry);
                FileOutputStream fos = new FileOutputStream(new File(uncompressDir, name));
                copyInputStream(inputStream, fos);
                inputStream.close();
                fos.flush();
                fos.close();
            }
        }

        logger.debug("Get the job class name from the Talend job zip");
        jobClassName = this.javaCommandLookup(jobZipFile);
        logger.debug("Get the job name from the class name");
        jobName = this.extractNameFromClassName(jobClassName);
        logger.debug("Get the job version from the class name");
        String jobVersion = this.extractVersionFromClassName(jobClassName);

        ZipOutputStream resourcesZip = new ZipOutputStream(new FileOutputStream(new File(uncompressDir, "resources_" + timestamp + ".zip")));

        if (jobName != null && jobClassName != null) {
            logger.debug("Append OSGi blueprint descriptor");
            ZipEntry blueprint = new ZipEntry("OSGI-INF/blueprint/job.xml");
            resourcesZip.putNextEntry(blueprint);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resourcesZip));
            BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/OSGI-INF/blueprint/job.xml")));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("@JOBNAME@", jobName);
                line = line.replaceAll("@JOBCLASSNAME@", jobClassName);
                writer.write(line);
                writer.newLine();
            }
            reader.close();
            writer.flush();
            writer.close();
        }

        logger.debug("Close the resources zip");
        resourcesZip.flush();
        resourcesZip.close();

        logger.debug("Creating Talend bundle jar (using BND)");
        Builder builder = new Builder();
        builder.setProperty("Bundle-Name", jobName);
        builder.setProperty("Bundle-SymbolicName", jobName);
        builder.setProperty("Bundle-Version", jobVersion);
        builder.setProperty("Export-Package", "!routines.system*,*");
        builder.setProperty("Import-Package", "routines.system*;resolution:=optional,*;resolution:=optional");
        builder.setProperty("Private-Package", "routines.system*");
        logger.debug("Iterate in the working directory");
        File[] files = uncompressDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".jar") ||
                    files[i].getName().endsWith(".zip")) {
                logger.debug("Add jar file {}", files[i].getName());
                builder.addClasspath(files[i]);
            }
            files[i].delete();
        }

        Jar jar = builder.build();
        Manifest manifest = jar.getManifest();
        jar.write(osgiJobLocation);

        logger.debug("Delete working directory {}", uncompressDir.getPath());
        uncompressDir.delete();

        if (deleteSourceJob) {
            logger.debug("Delete source job {}", sourceJob);
            sourceJob.delete();
        }
    }

    public void convertToBundle(File sourceJob, boolean deleteSourceJob) throws Exception {
        this.convertToBundle(sourceJob, null, null, deleteSourceJob);
    }

    /**
     * Looking for a shell script in a Talend job zip file
     * and extract the java command line.
     *
     * @param zip the Talend job zip.
     * @return the first java command found the shell scripts.
     * @throws Exception in case of lookup error.
     */
    protected String javaCommandLookup(ZipFile zip) throws Exception {
        List<ZipEntry> shEntries = this.searchEntriesWithSuffix(zip, ".sh");
        if (shEntries.size() > 0) {
            String java = this.parseJobClassName(zip.getInputStream(shEntries.get(0)));
            if (java != null) {
                return java;
            }
        }
        return null;
    }

    /**
     * Extract the Talend job version from the job class name.
     *
     * @param className the full qualified name of the job class.
     * @return the extracted version
     */
    protected String extractVersionFromClassName(String className) {
        if (className.lastIndexOf('.') != -1) {
            className = className.substring(0, className.lastIndexOf('.'));
            if (className.lastIndexOf('.') != -1) {
                className = className.substring(className.lastIndexOf('.'));
                if (className.indexOf('_') != -1) {
                    className = className.substring(className.indexOf('_') + 1);
                    className = className.replace('_', '.');
                    return className;
                }
            }
        }
        return "0.0.0";
    }

    /**
     * Extract the Talend job name from the job class name.
     *
     * @param className the full qualified name of the job class.
     * @return the extracted name.
     */
    protected String extractNameFromClassName(String className) {
        if (className.lastIndexOf('.') != -1) {
            return className.substring(className.lastIndexOf('.') + 1);
        }
        return null;
    }

    /**
     * Looking for all entries with a given suffix.
     *
     * @param zip the zip file to search into.
     * @param suffix the entry name suffix.
     * @return a list containing zip entries matching the given suffix.
     * @throws Exception in case of search error.
     */
    protected List<ZipEntry> searchEntriesWithSuffix(ZipFile zip, String suffix) throws Exception {
        ArrayList<ZipEntry> entriesWithSuffix = new ArrayList<ZipEntry>();
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(suffix)) {
                entriesWithSuffix.add(entry);
            }
        }
        return entriesWithSuffix;
    }

    /**
     * Looking for a Talend job java class name.
     *
     * @param inputStream the input stream where to look for Talend job java class name.
     * @return the Talend java class nameor null if no java class found.
     * @throws Exception in case of lookup failure.
     */
    protected String parseJobClassName(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("java")) {
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                if (tokenizer.hasMoreTokens()) {
                    // ignore the starting java runner
                    // and the classpath
                    tokenizer.nextToken();
                    // iterate in java args
                    String previous = null;
                    if (tokenizer.hasMoreTokens()) {
                        previous = tokenizer.nextToken();
                    }
                    while (tokenizer.hasMoreTokens()) {
                        String arg = tokenizer.nextToken();
                        if (!arg.startsWith("-")) {
                            if (previous != null && !previous.contains("-cp")) {
                                // it's the first "non arg" which is not the cp
                                bufferedReader.close();
                                return arg;
                            }
                        }
                        previous = arg;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Just copy from an input stream to an output stream.
     *
     * @param in the input stream.
     * @param out the output stream.
     * @throws IOException in case of copy failure.
     */
    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int len = in.read(buffer);
        while (len >= 0) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }
    
    private static boolean deleteDir(File path) {
    	if( path.exists() ) {
    		File[] files = path.listFiles();
    		for(int i=0; i<files.length; i++) {
    			if(files[i].isDirectory()) {
    				deleteDir(files[i]);
    			}
    			else {
    				files[i].delete();
    			}
    		}
    	}
    	return( path.delete() );
    }


}
