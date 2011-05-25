/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.sam.server.persistence.criterias;

/**
 * Criteria based on enumeration
 * 
 * @author zubairov
 * 
 */
public class EnumCriteria extends Criteria {

	@SuppressWarnings("rawtypes")
	private Class<? extends Enum> enumClass;

	private Enum<?> value;

	public EnumCriteria(String name, String colunmName,
			@SuppressWarnings("rawtypes") Class<? extends Enum> clazz) {
		super(name, colunmName);
		this.enumClass = clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Criteria parseValue(String value) {
		EnumCriteria result = new EnumCriteria(this.name, this.columnName,
				(Class<? extends Enum<?>>) this.enumClass);
		result.value = Enum.valueOf(enumClass, value);
		return result;
	}

	@Override
	public Object getValue() {
		return String.valueOf(value);
	}

	@Override
	public String getComparisonOperator() {
		return "=";
	}

}
