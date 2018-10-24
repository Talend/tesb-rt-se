Installing SAPJCO3 Destination Connection to Talend Runtime
=============================================================

Installing SAP Java Connector 3.0
==================================
For Windows OS: 
After installing SAP Java Connector, ensure that the installation path <sapjco3_home> is added to your PATH environment variable.

For Linux OS (installation of ESB as a Service): 
Copy the file libsapjco3.so into <Talend-ESB>/container/lib/wrapper/

Deploying sapjco3 jar in Talend Runtime
=======================================
Start the Talend Runtime Karaf console and use the following command:
bundle:install -s 'wrap:file:<sapjco3_home>/sapjco3.jar$Bundle-SymbolicName=com.sap.conn.jco&Bundle-Version=7.30.1&Bundle-Name=SAP_Java_Connector_v3'

where <sapjco3_home> is the path where SAP Java Connector is installed in the previous step.

To check that the JAR is installed correctly, use the following command in the Karaf console:
karaf@trun> list | grep SAP

316 | Active   |  80 | 7.30.1                | SAP_Java_Connector_v3

Installing talend-sap-hibersap
==============================
After the connector is deployed, use the following command in the Karaf console to install the talend-sap-hibersap feature:
feature:install talend-sap-hibersap

To check that the JARs are installed correctly, use the following command in the Karaf console:
karaf@trun> list | grep Hibersap

317 | Active   |  80 | 1.2.0                 | Hibersap Core
318 | Active   |  80 | 1.2.0                 | Hibersap JCo

Connection pool configuration and deployment
============================================
1. Connection configuration.
In JCo 3.0, the connection setup is no longer implemented explicitly 
using a direct or pooled connection.

Instead, the type of connection is determined only by the connection 
properties (properties) that define a direct or pooled connection 
implicitly. A destination model is used which defines a connection 
type for each destination. Thus, by specifying the destination name, 
the corresponding connection is set up using either a direct or 
pooled connection.
Source: http://help.sap.com/saphelp_nwpi711/helpdata/en/48/874bb4fb0e35e1e10000000a42189c/content.htm?frameset=/en/48/634503d4e9501ae10000000a42189b/frameset.htm

Connection configuration is defined in org.talend.sap.connection.cfg.
The values of jco.destination.peak_limit and jco.destination.pool_capacity are used to
define connection pool properties.

Ensure that the connection parameters are correct.

The destination connection name is defined in the Blueprint configuration of the talend-sapjco3-connector-5.5.0.jar
src/main/resources/OSGI-INF/blueprintorg.talend.sap.connection.xml:
<property name="connectionPoolName" value="SAP_CONNECTION_POOL"/>

2. Deploying the connection pool to Talend Runtime.
Copy the file org.talend.sap.connection.cfg to <Talend-ESB>\container\etc\
Copy the file talend-sapjco3-connector-5.5.0.jar to <Talend-ESB>\container\deploy\

Now SAP_CONNECTION_POOL is created and can be used.

To check if the connection pool is deployed correctly, use the following command in the Karaf console: 
karaf@trun> list | grep SAPJCo3

326 | Active   |  80 | 5.5.0                 | Talend SAPJCo3 Connector