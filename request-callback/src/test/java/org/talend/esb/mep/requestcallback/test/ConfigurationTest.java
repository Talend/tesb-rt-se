package org.talend.esb.mep.requestcallback.test;

import org.junit.*;
import org.talend.esb.mep.requestcallback.feature.Configuration;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;
import org.talend.esb.mep.requestcallback.impl.ConfigurationImpl;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class ConfigurationTest {

    private static final QName qname = new QName("http://test.talend.org", "requestcallback");

    private ConfigurationImpl conf = null;
    private Map<String, Object> m;

    private final String INT_KEY = "INT_KEY";
    private final String LONG_KEY = "LONG_KEY";
    private final String BOOL_KEY = "BOOL_KEY";
    private final String STR_KEY = "STR_KEY";

    @Before
    public void before() {
        conf = new ConfigurationImpl(qname);
        Assert.assertNotNull(conf);
    }

    @AfterClass
    public static void cleanup() {
        System.clearProperty(RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_SYSTEM_PROPERTY);
    }

    @Test
    public void testConstructor() {
        conf = new ConfigurationImpl(null);
        Assert.assertNotNull(conf);
        Assert.assertNull(conf.getAlternateConfigurationIdentifier());
        Assert.assertEquals(0, conf.size());
        Assert.assertTrue(conf.isEmpty());
        Assert.assertFalse(conf.containsKey("some_key"));
        Assert.assertFalse(conf.containsValue("some_value"));
    }


    @Test
    public void testSimple() {
        m = new HashMap<String, Object>();
        m.put(INT_KEY, 1);
        m.put(LONG_KEY, 1099l);
        m.put(STR_KEY, "What's in a string?");
        m.put(BOOL_KEY, true);

        Assert.assertTrue(conf.isEmpty());

        conf.putAll(m);

        Set<String> keys = conf.keySet();
        Assert.assertTrue(keys.contains(INT_KEY));
        Assert.assertTrue(keys.contains(LONG_KEY));
        Assert.assertTrue(keys.contains(STR_KEY));
        Assert.assertTrue(keys.contains(BOOL_KEY));


        Assert.assertNotNull(conf.getProperty(STR_KEY));
        Assert.assertEquals("What's in a string?", conf.getProperty(STR_KEY));

        Assert.assertNotNull(conf.getIntegerProperty(INT_KEY));
        Assert.assertEquals(new Integer(1), conf.getIntegerProperty(INT_KEY));

        Assert.assertNotNull(conf.getLongProperty(LONG_KEY));
        Assert.assertEquals(new Long(1099), conf.getLongProperty(LONG_KEY));

        Assert.assertNotNull(conf.getBooleanProperty(BOOL_KEY));
        Assert.assertEquals(Boolean.TRUE, conf.getBooleanProperty(BOOL_KEY));

        conf.put("some_key", "Nothing will come of nothing");
        Assert.assertNotNull(conf.get("some_key"));
        Assert.assertEquals("Nothing will come of nothing",  conf.get("some_key"));
        conf.remove("some_key");
        Assert.assertNull(conf.get("some_key"));

        conf.clear();
        Assert.assertEquals(0, conf.size());
        Assert.assertTrue(conf.values().isEmpty());
        Assert.assertTrue(conf.entrySet().isEmpty());

        Assert.assertEquals(qname, conf.getConfigurationName());
        Assert.assertEquals("org.talend.esb.mep.requestcallback.http-test.talend.org.requestcallback", conf.getConfigurationIdentifier());
        Assert.assertEquals("org.talend.esb.mep.requestcallback.requestcallback", conf.getAlternateConfigurationIdentifier());

        Configuration.ChangeListener listener = setChangeListener();
        Assert.assertTrue(listener.equals(conf.getChangeListener()));
    }

    @Test
    public void testStaticConfiguration() {
        System.setProperty(RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_SYSTEM_PROPERTY,
                                "file://resources/");
        setChangeListener();
        conf.refreshStaticConfiguration();

        Assert.assertFalse(conf.isEmpty());
        Assert.assertTrue(conf.containsKey("jndiInitialContextFactory"));
        Assert.assertFalse(conf.containsKey("default"));
        Assert.assertTrue(conf.containsValue("tcp://localhost:61616"));
        Assert.assertEquals("dynamicQueues/libraryprovider.queue", conf.get("destinationName"));

        m = new HashMap<String, Object>();
        conf.fillProperties(null, m);

        Assert.assertNotNull(conf.get("jndiInitialContextFactory"));
        Assert.assertEquals("org.apache.activemq.jndi.ActiveMQInitialContextFactory", conf.get("jndiInitialContextFactory"));
    }

    @Test
    public void testDynamicConfigurationMap() {
        setChangeListener();

        m = new HashMap<String, Object>();
        m.put(STR_KEY, "string_value");

        conf.updateDynamicConfiguration(m, false);
        Assert.assertEquals("string_value", conf.get(STR_KEY));

        m.clear();
        conf.updateDynamicConfiguration(m, true);
        Assert.assertTrue(conf.isEmpty());

        m.put(STR_KEY, "string_value");
        conf.updateDynamicConfiguration(m, true);

        Assert.assertTrue(conf.containsKey(STR_KEY));
        Assert.assertEquals(1, conf.size());

        conf.put(INT_KEY, 1);
        Assert.assertEquals(new Integer(1), conf.getIntegerProperty(INT_KEY));
        Assert.assertFalse(conf.isEmpty());
        Assert.assertTrue(conf.containsKey(INT_KEY));

        m.clear();
        conf.fillProperties("prfx", m);
        Assert.assertTrue(m.isEmpty());
        conf.fillProperties(null, m);
        Assert.assertFalse(m.isEmpty());
    }

    @Test
    public void testDynamicConfigurationDict() {
        setChangeListener();

        Dictionary<String, Object> dict = new Hashtable<String, Object>();


        dict.put(STR_KEY, "string_value");

        conf.updateDynamicConfiguration(dict, false);
        Assert.assertEquals("string_value", conf.get(STR_KEY));

        dict.remove(STR_KEY);
        conf.updateDynamicConfiguration(dict, true);
        Assert.assertTrue(conf.isEmpty());
    }

    @Test
    public void testUserMapCases() {
        Assert.assertNull(conf.remove(STR_KEY));
        conf.put(STR_KEY, "strval");
        Assert.assertEquals("strval", conf.remove(STR_KEY));
        m = new HashMap<String, Object>();
        conf.putAll(m);
        Assert.assertTrue(conf.isEmpty());
    }

    @Test
    public void testExpandedProps() {
        conf.put(STR_KEY, "str_val");
        m = new HashMap<String, Object>();
        conf.fillExpandedProperties(null, m);
        Assert.assertTrue(conf.containsKey(STR_KEY));
        Assert.assertTrue(conf.containsValue("str_val"));
    }

    @Test
    public void staticConfigurationNegative() {
        conf = new ConfigurationImpl(new QName("http://example.com/", "example"));
        conf.refreshStaticConfiguration();
        Assert.assertTrue(conf.isEmpty());
    }

    @Test
    public void staticConfiguration2() {
        System.setProperty(RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_SYSTEM_PROPERTY,
                "src/test/resources/");
        conf.refreshStaticConfiguration();
        Assert.assertFalse(conf.isEmpty());
    }

    @Test
    public void testIntProp() {
        Assert.assertNull(conf.getIntegerProperty(INT_KEY));
        conf.put(INT_KEY, new AtomicInteger(1));
        Assert.assertEquals(new Integer(1), conf.getIntegerProperty(INT_KEY));
        conf.put(INT_KEY, "1");
        Assert.assertEquals(new Integer(1), conf.getIntegerProperty(INT_KEY));
        conf.put(INT_KEY, "abracadabra");
        Assert.assertNull(conf.getIntegerProperty(INT_KEY));
    }

    @Test
    public void testLongProp() {
        Assert.assertNull(conf.getLongProperty(LONG_KEY));
        conf.put(LONG_KEY, new AtomicLong(1));
        Assert.assertEquals(new Long(1), conf.getLongProperty(LONG_KEY));
        conf.put(LONG_KEY, "1");
        Assert.assertEquals(new Long(1), conf.getLongProperty(LONG_KEY));
        conf.put(LONG_KEY, "abracadabra");
        Assert.assertNull(conf.getLongProperty(LONG_KEY));
    }

    @Test
    public void testBooleanProp() {
        Assert.assertNull(conf.getBooleanProperty(BOOL_KEY));
        conf.put(BOOL_KEY, "true");
        Assert.assertTrue(conf.getBooleanProperty(BOOL_KEY));

    }

    @Test
    public void testPidModeProperty() {
        final String PID_KEY = "pid_mode_key";
        Assert.assertNull(conf.getPidModeProperty(PID_KEY));
        conf.put(PID_KEY, Configuration.PidMode.FULL_NAME);
        Assert.assertEquals(Configuration.PidMode.FULL_NAME, conf.getPidModeProperty(PID_KEY));
        conf.put(PID_KEY, "fullName");
        Assert.assertEquals(Configuration.PidMode.FULL_NAME, conf.getPidModeProperty(PID_KEY));
        conf.put(PID_KEY, "localName");
        Assert.assertEquals(Configuration.PidMode.LOCAL_NAME, conf.getPidModeProperty(PID_KEY));
        conf.put(PID_KEY, "superduper");
        Assert.assertNull(conf.getPidModeProperty(PID_KEY));

    }

    @Test
    public void  testExpandedProperty() {
        final String SOME_KEY = "some.property";
        conf.put(SOME_KEY, "some_value");
        conf.put(STR_KEY, "${some.property}");
        Assert.assertEquals("some_value", conf.getExpandedProperty(STR_KEY));
    }


    private Configuration.ChangeListener setChangeListener() {
        Configuration.ChangeListener listener = new Configuration.ChangeListener() {


            @Override
            public void changed(Configuration configuration) {

            }

            public boolean equals(Object o ) {
                if (o == null) {
                    return false;
                }
                if (! (o instanceof Configuration.ChangeListener)) {
                    return  false;
                }

                Configuration.ChangeListener l = (Configuration.ChangeListener)o;
                return l.toString().equals("100");
            }

            public  String toString() {
                return "100";
            }
        };

        conf.setChangeListener(listener);
        return listener;
    }
}
