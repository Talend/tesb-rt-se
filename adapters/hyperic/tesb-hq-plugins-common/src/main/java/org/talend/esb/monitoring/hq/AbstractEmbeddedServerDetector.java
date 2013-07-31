/*
 * #%L
 * Talend ESB :: Adapters :: HQ :: Common
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.PluginManager;
import org.hyperic.hq.product.ServerResource;
import org.hyperic.hq.product.jmx.MxUtil;
import org.hyperic.util.config.ConfigResponse;

import static org.talend.esb.monitoring.hq.HypericUtils.getMandatoryProperty;
import static org.talend.esb.monitoring.hq.HypericUtils.getOptionalProperty;

public abstract class AbstractEmbeddedServerDetector extends
		DynamicMxFieldServerDetector {

	protected final Log log = getLog();

	protected ObjectName targetDomainPattern;
	protected ObjectName absentDomainPattern;
	protected String title;

	@Override
	public void init(final PluginManager manager) throws PluginException {
		super.init(manager);

		title = getMandatoryProperty(this, TalendHqConstants.PROP_SERVER_TITLE);

		try {
			targetDomainPattern = new ObjectName(getMandatoryProperty(this,
					TalendHqConstants.PROP_TARGET_DOMAIN) + ":*");
		} catch (MalformedObjectNameException e) {
			throw new PluginException(e);
		}

		try {
			String objName = getOptionalProperty(this,
					TalendHqConstants.PROP_ABSENT_DOMAIN);
			if (objName != null) {
				absentDomainPattern = new ObjectName(objName + ":*");
			}
		} catch (MalformedObjectNameException e) {
			throw new PluginException(e);
		}
	}

	/**
	 * This function returns the version file, assuring that it's exactly of
	 * expected version.
	 */
	@Override
	protected File findVersionFile(final File dir, final Pattern pattern) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("This is not a directory: "
					+ dir.getAbsolutePath());
		}

		final File[] fileList = dir.listFiles();
		if (fileList == null) {
			return null;
		}

		final Set<File> subs = new HashSet<File>();

		for (final File file : fileList) {
			if (file.isDirectory()) {
				subs.add(file);
			} else {
				if (versionFileMatches(file.getAbsolutePath(), pattern)) {
					return file;
				}
			}
		}

		for (final File subDir : subs) {
			final File versionFile = findVersionFile(subDir, pattern);
			if (versionFile != null) {
				return versionFile;
			}
		}

		return null;
	}

	protected boolean versionFileMatches(final String fileName,
			final Pattern pattern) {
		final Matcher m = pattern.matcher(fileName);
		if (m.find() && m.groupCount() > 0
				&& getTypeInfo().getVersion().equals(m.group(1))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getServerResources(final ConfigResponse platformConfig)
			throws PluginException {

		final List<ServerResource> discoveredServers = super
				.getServerResources(platformConfig);
		final List<ServerResource> serversToReturn = new ArrayList<ServerResource>();

		for (final ServerResource serverRes : discoveredServers) {
			JMXConnector jmxConnector = null;

			try {
				jmxConnector = MxUtil.getMBeanConnector(serverRes.getProductConfig()
						.toProperties());

				Set<ObjectName> objNames = jmxConnector.getMBeanServerConnection()
						.queryNames(targetDomainPattern, null);

				if (objNames.size() > 0) {

					if (absentDomainPattern != null) {
						objNames = jmxConnector.getMBeanServerConnection().queryNames(
								absentDomainPattern, null);

						if (objNames.size() > 0) {
							continue;
						}
					}

					final StringBuilder sb = new StringBuilder();

					sb.append(title);
					sb.append("at");
					sb.append(serverRes.getInstallPath());

					serverRes.setIdentifier(sb.toString());
					serversToReturn.add(serverRes);
				}
			} catch (IOException e) {
				log.debug(
						"Unable to check whether destination MBeanServer contains domain "
								+ targetDomainPattern.toString(), e);
			} finally {
				try {
					if (jmxConnector != null) {
						jmxConnector.close();
					}
				} catch (IOException e) {
					log.debug(
							"Exception during closing the connection to MBeanServer.",
							e);
				}
			}
		}

		return serversToReturn;
	}
}
