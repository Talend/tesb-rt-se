package org.talend.esb.sam.agent.wiretap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 *  Wraps a Writer as an OutputStream.
 */
public class WriterOutputStream extends OutputStream {

    private Writer writer;
    private String encoding;
    private byte[] buffer = new byte[1];

    public WriterOutputStream(Writer writer) {
        this.writer = writer;
    }

    public WriterOutputStream(Writer writer, String encoding) {
        this.writer = writer;
        this.encoding = encoding;
    }

    public void write(byte[] b) throws IOException {
        if (null == encoding) {
            writer.write(new String(b));
        } else {
            writer.write(new String(b, encoding));
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (null == encoding) {
            writer.write(new String(b, off, len));
        } else {
            writer.write(new String(b, off, len, encoding));
        }
    }

    public synchronized void write(int b) throws IOException {
        buffer[0] = (byte) b;
        write(buffer);
    }

    public void close() throws IOException {
        writer.close();
        writer = null;
        encoding = null;
    }

    public void flush() throws IOException {
        writer.flush();
    }
}
