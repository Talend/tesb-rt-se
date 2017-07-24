package org.talend.esb.sam.server.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

public class DataSourceProxyFactory implements InvocationHandler {
	
	public static final String PROP_DS_NAME = "org.talend.esb.datasource.name";
	
	/* --------------------- */

	private BundleContext bundleContext;

	private String dsName;

	private long timeout;

	/* --------------------- */

	private ServiceTracker<DataSource, DataSource> tracker;

	private DataSource proxy;

	private Filter filter;

	/* --------------------- */

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		if (dsName == null || dsName.length() == 0) {
			throw new IllegalArgumentException();
		}
		if (dsName.contains("\"") || dsName.contains(")")) {
			throw new IllegalArgumentException();
		}
		this.dsName = dsName;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException();
		}
		this.timeout = timeout;
	}

	/* --------------------- */

	public void init() throws Exception {
		filter = bundleContext.createFilter(getFilter(dsName));
		
		tracker = new ServiceTracker<DataSource, DataSource>(bundleContext, filter, null);
		tracker.open();
		
		proxy = (DataSource) Proxy.newProxyInstance(
				DataSourceProxyFactory.class.getClassLoader(),
				new Class<?>[] { DataSource.class }, this);
	}
	
	public void destroy() throws Exception {
		tracker.close();
	}

	/* --------------------- */

	public DataSource getDatasource() {
		return proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		DataSource realDs = tracker.waitForService(timeout);
		if (realDs == null) {
			throw new SQLException("Datasource '" + dsName + "' is not available.");
		}
		try {
		    return method.invoke(realDs, args);
		} catch (InvocationTargetException e) {
		    throw e.getTargetException();
		}
	}

	protected String getFilter(String dsName) {
		StringBuilder sb = new StringBuilder("(&(");
		sb.append(Constants.OBJECTCLASS);
		sb.append('=');
		sb.append(DataSource.class.getName());
		sb.append(")(");
		sb.append(PROP_DS_NAME);
		sb.append('=');
		sb.append(dsName);
		sb.append("))");
		return sb.toString();
	}
}
