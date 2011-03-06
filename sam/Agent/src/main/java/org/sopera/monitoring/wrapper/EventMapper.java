package org.sopera.monitoring.wrapper;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.cxf.attachment.ByteDataSource;
import org.sopera.monitoring._2010._09.common.CustomInfoType;
import org.sopera.monitoring._2010._09.common.EventEnumType;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring._2010._09.common.MessageInfoType;
import org.sopera.monitoring._2010._09.common.OriginatorType;
import org.sopera.monitoring.event.CustomInfo;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;

public class EventMapper {
    /**
     * convert Event bean to EventType manually
     * 
     * @param event
     * @return
     */
    public static EventType map(Event event) {
        EventType eventType = new EventType();
        eventType.setTimestamp(convertDate(event.getTimestamp()));
        eventType.setEventType(convertEventType(event.getEventType()));
        OriginatorType origType = mapOriginator(event.getOriginator());
        eventType.setOriginator(origType);
        MessageInfoType miType = mapMessageInfo(event.getMessageInfo());
        eventType.setMessageInfo(miType);
        eventType.setCustomInfo(convertCustomInfo(event.getCustomInfo()));
        DataHandler datHandler = getDataHandlerForString(event);
        eventType.setContent(datHandler);
        return eventType;
    }

    private static DataHandler getDataHandlerForString(Event event) {
        DataSource ds;
        try {
            ds = new ByteDataSource(event.getContent().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        DataHandler datHandler = new DataHandler(ds);
        return datHandler;
    }

    private static MessageInfoType mapMessageInfo(MessageInfo messageInfo) {
        MessageInfoType miType = new MessageInfoType();
        miType.setMessageId(messageInfo.getMessageId());
        miType.setFlowId(messageInfo.getFlowId());
        miType.setPorttype(convertString(messageInfo.getPortType()));
        miType.setOperationName(messageInfo.getOperationName());
        miType.setTransport(messageInfo.getTransportType());
        return miType;
    }

    private static OriginatorType mapOriginator(Originator originator) {
        OriginatorType origType = new OriginatorType();
        origType.setProcessId(originator.getProcessId());
        origType.setIp(originator.getIp());
        origType.setHostname(originator.getHostname());
        origType.setCustomId(originator.getCustomId());
        return origType;
    }

    private static CustomInfoType convertCustomInfo(CustomInfo cInfo) {
        if (cInfo == null) {
            return null;
        }

        CustomInfoType ciType = new CustomInfoType();

        Map<String, Object> prop = cInfo.getProperties();

        for (Map.Entry<String, Object> entry : prop.entrySet()) {
            CustomInfoType.Item cItem = new CustomInfoType.Item();
            cItem.setKey(entry.getKey());
            cItem.setValue(entry.getValue());
            ciType.getItem().add(cItem);
        }

        return ciType;
    }
    
    private static XMLGregorianCalendar convertToXmlGregorianCalendar(Calendar cal) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar();
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static XMLGregorianCalendar convertDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return convertToXmlGregorianCalendar(cal);

    }

    private static EventEnumType convertEventType(org.sopera.monitoring.event.EventTypeEnum eventType) {
        return EventEnumType.valueOf(eventType.name());
    }

    private static QName convertString(String str) {
        if (str != null) {
            return QName.valueOf(str);
        } else {
            return null;
        }
    }
}
