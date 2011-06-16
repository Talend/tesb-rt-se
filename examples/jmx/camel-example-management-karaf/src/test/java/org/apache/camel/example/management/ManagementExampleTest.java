
package org.apache.camel.example.management;

import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version 
 */
public class ManagementExampleTest extends CamelSpringTestSupport {

    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }

    @Test
    public void testManagementExample() throws Exception {
        // Give it a bit of time to run
        Thread.sleep(2000);

        MBeanServer mbeanServer = context.getManagementStrategy().getManagementAgent().getMBeanServer();

        // Find the endpoints
        Set<ObjectName> set = mbeanServer.queryNames(new ObjectName("*:type=endpoints,*"), null);
        // now there is no managed endpoint for the dead queue
        assertEquals(5, set.size()); 
        
        // Find the routes
        set = mbeanServer.queryNames(new ObjectName("*:type=routes,*"), null);
        assertEquals(3, set.size());
        
        // Stop routes
        for (ObjectName on : set) {
            mbeanServer.invoke(on, "stop", null, null);
        }
    }

}
