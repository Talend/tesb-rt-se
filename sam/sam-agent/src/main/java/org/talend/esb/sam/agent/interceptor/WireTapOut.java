package org.talend.esb.sam.agent.interceptor;

import java.io.OutputStream;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Create a wiretap InputStream in the message with key ContentKey.CONTENT_KEY
 */
public class WireTapOut extends AbstractPhaseInterceptor<Message> {
    private Interceptor<Message> wireTap;
    private boolean logMessage;

    public WireTapOut(Interceptor<Message> wireTap, boolean logMessage) {
        super(Phase.PRE_STREAM);
        this.wireTap = wireTap;
        this.logMessage = logMessage;
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        if (os != null && logMessage) {
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            message.setContent(OutputStream.class, newOut);
            message.setContent(CachedOutputStream.class, newOut);
            newOut.registerCallback(new CachedOutputStreamCallback() {
                
                @Override
                public void onFlush(CachedOutputStream os) {
                }
                
                @Override
                public void onClose(CachedOutputStream os) {
                    wireTap.handleMessage(message);
                }
            });
        }
    }

}
