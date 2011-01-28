package org.talend.esb.locator.sample;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.jms.spec.JMSSpecConstants;
import org.talend.esb.locator.LocatorRegistrar;
import org.talend.esb.locator.ServiceLocator;
import org.talend.esb.locator.ServiceLocatorException;


public class ServerJMS {
	
	private static final Logger LOG = Logger.getLogger(ServerJMS.class.getName());
	
	protected ServerJMS(String jmsConnectionString, String locatorEndpoints) throws Exception {
		
		initForLocator(locatorEndpoints);
		publishService(jmsConnectionString);        
        System.out.println("Server started");
    }

	private Bus publishService(String jmsConnectionString) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		boolean launchAmqBroker = false;
        boolean jaxws = true;
        
        if (launchAmqBroker) {
        	if (LOG.isLoggable(Level.INFO)) {
    			LOG.info("Starting ActiveMQ");
    		}
            Class<?> brokerClass = ServerJMS.class.getClassLoader().loadClass("org.apache.activemq.broker.BrokerService");
            if (brokerClass == null) {
        		if (LOG.isLoggable(Level.SEVERE)) {
        			LOG.severe("ActiveMQ is not in the classpath, cannot launch broker.");
        		}
                return null;
            }
            Object broker = brokerClass.newInstance();
            Method addConnectorMethod = brokerClass.getMethod("addConnector", String.class);
            addConnectorMethod.invoke(broker, "tcp://localhost:61616");
            Method startMethod = brokerClass.getMethod("start");
            startMethod.invoke(broker);
        }
        if (jaxws) {
    		if (LOG.isLoggable(Level.INFO)) {
    			LOG.info("Publishing JMS service with JaxWS");
    		}
            return launchJaxwsApi(jmsConnectionString);
        } else {
        	if (LOG.isLoggable(Level.INFO)) {
    			LOG.info("Publishing JMS service with CXF Api");
    		}
        	return launchCxfApi(jmsConnectionString);
        }
	}
	
	private static Bus launchCxfApi(String jmsConnectionString) {
		Object implementor = new org.talend.esb.sample.cxf.GreeterImpl();
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceClass(org.talend.esb.sample.cxf.Greeter.class);
        svrFactory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
        svrFactory.setAddress(jmsConnectionString);
        svrFactory.setServiceBean(implementor);
        svrFactory.create();
        return svrFactory.getBus();
    }

    private static Bus launchJaxwsApi(String jmsConnectionString) {
    	EndpointImpl ei = (EndpointImpl)Endpoint.publish(jmsConnectionString, new org.talend.esb.sample.cxf.GreeterImpl());
    	return ei.getBus();
    }

	private void initForLocator(String locatorEndpoints) throws IOException,
			InterruptedException, ServiceLocatorException {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Registering jms service in ZooKeeper.");
		}
        Bus bus = BusFactory.getDefaultBus();
 
        ServiceLocator lc = new ServiceLocator();
        lc.setLocatorEndpoints(locatorEndpoints);
        lc.setSessionTimeout(30 * 60 * 1000);
        lc.setConnectionTimeout(30 * 60 * 1000);
        lc.connect();
        
        LocatorRegistrar lr = new LocatorRegistrar();
        lr.setLocatorClient(lc);
        lr.setBus(bus);
	}

    public static void main(String args[]) throws Exception {
    	new ServerJMS(Constants.JMS_ENDPOINT_URI, Constants.LOCATORENDPOINT);
        System.out.println("Server ready...");
        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
