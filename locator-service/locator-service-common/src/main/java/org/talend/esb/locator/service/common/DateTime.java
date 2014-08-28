package org.talend.esb.locator.service.common;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

public class DateTime extends Date {
    
    private static final long serialVersionUID = 4084394298665419478L;

    public DateTime(String dateTime) {
        super(DatatypeConverter.parseDateTime(dateTime).getTime().getTime());
    }
}
