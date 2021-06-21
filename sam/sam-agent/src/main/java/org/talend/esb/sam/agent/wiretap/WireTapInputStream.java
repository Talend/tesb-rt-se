/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.sam.agent.wiretap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The Class WireTapInputStream used to
 * wiretap the input content.
 */
public class WireTapInputStream extends InputStream {
    private InputStream origStream;
    private OutputStream os;

    /**
     * Instantiates a new wire tap input stream.
     *
     * @param origStream the orig stream
     * @param os the os
     */
    public WireTapInputStream(InputStream origStream, OutputStream os) {
        this.origStream = origStream;
        this.os = os;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        int c = origStream.read();
        if (c != -1) {
            os.write(c);
        } else {
            os.close();
        }
        return c;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        int count = origStream.read(b);
        os.write(b, 0, count);
        return count;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = origStream.read(b, off, len);
        os.write(b, off, count);
        return count;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException {
        return origStream.skip(n);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        return origStream.available();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        os.close();
        origStream.close();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(int readlimit) {
        throw new RuntimeException("Reset not implemented");
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException {
        throw new RuntimeException("Reset not implemented");
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return false;
    }

}
