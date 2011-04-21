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
REM set ZOO_LOG4J_PROP=INFO,CONSOLE

set CLASSPATH="%TESB_ADDONS%/locator/locator-4.0.jar;%TESB_ADDONS%/lib/zookeeper-3.3.3.jar;%TESB_ADDONS%/lib/log4j-1.2.15.jar"

echo on
java -cp "%CLASSPATH%" org.talend.esb.locator.ServiceLocatorMain  %*

endlocal
