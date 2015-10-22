Installation SAPJCO3 Destination Connection in Talend Runtime
=============================================================

Install SAP Java Connector 3.0
==============================
Make sure {sapjco3-install-path} added to the PATH environment variable.

Deploy sapjco3 jar to Talend Runtime
==================================================
In Talend Runtime install jar (correct path before)
In Karaf concole execute one by one:
bundle:install -s 'wrap:file:c:/Apps/jco/v3/sapjco3.jar$Bundle-SymbolicName=com.sap.conn.jco&Bundle-Version=7.30.1&Bundle-Name=SAP Java Connector v3'

Check jar is installed. In Karaf console:
karaf@trun> list | grep SAP
[ 311] [Active     ] [            ] [       ] [   80] SAP Java Connector v3 (7.30.1)

Install talend-sap-hibersap feature
===================================
After connector installed, execute in Karaf console:
feature:install talend-sap-hibersap

Check jars are installed. In Karaf console:
karaf@trun> list | grep Hibersap
[ 312] [Active     ] [            ] [       ] [   80] Hibersap Core (1.2.0)
[ 313] [Active     ] [            ] [       ] [   80] Hibersap JCo (1.2.0)

Connection pool configuration/deployment
========================================
1. Connection configuration.
Some intro:
In JCo 3.0, the connection setup is no longer implemented explicitly 
using a direct or pooled connection.

Instead, the type of connection is determined only by the connection 
properties (properties) that define a direct or pooled connection 
implicitly. A destination model is used which defines a connection 
type for each destination. Thus, by specifying the destination name, 
the corresponding connection is set up using either a direct or 
pooled connection.
Source: http://help.sap.com/saphelp_nwpi711/helpdata/en/48/874bb4fb0e35e1e10000000a42189c/content.htm?frameset=/en/48/634503d4e9501ae10000000a42189b/frameset.htm

Connection configuration defined in org.talend.sap.connection.cfg.
Having jco.destination.peak_limit and jco.destination.pool_capacity we
define connection pool properties.

Correct values of the connection if needed.

Destination connection name defined in the Spring configuration 
org.talend.sap.connection.xml:
<value>SAP_CONNECTION_POOL</value>

2. Deploy to Talend Runtime.
Copy 
- org.talend.sap.connection.cfg to Talend-ESB\container\etc\
- org.talend.sap.connection.xml to Talend-ESB\container\deploy\

After that SAP_CONNECTION_POOL is created and can be used.

Check configuration is installed. 
In Karaf console:
karaf@trun> list | grep SAPJCo3
[ 317] [Active     ] [            ] [Started] [   80] Talend SAPJCo3 Connector (5.5)