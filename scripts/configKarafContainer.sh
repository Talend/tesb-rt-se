#
#    Licensed to the Apache Software Foundation (ASF) under one or more
#    contributor license agreements.  See the NOTICE file distributed with
#    this work for additional information regarding copyright ownership.
#    The ASF licenses this file to You under the Apache License, Version 2.0
#    (the "License"); you may not use this file except in compliance with
#    the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#
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
