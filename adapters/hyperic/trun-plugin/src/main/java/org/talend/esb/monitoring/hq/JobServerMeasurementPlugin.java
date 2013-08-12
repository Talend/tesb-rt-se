/*
 * #%L
 * Talend ESB :: Adapters :: HQ :: Talend Runtime Plugin
 * %%
 * Copyright (C) 2011 - 2013 Talend Inc.
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
package org.talend.esb.monitoring.hq;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.MetricInvalidException;
import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.jmx.MxMeasurementPlugin;
import org.hyperic.hq.product.jmx.MxUtil;

public class JobServerMeasurementPlugin extends MxMeasurementPlugin {

//	private final Logger log = Logger
//			.getLogger(JobServerMeasurementPlugin.class.getName());

	@Override
	public MetricValue getValue(Metric metric) throws PluginException,
			MetricNotFoundException, MetricUnreachableException {
		if (metric.isAvail()) {
			return super.getValue(metric);
		}
		
		try {
			String objNameStr = Metric.decode(metric.getObjectName());
			Object o = MxUtil.getValue(metric.getProperties(), objNameStr, metric.getAttributeName());
			
			if (o instanceof Collection<?>) {
				Collection<?> c = (Collection<?>) o;
				return new MetricValue(c.size());
			} else if (o instanceof Map<?, ?>) {
				Map<?, ?> m = (Map<?, ?>) o;
				return new MetricValue(m.size());
			} else if (o instanceof Number) {
				Number n = (Number) o;
				return new MetricValue(n);
			}
		} catch (IOException e) {
			throw new MetricUnreachableException("Error during communication with remote MBean Server.", e);
		} catch (Exception e) {
			throw new PluginException(e);
		}
		
		throw new MetricInvalidException("Cannot parse value of metric " + metric.toString());
	}
}
