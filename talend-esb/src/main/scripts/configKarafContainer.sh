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

echo "JMX configuration properties"
config:edit --force org.apache.karaf.management
echo "Setting (RMI Registry Port, $1)"
config:propset rmiRegistryPort $1
echo "Setting (RMI Server Port, $2)"
config:propset rmiServerPort $2
config:update
#
echo "Pax web configuration properties"
config:edit --force org.ops4j.pax.web
echo "Setting (HTTP Port, $3)"
config:propset org.osgi.service.http.port $3
echo "Setting (HTTPS Port, $4)"
config:propset org.osgi.service.http.port.secure $4
config:update
#
echo "Karaf shell SSH configuration properties"
config:edit --force org.apache.karaf.shell
echo "Setting (SSH port, $5)"
config:propset sshPort $5
config:update
#
echo "Jobserver configuration properties"
config:edit --force org.talend.remote.jobserver.server
echo "Setting (command server port, $6)"
config:propset COMMAND_SERVER_PORT $6
echo "Setting (file server port, $7)"
config:propset FILE_SERVER_PORT $7
echo "Setting (monitoring port, $8)"
config:propset MONITORING_PORT $8
config:update
