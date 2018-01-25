/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.talend.esb.security.oidc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * An outbound REST interceptor which receives OIDC Access Token from 
 * OIDC server and puts it to outgoing message header
 */
public class OIDCRESTOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private String username;
	private String password;
	private Map<String, String> oidcProperties;

	public OIDCRESTOutInterceptor(String username, String password) {
		super(Phase.POST_PROTOCOL);
		this.username = username;
		this.password = password;
	}
	
	public OIDCRESTOutInterceptor(String username, String password, Map<String, String> oidcProperties) {
		super(Phase.POST_PROTOCOL);
		this.username = username;
		this.password = password;
		this.oidcProperties = oidcProperties;
	}
	
	
	

	@Override
	public void handleMessage(Message message) throws Fault {

		if (isRequestor(message)) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
			try {
				headers.put("Authorization",
						Collections.singletonList(OidcClientUtils.oidcClientBearer(username, password, oidcProperties)));
			} catch (RuntimeException ex) {
	            throw new Fault(ex);
	        } catch (Exception ex) {
	            StringWriter sw = new StringWriter();
	            ex.printStackTrace(new PrintWriter(sw));
	            throw new Fault(new RuntimeException(ex.getMessage() + ", stacktrace: " + sw.toString()));
	        }
		}
	}
}
