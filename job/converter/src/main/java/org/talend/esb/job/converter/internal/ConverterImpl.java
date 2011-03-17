package org.talend.esb.job.converter.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    public void convertToBundle(File sourceJob, File osgiJobLocation, File workingDirectory) throws IOException {
        logger.info("Converting Talend job into OSGi bundle ...");

        logger.debug("Creating Talend job OSGi jar ...");

        long timestamp = System.currentTimeMillis();
        File uncompressDir = new File(workingDirectory, "convert" + timestamp);
        logger.debug("Create working directory {}", uncompressDir.getPath());
        uncompressDir.mkdirs();

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

        logger.debug("Creating Talend bundle jar (using BND)");
        Builder builder = new Builder();
        builder.setProperty("Export-Package", "!routines*,*");
        builder.setProperty("Private-Package", "routines*");
        builder.setProperty("Import-Package", "*;resolution:=optional");
        logger.debug("Iterate in the working directory");
        File[] files = uncompressDir.listFiles();
        for (File file : files) {
            builder.addClasspath(file);
		}
        try {
            Jar jar = builder.build();
            jar.write(osgiJobLocation);
            jar.close();
            builder.close();
        } catch (Exception e) {
            throw new IOException("Can't create Talend job bundle jar", e);
        }

        logger.debug("Delete working directory {}", uncompressDir.getPath());
        deleteDir(uncompressDir);
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
