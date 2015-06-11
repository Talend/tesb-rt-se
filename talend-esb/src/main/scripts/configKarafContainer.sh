#
#
# Copyright (C) 2011 - 2012 Talend Inc.
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

# This is NOT a OS shell script, but a Karaf script
# To execute it, open a Karaf shell for your container and type: source scripts/<This script's name>

echo "Start configuring ......"

echo
echo "JMX Management configuration (PID: org.apache.karaf.management.cfg)"
config:edit --force org.apache.karaf.management
echo "rmiRegistryPort = $1"
config:property-set rmiRegistryPort $1
echo "rmiServerPort = $2"
config:property-set rmiServerPort $2
config:property-set serviceUrl service:jmx:rmi://\${rmiServerHost}:\${rmiServerPort}/jndi/rmi://\${rmiRegistryHost}:\${rmiRegistryPort}/karaf-\${karaf.name}
config:update

echo
echo "OSGI HTTP/HTTPS Service configuration (Pid: org.ops4j.pax.web.cfg)"
config:edit --force org.ops4j.pax.web
echo "org.osgi.service.http.port = $3"
config:property-set org.osgi.service.http.port $3
echo "org.osgi.service.http.port.secure = $4"
config:property-set org.osgi.service.http.port.secure $4
config:update

echo
echo "Karaf SSH shell configuration (Pid: org.apache.karaf.shell.cfg)"
config:edit --force org.apache.karaf.shell
echo "sshPort = $5"
config:property-set sshPort $5
config:update

echo
echo "Locator client configuration (Pid: org.talend.esb.locator.cfg)"
config:edit --force org.talend.esb.locator
echo "endpoint.http.prefix = http://localhost:$3/services"
echo "endpoint.https.prefix = https://localhost:$4/services"
config:property-set endpoint.http.prefix http://localhost:$3/services
config:property-set endpoint.https.prefix https://localhost:$4/services
config:update

echo
echo "Jobserver configuration (Pid: org.talend.remote.jobserver.server.cfg)"
config:edit --force org.talend.remote.jobserver.server
echo "org.talend.remote.jobserver.server.TalendJobServer.COMMAND_SERVER_PORT = $6"
config:property-set org.talend.remote.jobserver.server.TalendJobServer.COMMAND_SERVER_PORT $6
echo "org.talend.remote.jobserver.server.TalendJobServer.FILE_SERVER_PORT = $7"
config:property-set org.talend.remote.jobserver.server.TalendJobServer.FILE_SERVER_PORT $7
echo "org.talend.remote.jobserver.server.TalendJobServer.MONITORING_PORT = $8"
config:property-set org.talend.remote.jobserver.server.TalendJobServer.MONITORING_PORT $8
config:update

echo
echo "Configuration finished successfully."