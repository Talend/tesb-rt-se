package org.talend.cxf.crypto.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.cm.Configuration;

public class CryptoPropertiesFactory {

	private static final Logger LOG = Logger.getLogger(CryptoPropertiesFactory.class.getName());

	private Map<String, Object> defaultProperties;
	private Map<String, Object> updatedProperties;
	private Configuration updatedPropertiesConfiguration;
	private String propertiesSource;

	public Map<String, Object> getDefaultProperties() {
		return defaultProperties;
	}

	public void setDefaultProperties(Map<String, Object> defaultProperties) {
		this.defaultProperties = defaultProperties;
	}

	public Map<String, Object> getUpdatedProperties() {
		return updatedProperties;
	}

	public void setUpdatedProperties(Map<String, Object> updatedProperties) {
		this.updatedProperties = updatedProperties;
	}

	public Configuration getUpdatedPropertiesConfiguration() {
		return updatedPropertiesConfiguration;
	}

	public void setUpdatedPropertiesConfiguration(Configuration updatedPropertiesConfiguration) {
		this.updatedPropertiesConfiguration = updatedPropertiesConfiguration;
	}

	public String getPropertiesSource() {
		return propertiesSource;
	}

	public void setPropertiesSource(String propertiesSource) {
		this.propertiesSource = propertiesSource;
	}

	public Properties createProperties() {
		Properties result = new Properties();
		if (defaultProperties != null) {
			LOG.fine("Adding default properties");
			result.putAll(defaultProperties);
		}
		loadURLProperties(result);
		updateFromConfiguration(result);
		if (updatedProperties != null) {
			LOG.fine("Adding updated properties");
			result.putAll(updatedProperties);
		}
		return result;
	}

	private void loadURLProperties(Properties props) {
		if (propertiesSource == null || propertiesSource.isEmpty()) {
			return;
		}
		try {
			LOG.fine("Loading external properties");
			URL propsUrl = new URL(propertiesSource);
			try (InputStream is = propsUrl.openStream()) {
				props.load(is);
			}
		} catch (MalformedURLException e) {
			LOG.info("Properties source is not an URL, assuming file name");
			LOG.log(Level.FINE, "MalformedURLException caught. ", e);
			String propsFileName = propertiesSource;
			if (propsFileName.indexOf('/') < 0 && propsFileName.indexOf('\\') < 0) {
			    propsFileName = System.getProperty("karaf.etc", "etc") + "/keystores/" + propsFileName;
			}
			File propsFile = new File(propsFileName);
			try (InputStream is = new FileInputStream(propsFile)) {
				props.load(is);
			} catch (IOException ex) {
				LOG.log(Level.WARNING, "Exception caught loading external properties. ", ex);
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Exception caught loading external properties. ", e);
		}
	}

	private void updateFromConfiguration(Properties props) {
		if (updatedPropertiesConfiguration == null) {
			return;
		}
		LOG.fine("Adding updated properties from configuration");
		try {
			updatedPropertiesConfiguration.update();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Exception caught re-loading OSGi configuration. ", e);
		}
		Dictionary<String, Object> cfgProps = updatedPropertiesConfiguration.getProperties();
		if (cfgProps != null) {
			for (Enumeration<String> i = cfgProps.keys(); i.hasMoreElements();) {
				String key = i.nextElement();
				Object value = cfgProps.get(key);
				if (value != null) {
					props.put(key, value);
				}
			}
		}
	}
}
