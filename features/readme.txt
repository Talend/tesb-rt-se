List of TESB features:

tesb-sam-agent
--------------
install the Agent to the OSGi Container.
Depoy next bundles:
	geronimo-servlet_2.5_spec
	geronimo-el_1.0_spec
	geronimo-jsp_2.1_spec
	org.apache.servicemix.bundles.commons-beanutils
	org.apache.servicemix.bundles.jdom
	commons-jxpath
	commons-lang
	sam-common
	sam-agent

tesb-derby-starter
------------------
install the Derby database into the OSGI container and start running it.
Depoy next bundles:
	derby-all
	derby-starter

tesb-sam-server
---------------
install Monitoring Server in a Talend ESB container:
Depoy next bundles:
	geronimo-servlet_2.5_spec
	geronimo-el_1.0_spec
	geronimo-jsp_2.1_spec
	org.apache.servicemix.bundles.commons-beanutils
	org.apache.servicemix.bundles.jdom
	commons-jxpath
	commons-lang
	commons-dbcp
	derby
	derbyclient
	spring-jdbc
	spring-test
	sam-common
	sam-server

tesb-locator-client
-------------------
install locator client in a Talend ESB container
Depoy next bundles:
	locator
	zookeeper

tesb-zookeeper-server
---------------------
install and run zookeeper server in a Talend ESB container.
	zookeeper
	cxf-dosgi-ri-discovery-distributed-zookeeper-server
