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
package org.talend.esb.auxiliary.storage.persistence.jcr;


import java.util.HashMap;
import java.util.logging.Level;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryFactoryImpl;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.InitializationException;
import org.talend.esb.auxiliary.storage.common.exception.PersistencyException;
import org.talend.esb.auxiliary.storage.persistence.AbstractPersistencyManager;

public class PersistencyJCRManager extends AbstractPersistencyManager {


    public static String CONTEXT_DATA_PROPERTY_NAME = "context";

    private RepositoryFactory repositoryFactory;
    private Repository repository;

    private String storageDirPath;



    public PersistencyJCRManager() {
        repositoryFactory = new RepositoryFactoryImpl();
    }


    public void init() throws InitializationException {

        if (repositoryFactory == null) {
            String errorMessage = "Failed to initialize auxiliary storage persistency manager. JCR repository factory is null.";
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        }


        HashMap<String,String> parameters = new HashMap<String, String>();
        parameters.put(RepositoryFactoryImpl.REPOSITORY_HOME, storageDirPath);
        parameters.put(RepositoryFactoryImpl.REPOSITORY_CONF, "etc/org.talend.esb.auxiliary.repo.xml");


        try {
            repository = repositoryFactory.getRepository(parameters);
        } catch (RepositoryException e) {
            String errorMessage = "Failed to initialize auxiliary storage persistency manager. " +
                                  "Failed to inititalize jackrabbit repository: " + e.getMessage();
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        }
    }


    @Override
    public void storeObject(String context, String key) throws PersistencyException {

        Session session = null;
        Node rootNode;
        Node node;

        synchronized (this) {

            try {
                session = getSession();
                rootNode = session.getRootNode();

                if (rootNode.hasNode(key)) {
                    throw new ObjectAlreadyExistsException("Dublicated object with key {" + key + "}");
                }

                node = rootNode.addNode(key);
                node.setProperty(CONTEXT_DATA_PROPERTY_NAME, context);
                session.save();

            } catch (RepositoryException e) {
                LOG.log(Level.SEVERE, "Failed to sotre object. RepositoryException. Error message: " + e.getMessage());
                throw new PersistencyException("Saving object failed due to error " + e.getMessage());
            } finally {
                releaseSession(session);
            }
        }
    }

    @Override
    public String restoreObject(String key) throws PersistencyException {

        Node node = null;
        Property property = null;

        Session session=null;
        Node rootNode;

        synchronized (this) {
            try {
                session = getSession();
                rootNode = session.getRootNode();
                node = rootNode.getNode(key);
                property = node.getProperty(CONTEXT_DATA_PROPERTY_NAME);
                return (property == null) ? null : property.getString();
            } catch (PathNotFoundException e) {
                return null;
            } catch (RepositoryException e) {
                LOG.log(Level.SEVERE, "Failed to resotre object. RepositoryException. Error message: " + e.getMessage());
                    throw new PersistencyException("Error retrieving auxiliary store node with the key "
                            + key + "  Underlying error message is:" + e.getMessage());
            } finally {
                releaseSession(session);
            }
        }
    }


    @Override
    public void removeObject(String key) throws PersistencyException {

        synchronized (this) {

            Session session=null;
            Node rootNode;

            try {
                session = getSession();
                rootNode = session.getRootNode();

                Node node = rootNode.getNode(key);
                node.remove();
                session.save();
            } catch (PathNotFoundException e) {
                String errorMessage = "Attempt to remove non-existing object with key: " + key;
                LOG.log(Level.WARNING, errorMessage);
                throw new ObjectNotFoundException(errorMessage);
            } catch (RepositoryException e) {
                String errorMessage = "Attempt to remove object with key: " + key + " failed. "
                        + "RepositoryException. Error message is: " + e.getMessage();
                LOG.log(Level.WARNING, errorMessage);
                throw new PersistencyException(errorMessage);
            } finally {
                 releaseSession(session);
            }
        }
    }

    public void setStorageDirPath(String storageDirPath) {
        this.storageDirPath = storageDirPath;
    }

    private Session getSession() {

        Session session = null;

        try {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            return session;
        } catch (LoginException e) {
            String errorMessage = "Failed to login to jackrabbit repository: " + e.getMessage();
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        } catch (RepositoryException e) {
            String errorMessage = "Error occured during login process to jackrabbit repository: " + e.getMessage();
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        }
    }

    private void releaseSession(Session session) {
        if (session != null) {
            session.logout();
            session = null;
        }
    }

}
