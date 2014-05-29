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
package org.talend.esb.callcontext.store.persistence.jcr;


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
import org.talend.esb.callcontext.store.common.exception.CallContextAlreadyExistsException;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.InitializationException;
import org.talend.esb.callcontext.store.common.exception.PersistencyException;
import org.talend.esb.callcontext.store.persistence.AbstractPersistencyManager;

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
            String errorMessage = "Failed to initialize callcontext persistency manager. JCR repository factory is null.";
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        }


        HashMap<String,String> parameters = new HashMap<String, String>();
        parameters.put(RepositoryFactoryImpl.REPOSITORY_HOME, storageDirPath);
        parameters.put(RepositoryFactoryImpl.REPOSITORY_CONF, "etc/org.talend.esb.callback.repo.xml");


        try {
            repository = repositoryFactory.getRepository(parameters);
        } catch (RepositoryException e) {
            String errorMessage = "Failed to initialize callcontext persistency manager. " +
                                  "Failed to inititalize jackrabbit repository: " + e.getMessage();
            LOG.log(Level.SEVERE, errorMessage);
            throw new InitializationException(errorMessage);
        }
    }


    @Override
    public void storeCallContext(String context, String key) throws PersistencyException {

        Session session = null;
        Node rootNode;
        Node node;

        synchronized (this) {

            try {
                session = getSession();
                rootNode = session.getRootNode();

                if (rootNode.hasNode(key)) {
                    throw new CallContextAlreadyExistsException("Dublicated call context with key {" + key + "}");
                }

                node = rootNode.addNode(key);
                node.setProperty(CONTEXT_DATA_PROPERTY_NAME, context);
                session.save();

            } catch (RepositoryException e) {
                LOG.log(Level.SEVERE, "Failed to sotre context. RepositoryException. Error message: " + e.getMessage());
                throw new PersistencyException("Saving context failed due to error " + e.getMessage());
            } finally {
                releaseSession(session);
            }
        }
    }

    @Override
    public String restoreCallContext(String contextKey) throws PersistencyException {

        Node node = null;
        Property property = null;

        Session session=null;
        Node rootNode;

        synchronized (this) {
            try {
                session = getSession();
                rootNode = session.getRootNode();
                node = rootNode.getNode(contextKey);
                property = node.getProperty(CONTEXT_DATA_PROPERTY_NAME);
                return (property == null) ? null : property.getString();
            } catch (PathNotFoundException e) {
                return null;
            } catch (RepositoryException e) {
                LOG.log(Level.SEVERE, "Failed to resotre context. RepositoryException. Error message: " + e.getMessage());
                    throw new PersistencyException("Error retrieving context store node with the key "
                            + contextKey + "  Underlying error message is:" + e.getMessage());
            } finally {
                releaseSession(session);
            }
        }
    }


    @Override
    public void removeCallContext(String key) throws PersistencyException {

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
                String errorMessage = "Attempt to remove non-existing call context with key: " + key;
                LOG.log(Level.WARNING, errorMessage);
                throw new CallContextNotFoundException(errorMessage);
            } catch (RepositoryException e) {
                String errorMessage = "Attempt to remove call context with key: " + key + " failed. "
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
