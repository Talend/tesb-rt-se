package org.talend.esb.auxiliary.storage.server;


import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.persistence.PersistencyManager;
import org.talend.esb.auxiliary.storage.persistence.file.PersistencyFileManager;
import org.talend.esb.auxiliary.storage.persistence.jcr.PersistencyJCRManager;

import java.io.File;
import java.io.IOException;

public class PersistencyManagerFactoryTest {


    private final static String STORE_PATH_FILE  =  "./target/esbrepo/auxiliarystorage/fileStore";
    private final static String STORE_PATH_JCR   =  "./target/esbrepo/auxiliarystorage/jcrStore";


    private static PersistencyManagerFactory factory;


    @BeforeClass
    public static void init() {
        cleanupRepo();
        factory = new PersistencyManagerFactory();
    }

    @Test
    public void createManagerFile() {
        factory.setStorageDirPath(STORE_PATH_FILE);
        PersistencyManager manager = factory.createPersistencyManager(PersistencyManagerFactory.FILE_STORE);
        ((PersistencyFileManager)manager).init();
        Assert.assertNotNull(manager);
        manager.storeObject("Object for file store", "file_store");
        String retrieved = manager.restoreObject("file_store");
        Assert.assertNotNull(retrieved);
        Assert.assertEquals("Object for file store", retrieved);
        manager.removeObject("file_store");
    }

    @Test
    public void createManagerJcr() {
        factory.setStorageDirPath(STORE_PATH_JCR);
        PersistencyManager manager = factory.createPersistencyManager(PersistencyManagerFactory.JCR_STORE);
        Assert.assertNotNull(manager);
        ((PersistencyJCRManager)manager).init();
        manager.storeObject("Object for JCR store", "jcr_store");
        String retrieved = manager.restoreObject("jcr_store");
        Assert.assertNotNull(retrieved);
        Assert.assertEquals("Object for JCR store", retrieved);
        manager.removeObject("jcr_store");
    }


    private static void cleanupRepo() {
        cleanDirectory(STORE_PATH_FILE);
        cleanDirectory(STORE_PATH_JCR);
    }

    private static void cleanDirectory(String path) {
        File tempFolder = new File(path);
        if (tempFolder.exists()) {
            try {
                FileUtils.cleanDirectory(tempFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
