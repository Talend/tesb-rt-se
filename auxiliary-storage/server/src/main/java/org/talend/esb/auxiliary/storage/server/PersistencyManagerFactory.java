package org.talend.esb.auxiliary.storage.server;

import org.talend.esb.auxiliary.storage.persistence.PersistencyManager;
import org.talend.esb.auxiliary.storage.persistence.file.PersistencyFileManager;
import org.talend.esb.auxiliary.storage.persistence.jcr.PersistencyJCRManager;

public class PersistencyManagerFactory {

    public static String FILE_STORE = "FILEStore";
    public static String JCR_STORE  = "JCRStore";



    private String storageDirPath = null;


    public PersistencyManager createPersistencyManager(String managerType) {


        if (FILE_STORE.equals(managerType)) {
            return createFileStore();
        } else if (JCR_STORE.equals(managerType)) {
            return createJCRStore();
        } else {
            return createFileStore();
        }

    }


    private PersistencyManager createFileStore() {
        PersistencyFileManager manager = new PersistencyFileManager();
        manager.setStorageDirPath(getStorageDirPath());
        return manager;
    }


    private PersistencyManager createJCRStore() {
        PersistencyJCRManager manager = new PersistencyJCRManager();
        manager.setStorageDirPath(getStorageDirPath());
        return manager;
    }





    public String getStorageDirPath() {
        return storageDirPath;
    }


    public void setStorageDirPath(String storageDirPath) {
        this.storageDirPath = storageDirPath;
    }
}
