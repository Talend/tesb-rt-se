package org.talend.esb.sam.agent.serviceclient;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.cxf.attachment.ByteDataSource;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

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
        eventType.setCustomInfo(convertCustomInfo(event.getCustomInfoList()));
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

    private static CustomInfoType convertCustomInfo(List<CustomInfo> ciList) {
        if (ciList == null || ciList.size() < 1 ) {
            return null;
        }

        CustomInfoType ciType = new CustomInfoType();

        for (int i=0; i<ciList.size();i++){
        	CustomInfo cInfo = ciList.get(i);
        	
            CustomInfoType.Item cItem = new CustomInfoType.Item();
            cItem.setKey(cInfo.getCustKey());
            cItem.setValue(cInfo.getCustValue());
            ciType.getItem().add(cItem);
        }
        
        return ciType;
    }
    
	private static XMLGregorianCalendar convertDate(Date date) {
		XMLGregorianCalendar gCal = null;

		try {
			gCal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		} catch (DatatypeConfigurationException ex) {
			ex.printStackTrace();
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		gCal.setYear(cal.get(Calendar.YEAR));
		gCal.setMonth(cal.get(Calendar.MONTH) + 1);
		gCal.setDay(cal.get(Calendar.DAY_OF_MONTH));
		gCal.setHour(cal.get(Calendar.HOUR_OF_DAY));
		gCal.setMinute(cal.get(Calendar.MINUTE));
		gCal.setSecond(cal.get(Calendar.SECOND));
		gCal.setMillisecond(cal.get(Calendar.MILLISECOND));
		gCal.setTimezone(cal.get(Calendar.ZONE_OFFSET) / 60000);
		return gCal;

	}

    private static EventEnumType convertEventType(org.talend.esb.sam.common.event.EventTypeEnum eventType) {
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
