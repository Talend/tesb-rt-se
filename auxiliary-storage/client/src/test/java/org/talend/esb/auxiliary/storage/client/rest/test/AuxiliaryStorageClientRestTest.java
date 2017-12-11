package org.talend.esb.auxiliary.storage.client.rest.test;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.auxiliary.storage.client.rest.AuxiliaryStorageClientRest;
import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;
import org.talend.esb.auxiliary.storage.common.exception.IllegalParameterException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })
public class AuxiliaryStorageClientRestTest {

    private final static String TMP_PATH =  "./target/esbrepo/auxiliarystorage/fileStore";
    private final static String SERVER_URL = "local://aux-rest-service";

    private static AuxiliaryStorageClientRest<Pojo> client;

    private Pojo pojo;

    @BeforeClass
    public static void init() {
        client = new AuxiliaryStorageClientRest<Pojo>();
        client.setServerURL(SERVER_URL);
        client.setAuxiliaryStorageAuthentication("NO");
        client.setAuthenticationUser("tadmin");
        client.setAuthenticationPassword("tadmin");
    }

    @Before
    public void before() {
        cleanupRepo();
        client.setAuxiliaryObjectFactory(new PojoAuxiliaryObjectFactory());
    }


    @Test
    public void  testSave() {
        pojo = new Pojo(5, "Pojo");
        client.saveObject(pojo);

        AuxiliaryObjectFactory<Pojo> factory = client.getAuxiliaryObjectFactory();
        Assert.assertNotNull(factory);

        Pojo retrievedPojo = client.getStoredObject(factory.createObjectKey(pojo));
        Assert.assertNotNull(retrievedPojo);
        Assert.assertEquals(5, retrievedPojo.getId());
        Assert.assertEquals("Pojo", retrievedPojo.getName());

        client.saveObject(pojo);
    }

    @Test
    public void  testRemove() {
        pojo = new Pojo(5, "Pojo");
        client.saveObject(pojo);

        AuxiliaryObjectFactory<Pojo> factory = client.getAuxiliaryObjectFactory();
        Assert.assertNotNull(factory);

        client.removeStoredObject(factory.createObjectKey(pojo));
        Pojo retrieved = client.getStoredObject(factory.createObjectKey(pojo));
        Assert.assertNull(retrieved);

        client.removeStoredObject(factory.createObjectKey(pojo));
    }


    @Test(expected = IllegalParameterException.class)
    public void testNoObjectFactory() {
        client.setAuxiliaryObjectFactory(null);
        Pojo pojo = new Pojo(1, "one");
        client.saveObject(pojo);
    }

    @Test(expected = IllegalParameterException.class)
    public void testSaveNullObject() {
        client.saveObject(null);
    }

    @Test(expected = IllegalParameterException.class)
    public void testLookupNullKey() {
        client.getStoredObject(null);
    }

    @Test(expected = IllegalParameterException.class)
    public void testLookupEmptyKey() {
        client.getStoredObject("");
    }

    @Test(expected = IllegalParameterException.class)
    public void testRemoveEmptyKey() {
        client.removeStoredObject("");
    }

    @Test(expected = IllegalParameterException.class)
    public void testRemoveNullKey() {
        client.removeStoredObject(null);
    }

    @Test
    public void testPropsConstructor() {
        Properties p = new Properties();
        p.setProperty("auxiliary.storage.service.url", SERVER_URL);
        p.setProperty("auxiliary.storage.service.authentication", "NO");

        AuxiliaryStorageClientRest<Pojo> client2 = new AuxiliaryStorageClientRest<Pojo>(p);
        client2.setAuxiliaryObjectFactory(new PojoAuxiliaryObjectFactory());

        Pojo pojo = new Pojo(123, "One hundred and twenty three");
        client2.saveObject(pojo);
        Pojo retrievedPojo = client2.getStoredObject(client2.getAuxiliaryObjectFactory().createObjectKey(pojo));
        Assert.assertNotNull(retrievedPojo);
        Assert.assertEquals(123, retrievedPojo.getId());
        Assert.assertEquals("One hundred and twenty three", retrievedPojo.getName());
    }

    @Test
    public void testUrlSwitching() {
        Properties p = new Properties();
        p.setProperty("auxiliary.storage.service.url", "local://not-existing-endpoint," + SERVER_URL);
        p.setProperty("auxiliary.storage.service.authentication", "NO");

        AuxiliaryStorageClientRest<Pojo> client2 = new AuxiliaryStorageClientRest<Pojo>(p);
        client2.setAuxiliaryObjectFactory(new PojoAuxiliaryObjectFactory());

        Pojo pojo = new Pojo(123, "One hundred and twenty three");
        client2.saveObject(pojo);
        Pojo retrievedPojo = client2.getStoredObject(client2.getAuxiliaryObjectFactory().createObjectKey(pojo));
        Assert.assertNotNull(retrievedPojo);
        Assert.assertEquals(123, retrievedPojo.getId());
        Assert.assertEquals("One hundred and twenty three", retrievedPojo.getName());
    }

    @Test(expected = RuntimeException.class)
    public void testUrlSwitchingFailure() {
        Properties p = new Properties();
        p.setProperty("auxiliary.storage.service.url", "local://not-existing-endpoint,local://another-fictional-endpoint");
        p.setProperty("auxiliary.storage.service.authentication", "NO");

        AuxiliaryStorageClientRest<Pojo> client2 = new AuxiliaryStorageClientRest<Pojo>(p);
        client2.setAuxiliaryObjectFactory(new PojoAuxiliaryObjectFactory());

        Pojo pojo = new Pojo(123, "One hundred and twenty three");
        client2.saveObject(pojo);
    }





    private static void cleanupRepo() {
        File tempFolder = new File(TMP_PATH);
        if (tempFolder.exists()) {
            try {
                FileUtils.cleanDirectory(tempFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PojoAuxiliaryObjectFactory implements AuxiliaryObjectFactory<Pojo> {

        @Override
        public String marshalObject(Pojo ctx) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "";
            try {
                json = objectMapper.writer().writeValueAsString(ctx);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        public Pojo unmarshallObject(String marshalledData) {
            if (marshalledData == null) {
                return  null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Pojo ret = null;
            try {
                ret = objectMapper.readValue(marshalledData, Pojo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ret;
        }

        @Override
        public String createObjectKey(Pojo ctx) {
            if (ctx == null) {
                return null;
            }
            return Integer.toString(ctx.getId());
        }

        @Override
        public String contentType() {
            return "application/json";
        }
    }
}
