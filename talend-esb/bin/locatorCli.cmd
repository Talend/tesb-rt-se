@echo off
REM #%L
REM Service Locator CLI
REM %%
REM Copyright (C) 2011 Talend Inc.
REM %%
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM 
REM     http://www.apache.org/licenses/LICENSE-2.0
REM 
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM #L%
REM 
REM Display the contents of a Service Locator
REM

setlocal
set TESB_ADDONS=%~dp0..\add-ons
set CXF_MODULES=%~dp0..\modules

set CLASSPATH="%TESB_ADDONS%/locator/locator-${project.version}.jar;%TESB_ADDONS%/lib/zookeeper-${zookeeper.version}.jar;%TESB_ADDONS%/lib/log4j-${log4j.version}.jar,%CXF_MODULES%\cxf-api-${cxf.version}.jar"

echo on
java -cp %CLASSPATH% org.talend.esb.locator.ServiceLocatorMain  %*

endlocal
