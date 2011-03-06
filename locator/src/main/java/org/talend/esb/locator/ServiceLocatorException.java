/*******************************************************************************
*
* Copyright (c) 2011 Talend Inc. - www.talend.com
* All rights reserved.
*
* This program and the accompanying materials are made available
* under the terms of the Apache License v2.0
* which accompanies this distribution, and is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/

package org.talend.esb.locator;


public class ServiceLocatorException extends Exception {

	private static final long serialVersionUID = 7546855647307337936L;

	public ServiceLocatorException() {
	}

	public ServiceLocatorException(String msg) {
		super(msg);
	}

	public ServiceLocatorException(Throwable cause) {
		super(cause);
	}

	public ServiceLocatorException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
