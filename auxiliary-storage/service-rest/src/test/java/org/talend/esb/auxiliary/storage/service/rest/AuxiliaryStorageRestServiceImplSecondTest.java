package org.talend.esb.auxiliary.storage.service.rest;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.common.AuxiliaryStorageServer;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.persistence.PersistencyManager;
import org.talend.esb.auxiliary.storage.persistence.file.PersistencyFileManager;
import org.talend.esb.auxiliary.storage.server.AuxiliaryStorageServerImpl;
import org.talend.esb.auxiliary.storage.server.PersistencyManagerFactory;

import java.io.File;
import java.io.IOException;

public class AuxiliaryStorageRestServiceImplSecondTest {

    private static final String STORE_PATH_FILE  =  "./target/esbrepo/auxiliarystorage/fileStore";
    private static final String OBJECT_VALUE = "some test value";
    private static final String OBJECT_KEY = "some_test_key";

    private static AuxiliaryStorageRestServiceImpl service;
    private static AuxiliaryStorageServerImpl server;


    @BeforeClass
    public static void init() {
        cleanupRepo();
        PersistencyManagerFactory factory = new PersistencyManagerFactory();
        factory.setStorageDirPath(STORE_PATH_FILE);
        PersistencyManager manager =  factory.createPersistencyManager(PersistencyManagerFactory.FILE_STORE);
        ((PersistencyFileManager)manager).init();

        server = new AuxiliaryStorageServerImpl();
        server.setPersistencyManager(manager);

        service = new AuxiliaryStorageRestServiceImpl();
        service.setAuxiliaryStorageServer(server);
    }

    @After
    public void after() {
        service.setAuxiliaryStorageServer(server);
    }

    @Test
    public void testGetServer() {
        AuxiliaryStorageServer server = service.getAuxiliaryStorageServer();
        Assert.assertNotNull(server);
    }

    @Test(expected = AuxiliaryStorageException.class)
    public void testNoServerPut() {
        service.setAuxiliaryStorageServer(null);
        service.put(OBJECT_VALUE, OBJECT_KEY);
    }

    @Test(expected = AuxiliaryStorageException.class)
    public void testNoServerLookup() {
        service.setAuxiliaryStorageServer(null);
        service.lookup(OBJECT_KEY);
    }

    @Test
    public void testSave() {
        service.put(OBJECT_VALUE, OBJECT_KEY);
        String retrieved = service.lookup(OBJECT_KEY);
        Assert.assertNotNull(retrieved);
        Assert.assertEquals(OBJECT_VALUE, retrieved);
        service.remove(OBJECT_KEY);
    }



    private static void cleanupRepo() {
        File tempFolder = new File(STORE_PATH_FILE);
        if (tempFolder.exists()) {
            try {
                FileUtils.cleanDirectory(tempFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
