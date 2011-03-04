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

package demo.common;

import javax.jws.WebService;

@WebService(targetNamespace = "http://talend.org/esb/examples/", name = "Greeter")
public interface Greeter {
	String greetMe(String requestType);
}
