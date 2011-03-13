package org.talend.esb.job.converter.internal;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.job.converter.Converter;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Default implementation of a job transformer.
 */
public class ConverterImpl implements Converter {

    private Logger logger = LoggerFactory.getLogger(ConverterImpl.class);

    public void convertToBundle(File sourceJob, File osgiJobLocation, File workingDirectory) throws IOException {
        logger.info("Converting Talend job into OSGi bundle ...");

        logger.debug("Creating Talend job OSGi jar ...");

        long timestamp = System.currentTimeMillis();
        File uncompressDir = new File(workingDirectory, "convert" + timestamp);
        logger.debug("Create working directory {}", uncompressDir.getPath());
        uncompressDir.mkdirs();

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

        logger.debug("Creating Talend bundle jar (using BND)");
        Builder builder = new Builder();
        builder.setProperty("Export-Package", "!routines*,*");
        builder.setProperty("Private-Package", "routines*");
        logger.debug("Iterate in the working directory");
        File[] files = uncompressDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            builder.addClasspath(files[i]);
            files[i].delete();
        }

        try {
            Jar jar = builder.build();
            Manifest manifest = jar.getManifest();
            jar.write(osgiJobLocation);
        } catch (Exception e) {
            throw new IOException("Can't create Talend job bundle jar", e);
        }

        logger.debug("Delete working directory {}", uncompressDir.getPath());
        uncompressDir.delete();
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
