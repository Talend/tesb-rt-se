package org.talend.esb.sam.agent.eventadmin.translator.subject;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractSubjectExtractorHandler extends DefaultHandler {
    
    public abstract String getSubject();
    
    public static class SubjectFoundException extends SAXException {
        private static final long serialVersionUID = 1L;
    }
}
