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
package org.talend.esb.sam.agent.util;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * The Class Converter.
 */
public final class Converter {

	private static DatatypeFactory factory = null;

    /**
     * Instantiates a new converter.
     */
    private Converter() {
    }

    private static DatatypeFactory getDatatypeFactory() throws DatatypeConfigurationException{
    	if(factory == null) {
            factory = DatatypeFactory.newInstance();
    	}
    	return  factory;
    }

    /**
     * convert Date to XMLGregorianCalendar.
     *
     * @param date the date
     * @return the xML gregorian calendar
     */
    public static XMLGregorianCalendar convertDate(Date date) {
        if (date == null) {
            return null;
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());

        try {
            return getDatatypeFactory().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException ex) {
            return null;
        }
    }

    /**
     * Gets the pID.
     *
     * @return the pID
     */
    public static String getPID() {
        final String mxName = ManagementFactory.getRuntimeMXBean().getName();
        return mxName.split("@")[0];
    }

}
