@echo off
REM Licensed to the Apache Software Foundation (ASF) under one or more
REM contributor license agreements.  See the NOTICE file distributed with
REM this work for additional information regarding copyright ownership.
REM The ASF licenses this file to You under the Apache License, Version 2.0
REM (the "License"); you may not use this file except in compliance with
REM the License.  You may obtain a copy of the License at
REM
REM     http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

setlocal enabledelayedexpansion

if "%1"=="" (
	echo Usage: %~dp0zkServer.cmd { start : stop }
	goto END
)

if not defined JMXLOCALONLY ( 
	set JMXLOCALONLY=false 
)

if not defined JMXDISABLE (
	echo "JMX enabled by default"
	set ZOOMAIN=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=%JMXLOCALONLY% org.apache.zookeeper.server.quorum.QuorumPeerMain
) else (
	echo "JMX disabled by user request"
    set ZOOMAIN="org.apache.zookeeper.server.quorum.QuorumPeerMain"
)

call "%~dp0zkEnv.cmd"

if not "%2"=="" (
	set ZOOCFG="%ZOOCFGDIR%\%2"
)

echo "Using config: %ZOOCFG%"

if "%1"=="start" (
	echo  "Starting zookeeper ... "
	title zookeeper
	for /F "tokens=2 delims= " %%A in ('TASKLIST /FI ^"WINDOWTITLE eq zookeeper^" /NH') do ( 
	set /A ZOOPID=%%A 
	echo !ZOOPID!>zookeeper_server.pid
)
	if exist java (
	start "zookeeper" /b java  "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %JVMFLAGS% %ZOOMAIN% "%ZOOCFG%"
    echo STARTED	
	)
	if not exist java (
		if defined JAVA_HOME (
		path=%PATH%;%JAVA_HOME%\bin
		start "zookeeper" /b java  "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %JVMFLAGS% %ZOOMAIN% "%ZOOCFG%"
		echo STARTED
	 ) else (
	  echo JAVA_HOME doesn`t exist
	 )
)
)

if "%1"=="stop" (
	echo  "Stopping zookeeper ... "
	if not exist zookeeper_server.pid ( echo "error: could not find file zookeeper_server.pid"
	exit /B 1 )
	For /F "Delims=" %%I In (zookeeper_server.pid) Do Set ZOOPID=%%~I
	set /A ZOOPID=!ZOOPID!
	del zookeeper_server.pid
	taskkill /t /f /pid !ZOOPID!
	echo STOPED
)
 
:END
 
endlocal