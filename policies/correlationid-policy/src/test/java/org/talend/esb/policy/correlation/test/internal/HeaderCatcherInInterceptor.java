package org.talend.esb.policy.correlation.test.internal;



import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.List;


public class HeaderCatcherInInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final QName CORRELATION_HEADER_NAME = new QName("http://www.talend.com/esb/sam/correlationId/v1", "correlationId");

    private Object latestCorrelationHeader;

    public HeaderCatcherInInterceptor() {
        super(Phase.POST_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        List<Header>headers = (List<Header>)message.get(Header.HEADER_LIST);
        for (Header header : headers) {
            if (!CORRELATION_HEADER_NAME.equals(header.getName())
                    || !(header.getObject() instanceof Element)) {
                continue;
            }

            Element obj = (Element)header.getObject();
            if (obj.getFirstChild() == null) {
                continue;
            }
            System.out.println("HEADER RECEIVED: " + header.getName() + " : " +  obj.getFirstChild().getTextContent());
            latestCorrelationHeader = obj.getFirstChild().getTextContent();
            return;
        }
        latestCorrelationHeader = null;
    }

    public Object getLatestCorrelationHeader() {
        return latestCorrelationHeader;
    }
}
