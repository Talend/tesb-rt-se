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

echo "Initialize Local Test Runtime Server"

echo
echo "Install tesb:start-all ......"
tesb:start-all
echo "Done"

echo
echo "Install feature:install activemq-broker ......"
feature:install activemq-broker
echo "Done"

echo
echo "Initalize finished successfully."

# Talend ESB Studio needs to read the ending tag on initializing, please do not remove it.
echo "EOF"
