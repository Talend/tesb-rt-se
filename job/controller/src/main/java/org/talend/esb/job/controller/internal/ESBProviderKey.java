/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal;

import javax.xml.namespace.QName;

public class ESBProviderKey {

	private final QName serviceName;
	private final QName portName;

	public ESBProviderKey(
			final QName serviceName,
			final QName portName) {
		this.serviceName = serviceName;
		this.portName = portName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ESBProviderKey) {
			ESBProviderKey anotherObj = (ESBProviderKey)obj;
			return serviceName.equals(anotherObj.serviceName) && portName.equals(anotherObj.portName);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return serviceName.hashCode() ^ portName.hashCode();
	}
}
