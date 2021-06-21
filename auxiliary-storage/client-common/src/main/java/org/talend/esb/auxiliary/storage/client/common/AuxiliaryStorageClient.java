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
package org.talend.esb.auxiliary.storage.client.common;

import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;

/**
 * Public interface for the Registry client.
 */
public interface AuxiliaryStorageClient<E> {

    /**
     * gets AuxiliaryObject factory
     * @return Auxiliary Object factory
     */
	public AuxiliaryObjectFactory<E> getAuxiliaryObjectFactory();

    /**
     * sets AuxiliaryObject factory
     * @param AuxiliaryObject factory
     */
    public void setAuxiliaryObjectFactory(AuxiliaryObjectFactory<E> factory);

    /**
     * lookup stored by context key and policy alias
     * @param context key
     * @return Stored object
     */
    public E getStoredObject(String key);

    /**
     * save object in auxiliary persistence storage
     * @param saved object
     * @return context key
     */
    public String saveObject(E obj);

    /**
     * remove stored object from aux persistence storage
     * @param context key
     */
    public void removeStoredObject(String key);
}
