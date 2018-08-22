package org.talend.esb.sam.agent.wiretap;

import static org.junit.Assert.assertEquals;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.easymock.EasyMock;
import org.junit.Test;

public class WireTapHelperTest {

    private static final String EXTERNAL_PROPERTY_NAME = "org.talend.esb.sam.agent.log.messageContent";

    @Test
    public void testIfCustomIsNullAndGlobalIsTrue() {

        Message message = EasyMock.createMock(MessageImpl.class);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, true, true);
        assertEquals(true, result);
    }

    @Test
    public void testIfCustomIsNullAndGlobalIsFalse() {

        Message message = EasyMock.createMock(MessageImpl.class);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, false, true);
        assertEquals(false, result);
    }

    @Test
    public void testIfCustomIsNumberAndGlobalIsTrue() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn(123).anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, true, true);
        EasyMock.verify(message);
        assertEquals(true, result);
    }

    @Test
    public void testIfCustomIsFalseAndGlobalIsTrue() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn("false").anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, true, true);
        EasyMock.verify(message);
        assertEquals(false, result);
    }

    @Test
    public void testIfCustomIsTrueAndGlobalIsTrue() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn("true").anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, true, true);
        EasyMock.verify(message);
        assertEquals(true, result);
    }

    @Test
    public void testIfCustomIsTrueAndGlobalIsFalse() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn(true).anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, false, true);
        EasyMock.verify(message);
        assertEquals(true, result);
    }

    @Test
    public void testIfCustomIsFalseAndGlobalIsFalse() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn("FALSE").anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, false, true);
        EasyMock.verify(message);
        assertEquals(false, result);
    }

    @Test
    public void testIfCustomIsBooleanTrueAndGlobalIsFalse() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn(Boolean.TRUE).anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, false, true);
        EasyMock.verify(message);
        assertEquals(true, result);
    }

    @Test
    public void testIfCustomIsAnytStringAndGlobalIsFalse() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn("test").anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, false, true);
        EasyMock.verify(message);
        assertEquals(false, result);
    }

    @Test
    public void testIfCustomIsAnyObjectAndGlobalIsTrue() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn(new Object()).anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, true, true);
        EasyMock.verify(message);
        assertEquals(true, result);
    }

    @Test
    public void testIfExternalOverridingIsFalseAndCustomIsFalseAndGlobalIsFalse() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn(new Object()).anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, false, false);
        EasyMock.verify(message);
        assertEquals(false, result);
    }

    @Test
    public void testIfExternalOverridingIsFalseAndCustomIsFalseAndGlobalIsTrue() {

        Message message = EasyMock.createMock(MessageImpl.class);

        EasyMock.expect(message.getContextualProperty(EXTERNAL_PROPERTY_NAME)).andReturn(new Object()).anyTimes();
        EasyMock.replay(message);

        boolean result = WireTapHelper.isMessageContentToBeLogged(message, true, false);
        EasyMock.verify(message);
        assertEquals(true, result);
    }
}
