package org.talend.esb.sam.agent.eventadmin.translator.subject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

public class SubjectExtractorTest {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Test
    public void testDTDInjection() throws Exception {
        String xmlDoc = loadFile("target/test-classes/DTDInjectionSample.xml");
        if (xmlDoc.trim().isEmpty()) {
            throw new IllegalStateException("the sample document is empty.");
        }

        SubjectExtractor extractor = new SubjectExtractor();
        String subject = extractor.getSubject(xmlDoc, new SamlTokenSubjectExtractor());
        Assert.assertEquals(subject, null);
    }
    
    private String loadFile(String fileName) throws Exception {
        File f = new File(fileName);
        long fileSize = f.length();
        if (fileSize == 0L) {
            throw new IOException("File " + fileName + " not found or empty.");
        }
        if (fileSize > ((long) Integer.MAX_VALUE)) {
            throw new IOException("File " + fileName + " is too big.");
        }
        ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
        FileInputStream fis = new FileInputStream(f);
        try {
            FileChannel fc = fis.getChannel();
            fc.read(buffer);
        } finally {
            fis.close();
        }

        buffer.rewind();
        return UTF8.decode(buffer).toString();
    }
}
