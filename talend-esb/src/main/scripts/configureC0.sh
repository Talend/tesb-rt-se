#
#
# Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

HTTP_Port=8040
HTTPS_Port=9001
RMI_Registry_Port=1099
RMI_Server_Port=44444
SSH_Port=8101
COMMAND_SERVER_PORT=8000
FILE_SERVER_PORT=8001
MONITORING_PORT=8888
PROCESS_MESSAGE_PORT=8555

source scripts/configKarafContainer.sh $RMI_Registry_Port $RMI_Server_Port $HTTP_Port $HTTPS_Port $SSH_Port $COMMAND_SERVER_PORT $FILE_SERVER_PORT $MONITORING_PORT $PROCESS_MESSAGE_PORT
