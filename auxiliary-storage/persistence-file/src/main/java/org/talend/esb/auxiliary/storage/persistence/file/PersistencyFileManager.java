/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.auxiliary.storage.persistence.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.InitializationException;
import org.talend.esb.auxiliary.storage.common.exception.PersistencyException;
import org.talend.esb.auxiliary.storage.persistence.AbstractPersistencyManager;

public class PersistencyFileManager extends AbstractPersistencyManager {


    private String storageDirPath = null;

    public PersistencyFileManager() {

    }

    @Override
    public String restoreObject(String key) throws PersistencyException {

        synchronized (this) {

            String filePath = createFilePath(key);
            File file = new File(filePath);
            checkFileIsJailedToStorageDir(file);
            if (!file.exists()) {
                return null;
            }


            ObjectInputStream ois = null;

            String restoredContext = null;

            try {
                ois = new PersistencyFileManagerObjectInputStream(new FileInputStream(file));
                restoredContext = (String)ois.readObject();

            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed to restore context. IOException. Error message: " + e.getMessage());
                throw new PersistencyException("Error reading context store file "
                        + filePath + "  Underlying error message is:" + e.getMessage());
            } catch (ClassNotFoundException e) {
                 LOG.log(Level.SEVERE, "Failed to restore context. ClassNotFoundException. Error message: " + e.getMessage());
                 throw new PersistencyException("Error reading context store file "
                         + filePath + "  Underlying error message is:" + e.getMessage());
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                       LOG.log(Level.WARNING, "Failed to close DataFileReader after restoring context. The message is: " + e.getMessage());
                    }
                }
            }

            return restoredContext;
        }
    }


    @Override
    public void storeObject(String context, String key) throws PersistencyException {

        synchronized (this) {

            String filePath = createFilePath(key);
            File file = new File(filePath);
            checkFileIsJailedToStorageDir(file);

            if (file.exists()) {
                throw new ObjectAlreadyExistsException("Dublicated object with key {" + key + "}");
            }

            ObjectOutputStream oos = null;

            try {
                oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(context);
                oos.flush();

            } catch (IOException e) {
                 LOG.log(Level.SEVERE, "Failed to sotre context. IOException. Error message: " + e.getMessage());
                throw new PersistencyException("Saving context failed due to error of writing to file " + filePath);
            } finally {
                try {
                    oos.close();
                } catch (IOException e) {
                       LOG.log(Level.WARNING, "Failed to close DataFileWriter after storing context. The message is: " + e.getMessage());
                }
            }
        }
    }


    public void init() throws InitializationException {

        File storageDir = new File(storageDirPath);
        if (!storageDir.exists()) {
            try {
                FileUtils.forceMkdir(storageDir);
            } catch (IOException e) {
                String errorMessage = "Failed to initialize auxiliary storage persistency manager. " +
                                      "Failed to create directory " + storageDirPath + " for file-based persistence storage. " +
                                      "Error message is: " + e.getMessage();
                LOG.log(Level.SEVERE, errorMessage);
                throw new InitializationException(errorMessage);
            }
        }
    }


    public void setStorageDirPath(String dirPath) {

        if (!dirPath.endsWith("/")) {
            dirPath += "/";
        }

        this.storageDirPath = dirPath;
    }

    private void checkFileIsJailedToStorageDir(File file) {
        File baseDirectory = new File(storageDirPath != null ? storageDirPath : "");
        try {
            if (!file.getCanonicalPath().startsWith(baseDirectory.getCanonicalPath())) {
                throw new PersistencyException("Path manipulation attack detected - attempting to write outside of the storageDirPath folder");
            }
        } catch (IOException e) {
            throw new PersistencyException("Error checking whether the file is jailed to the storage dir", e);
        }
    }


    private String createFilePath(String key) {
        if (storageDirPath == null) {
            storageDirPath = "";
            LOG.log(Level.WARNING, "Auxiliary file-based persistent storage directory path was not set.");
        }
        return storageDirPath + key + ".ctx";
    }


    @Override
    public void removeObject(String key) throws ObjectNotFoundException {
        String filePath = createFilePath(key);
        File file = new File(filePath);

        if (!file.exists()) {
            String errorMessage = "Attempt to remove non-existing object with key: " + key;
            LOG.log(Level.WARNING, errorMessage);
            throw new ObjectNotFoundException(errorMessage);
        }

        file.delete();
    }

    /**
     * Override ObjectInputStream so that only Strings can be deserialized
     */
    private static class PersistencyFileManagerObjectInputStream extends ObjectInputStream {

        public PersistencyFileManagerObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            if (!desc.getName().equals(String.class.getName())) {
                throw new InvalidClassException("Unauthorized deserialization attempt", desc.getName());
            }
            return super.resolveClass(desc);
        }
    }
}
