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

package demo.service;

import javax.jws.WebService;

import demo.common.Greeter;

@WebService(targetNamespace = "http://talend.org/esb/examples/", serviceName = "GreeterService")
public class GreeterImpl implements Greeter {

	public String greetMe(String me) {
		System.out.println("Executing operation greetMe");
		System.out.println("Message received: " + me + "\n");
		return "Hello " + me;
	}
}
