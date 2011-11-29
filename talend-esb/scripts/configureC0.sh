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

HTTP_Port=8040
HTTPS_Port=9001
RMI_Registry_Port=1099
RMI_Server_Port=44444
SSH_Port=8101

#addcommand system (loadClass java.lang.System)
#KARAF_HOME = system:getProperty karaf.home
#KARAF_FILE = (new java.io.File $KARAF_HOME)

KARAF_FILE = (new java.io.File ".")
KARAF_PATH = $KARAF_FILE getCanonicalPath
KARAF_FILE = (new java.io.File $KARAF_PATH)
TESB_FILE = $KARAF_FILE getParentFile
TESB_HOME = $TESB_FILE getCanonicalPath

source $KARAF_PATH/scripts/configKarafContainer.sh $RMI_Registry_Port $RMI_Server_Port $HTTP_Port $HTTPS_Port $SSH_Port
#source scripts/configKarafContainer.sh $RMI_Registry_Port $RMI_Server_Port $HTTP_Port $HTTPS_Port $SSH_Port
