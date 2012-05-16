package org.talend.esb.cxf.continuation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.continuations.ContinuationProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.ContinuationProviderFactory;


public class DisableContinuationProvider implements ContinuationProviderFactory {

    public ContinuationProvider createContinuationProvider(Message inMessage, HttpServletRequest req,
                                                           HttpServletResponse resp) {
        return null;
    }

    public Message retrieveFromContinuation(HttpServletRequest req) {
        // TODO Auto-generated method stub
        return null;
    }
}
