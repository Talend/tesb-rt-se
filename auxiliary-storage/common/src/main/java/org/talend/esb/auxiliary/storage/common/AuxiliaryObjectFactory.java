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
package org.talend.esb.auxiliary.storage.common;

public interface  AuxiliaryObjectFactory<E> {

	public String marshalObject(E ctx);

	public E unmarshallObject(String marshalledData);

	public String createObjectKey(E ctx);

	public String contentType();
}
