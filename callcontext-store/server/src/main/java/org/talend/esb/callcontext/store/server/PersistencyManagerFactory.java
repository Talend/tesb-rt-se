package org.talend.esb.callcontext.store.server;

import org.talend.esb.callcontext.store.persistence.PersistencyManager;
import org.talend.esb.callcontext.store.persistence.file.PersistencyFileManager;
import org.talend.esb.callcontext.store.persistence.jcr.PersistencyJCRManager;

public class PersistencyManagerFactory {

    public static String FILE_STORE = "FILEStore";
    public static String JCR_STORE  = "JCRStore";



    private String storageDirPath = null;
    private String schemaResourceName = null;


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
        manager.setSchemaResourceName(getSchemaResourceName());
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


    public String getSchemaResourceName() {
        return schemaResourceName;
    }


    public void setSchemaResourceName(String schemaResourceName) {
        this.schemaResourceName = schemaResourceName;
    }

}
