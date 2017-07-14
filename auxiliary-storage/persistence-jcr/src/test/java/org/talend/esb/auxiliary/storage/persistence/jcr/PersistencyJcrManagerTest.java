package org.talend.esb.auxiliary.storage.persistence.jcr;


import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;

public class PersistencyJcrManagerTest {

    private final static String TMP_PATH =  "./target/esbrepo/auxiliarystorage/jcrStore";

    private static final PersistencyJCRManager persistencyManager = new PersistencyJCRManager();


    @BeforeClass
    public static void init() {
        cleanupRepo();
        persistencyManager.setStorageDirPath(TMP_PATH);
        persistencyManager.init();
    }


    @Test
    public void testSave() {
        persistencyManager.storeObject("testSave", "1");
        String ctx = persistencyManager.restoreObject("1");
        Assert.assertNotNull(ctx);
        Assert.assertEquals("testSave", ctx);
    }


    @Test
    public void testRemove() {
        persistencyManager.storeObject("testRemove", "2");
        persistencyManager.removeObject("2");
        String ctx = persistencyManager.restoreObject("2");
        Assert.assertNull(ctx);
    }

    @Test(expected = ObjectAlreadyExistsException.class)
    public void testStoreTwice() {
        persistencyManager.storeObject("testStoreTwice", "3");
        persistencyManager.storeObject("testStoreTwice", "3");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testRemoveNonExisting() {
        persistencyManager.removeObject("100500");
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
}
