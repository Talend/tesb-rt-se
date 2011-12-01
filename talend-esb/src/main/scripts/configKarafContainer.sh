#
#
# Copyright (C) 2011 Talend Inc.
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

echo "################################ START #############################################"
echo "JMX Management configuration (etc/org.apache.karaf.management.cfg)"
config:edit --force org.apache.karaf.management
echo "rmiRegistryPort = $1"
config:propset rmiRegistryPort $1
echo "rmiServerPort = $2"
config:propset rmiServerPort $2
config:update

echo
echo "OSGI HTTP/HTTPS Service configuration (etc/org.ops4j.pax.web.cfg)"
config:edit --force org.ops4j.pax.web
echo "org.osgi.service.http.port = $3"
config:propset org.osgi.service.http.port $3
echo "org.osgi.service.http.port.secure = $4"
config:propset org.osgi.service.http.port.secure $4
config:update

echo
echo "Karaf SSH shell configuration (etc/org.apache.karaf.shell.cfg)"
config:edit --force org.apache.karaf.shell
echo "sshPort = $5"
config:propset sshPort $5
config:update

echo
echo "Locator client configuration (etc/org.talend.esb.locator.cfg)"
config:edit --force org.talend.esb.locator
echo "endpoint.prefix = http://localhost:$3/services"
config:propset endpoint.prefix http://localhost:$3/services
config:update

echo
echo "SAM agent configuration (etc/org.talend.esb.sam.agent.cfg)"
config:edit --force org.talend.esb.sam.agent
echo "service.url = http://localhost:$3/services/MonitoringServiceSOAP"
config:propset service.url http://localhost:$3/services/MonitoringServiceSOAP
config:update

echo
echo "DataService client sts configuration (etc/org.talend.esb.job.client.sts.cfg)"
config:edit --force org.talend.esb.job.client.sts
echo "sts.wsdl.location = http://localhost:$3/services/SecurityTokenService/UT?wsdl"
config:propset sts.wsdl.location http://localhost:$3/services/SecurityTokenService/UT?wsdl
config:update

echo
echo "Jobserver configuration (etc/org.talend.remote.jobserver.server.cfg)"
config:edit --force org.talend.remote.jobserver.server
echo "COMMAND_SERVER_PORT = $6"
config:propset COMMAND_SERVER_PORT $6
echo "FILE_SERVER_PORT = $7"
config:propset FILE_SERVER_PORT $7
echo "MONITORING_PORT = $8"
config:propset MONITORING_PORT $8
config:update
echo "################################ END ###############################################"