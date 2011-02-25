Service Activity Monitoring
---------------------------

Supports monitoring and central collection of service requests and responses on client and server side

Subprojects
-----------

Agent: 
  Runs together with CXF on service client and provider. The monitoring events are processed asynchronously to the main message flow. Filters and
  Handlers allow to decide which messages and what parts are monitored. The monitoring event will then be sent to the monitoring service.

MonitoringService: 
  Receives monitoring events and stores them into a database

PersistentQueue: 
  Implementation of a persitent queue from gaborcselle. This will perhaps be replaced

Common: 
  Currently contains shared code between Agent and Server. Will soon be deleted

Dummy: 
  Test Client and Service that shows how to use the Monitoring. Should rather be named examples
