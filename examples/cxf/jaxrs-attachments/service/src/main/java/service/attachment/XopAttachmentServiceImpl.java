/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service.attachment;

import common.attachment.XopAttachmentService;
import common.attachment.XopBean;

/**
 * JAX-RS XopAttachmentService root resource
 */
public class XopAttachmentServiceImpl implements XopAttachmentService {

    /**
     * {@inheritDoc}
     */
    public XopBean echoXopAttachment(XopBean xop) {
        return xop;
    }

}
