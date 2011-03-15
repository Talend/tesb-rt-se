package org.talend.esb.sam.agent.interceptor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * In interceptor for receiving a message and creating an event.
 */
public class WireTapIn extends AbstractPhaseInterceptor<Message> {
    private boolean logMessage;

    public WireTapIn(boolean logMessage) {
        super(Phase.RECEIVE);
        this.logMessage = logMessage;
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        InputStream is = message.getContent(InputStream.class);
        if (is != null || !logMessage) {
            try {
                CachedOutputStream cos = new CachedOutputStream();
                // TODO: We should try to make this streaming
                //WireTapInputStream wtis = new WireTapInputStream(is, cos);
                //message.setContent(InputStream.class, wtis);
                IOUtils.copy(is, cos);
                message.setContent(InputStream.class, cos.getInputStream());
                message.setContent(CachedOutputStream.class, cos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
