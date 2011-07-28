package org.talend.esb.sam.agent.util;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Converter {

	/**
	 * convert Date to XMLGregorianCalendar
	 * @param date
	 * @return
	 */
    public static XMLGregorianCalendar convertDate(Date date) {
    	if (date == null) {
    		return null;
    	}
        XMLGregorianCalendar gCal = null;

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        
        try {
            gCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException ex) {
            ex.printStackTrace();
            return null;
        }
        
        return gCal;
    }	
}
