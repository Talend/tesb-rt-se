package org.talend.esb.auxiliary.storage.persistence.file.test;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.PersistencyException;
import org.talend.esb.auxiliary.storage.persistence.file.PersistencyFileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PersistencyFileManagerTest {

    private final static String TMP_PATH =  "./target/esbrepo/auxiliarystorage/fileStore";

    private static final PersistencyFileManager persistencyManager = new PersistencyFileManager();


    @BeforeClass
    public static void init() {
        persistencyManager.setStorageDirPath(TMP_PATH);
        persistencyManager.init();
    }

    @After
    public void cleanup() {
        cleanupRepo();
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

    @Test(expected = PersistencyException.class)
    public void testSaveOutsideOfStorageDir() {
        persistencyManager.storeObject("testSave", "../newfile");
    }

    @Test(expected = PersistencyException.class)
    public void testDeserializeDate() throws IOException {
        File newFile = new File(TMP_PATH + File.separator + "date.ctx");
        Files.copy(Paths.get("src/test/resources/date.ctx"), newFile.toPath());
        persistencyManager.restoreObject("date");
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
