package org.talend.esb.job.converter.internal;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.job.converter.Converter;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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
        Enumeration<ZipEntry> jobZipEntries = (Enumeration<ZipEntry>) jobZipFile.entries();
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

        logger.debug("Create job.properties");
        Properties jobProperties = new Properties();
        jobProperties.setProperty("version", "1.0");

        logger.debug("Add job.properties in the resources zip");
        ZipOutputStream resourcesZip = new ZipOutputStream(new FileOutputStream(new File(uncompressDir, "resources_" + timestamp + ".zip")));

        if (jobName != null && jobClassName != null) {
            logger.debug("Update job.properties");
            jobProperties.setProperty("job.blueprint", "true");
            jobProperties.setProperty("job.name", jobName);
            jobProperties.setProperty("job.class.name", jobClassName);
            logger.debug("Append OSGi blueprint descriptor");
            // TODO
            logger.debug("Replace in the OSGi blueprint descriptor");
            // TODO
            logger.debug("Add OSGi blueprint descriptor in the resources zip");
            ZipEntry resourceBlueprint = new ZipEntry("resources/OSGI-INF/blueprint/job.xml");
            resourcesZip.putNextEntry(resourceBlueprint);
            copyInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/OSGI-INF/blueprint/job.xml"), resourcesZip);
        }

        logger.debug("Write job.properties");
        File jobPropertiesFile = new File(uncompressDir, "job.properties");
        jobProperties.store(new FileOutputStream(jobPropertiesFile), null);

        logger.debug("Add job.properties in the resources zip");
        ZipEntry resourceJobProperties = new ZipEntry("META-INF/job.properties");
        resourcesZip.putNextEntry(resourceJobProperties);
        copyInputStream(new FileInputStream(jobPropertiesFile), resourcesZip);

        logger.debug("Close the resources zip");
        resourcesZip.flush();
        resourcesZip.close();

        logger.debug("Creating Talend bundle jar (using BND)");
        Builder builder = new Builder();
        builder.setProperty("Bundle-Name", outputName);
        builder.setProperty("Bundle-SymbolicName", outputName);
        // TODO extract the bundle version from the file name
        builder.setProperty("Bundle-Version", "4.0");
        builder.setProperty("Export-Package", "!routines*,*");
        builder.setProperty("Private-Package", "routines*");
        builder.setProperty("Import-Package", "*;resolution:=optional");
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

}
