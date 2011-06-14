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
 * Criteria for patterns
 * 
 * @author zubairov
 */
public class PatternCriteria extends Criteria {

	String pattern = null;
	
	String condition = null;
	
	public PatternCriteria(String name, String colunmName) {
		super(name, colunmName);
	}
	
	/**
	 * Conditional pattern criteria
	 * 
	 * @param name
	 * @param columnName
	 * @param condition condittion that will be concatenated to the pattern condition
	 */
	public PatternCriteria(String name, String columnName, String condition) {
		super(name, columnName);
		this.condition = condition;
	}
	

	@Override
	public Criteria[] parseValue(String attribute) {
		PatternCriteria result = new PatternCriteria(this.name, this.columnName, this.condition);
		result.pattern = toSQLPattern(attribute);
		return new Criteria[] {result};
	}

	@Override
	public Object getValue() {
		return pattern;
	}

	@Override
	public StringBuilder getFilterClause() {
		StringBuilder builder = new StringBuilder();
		builder.append(columnName);
		builder.append(" LIKE ");
		builder.append(":" + name);
		if (condition != null) {
			builder.append(" AND ");
			builder.append(condition);
		}
		return builder;
	}

	private String toSQLPattern(String attribute) {
		String pattern = attribute.replace("*", "%");
		if (!pattern.startsWith("%")) {
			pattern = "%" + pattern;
		}
		if (!pattern.endsWith("%")) {
			pattern = pattern.concat("%");
		}
		return pattern;
	}

}
