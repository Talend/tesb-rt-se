package org.talend.esb.sam.agent.wiretap;


import org.apache.cxf.io.CacheAndWriteOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class CacheAndWriteTapOutputStream extends CacheAndWriteOutputStream {


    public CacheAndWriteTapOutputStream(OutputStream stream) {
        super(stream);
    }

    @Override
    protected void doClose() throws IOException {
        // Fixing TESB-14583
        resetOut(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        }, false);
    }
}
