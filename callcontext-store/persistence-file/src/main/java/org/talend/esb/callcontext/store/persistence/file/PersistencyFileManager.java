/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.callcontext.store.persistence.file;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.commons.io.FileUtils;
import org.talend.esb.callcontext.store.common.exception.CallContextAlreadyExistsException;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.InitializationException;
import org.talend.esb.callcontext.store.common.exception.PersistencyException;
import org.talend.esb.callcontext.store.persistence.AbstractPersistencyManager;

public class PersistencyFileManager extends AbstractPersistencyManager {

    private Schema schema = null;

    private String storageDirPath = null;
    private String schemaResourceName = null;

    public PersistencyFileManager() {

    }

    @Override
    public String restoreCallContext(String contextKey) throws PersistencyException {

        synchronized (this) {

            String filePath = createFilePath(contextKey);
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }

            DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
            DataFileReader<GenericRecord> dataFileReader = null;

            String restoredContext = null;

            try {
                dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
                if (dataFileReader.hasNext()) {
                    restoredContext =  genericToContext(dataFileReader.next());
                }
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed to resotre context. IOException. Error message: " + e.getMessage());
                throw new PersistencyException("Error reading context store file "
                        + filePath + "  Underlying error message is:" + e.getMessage());
            } finally {
                if (dataFileReader != null) {
                    try {
                        dataFileReader.close();
                    } catch (IOException e) {
                       LOG.log(Level.WARNING, "Failed to close DataFileReader after restoring context. The message is: " + e.getMessage());
                    }
                }
            }

            return restoredContext;
        }
    }


    @Override
    public void storeCallContext(String context, String key) throws PersistencyException {

        synchronized (this) {

            String filePath = createFilePath(key);
            File file = new File(filePath);

            if (file.exists()) {
                throw new CallContextAlreadyExistsException("Dublicated call context with key {" + key + "}");
            }

            DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);

            try {

                dataFileWriter.create(schema, file);
                GenericRecord record = contextToGeneric(context);
                dataFileWriter.append(record);
            } catch (IOException e) {
                 LOG.log(Level.SEVERE, "Failed to sotre context. IOException. Error message: " + e.getMessage());
                throw new PersistencyException("Saving context failed due to error of writing to file " + filePath);
            } finally {
                try {
                    dataFileWriter.close();
                } catch (IOException e) {
                       LOG.log(Level.WARNING, "Failed to close DataFileWriter after storing context. The message is: " + e.getMessage());
                }
            }
        }
    }


    public void init() throws InitializationException {

        try {
            schema = (new Schema.Parser()).parse(PersistencyFileManager.class.getResourceAsStream(schemaResourceName));
        } catch (Exception e) {
            String errorMessage = "Failed to initialize callcontext persistency manager: " + e.getMessage();
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        }


        File storageDir = new File(storageDirPath);
        if (!storageDir.exists()) {
            try {
                FileUtils.forceMkdir(storageDir);
            } catch (IOException e) {
                String errorMessage = "Failed to initialize callcontext persistency manager. " +
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

    private GenericRecord contextToGeneric(String context) {

        GenericRecord record = new GenericData.Record(schema);

        record.put("bigString",  context);
        return record;
    }

    private String genericToContext(GenericRecord record) {
           String context = ((org.apache.avro.util.Utf8) record.get("bigString"))
					.toString();
           return context;
       }

    private String createFilePath(String key) {
        if (storageDirPath == null) {
            storageDirPath = "";
            LOG.log(Level.WARNING, "Call context file-based persistent storage directory path was not set.");
        }
        return storageDirPath + key + ".ctx";
    }


    @Override
    public void removeCallContext(String key) throws CallContextNotFoundException {
        String filePath = createFilePath(key);
        File file = new File(filePath);

        if (!file.exists()) {
            String errorMessage = "Attempt to remove non-existing call context with key: " + key;
            LOG.log(Level.WARNING, errorMessage);
            throw new CallContextNotFoundException(errorMessage);
        }

        file.delete();
    }


    public String getSchemaResourceName() {
        return schemaResourceName;
    }

    public void setSchemaResourceName(String schemaResourceName) {
        this.schemaResourceName = schemaResourceName;
    }
}
