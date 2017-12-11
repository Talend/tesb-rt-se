/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2017 Talend Inc. - www.talend.com
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
package org.talend.esb.auxiliary.storage.common;

public interface  AuxiliaryObjectFactory<E> {

	public String marshalObject(E ctx);

	public E unmarshallObject(String marshalledData);

	public String createObjectKey(E ctx);

	public String contentType();
}
