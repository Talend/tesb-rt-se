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

set ISJAVA=NO
	for %%G in ("%path:;=" "%") do (
	if exist %%G\java.exe (
	set ISJAVA=YES
	)
)

if "%1"=="" (
	echo Usage: %~dp0zkServer.cmd { start : stop }
	goto NOCOMMAND
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

set T=%TEMP%\sthUnique.tmp
wmic process where (Name="WMIC.exe" AND CommandLine LIKE "%%%TIME%%%") get ParentProcessId /value | find "ParentProcessId" >%T%
for /f "usebackq tokens=2 delims==" %%a in ("%T%") do set /a ParentPID=%%a
setlocal EnableDelayedExpansion

if "%1"=="start" (

	echo  "Starting zookeeper ... "
	
   	if %ISJAVA%==YES (
		start "zookeeper" /b java  "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %JVMFLAGS% %ZOOMAIN% "%ZOOCFG%"
		echo STARTED
	) else (
		if defined JAVA_HOME (
	        if exist "%JAVA_HOME%"\bin\java.exe (
			start "zookeeper" /b "%JAVA_HOME%"\bin\java  "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %JVMFLAGS% %ZOOMAIN% "%ZOOCFG%"
			echo STARTED
			) else (
			echo JAVA_HOME is set incorrectly
		    )
		) else (
		    echo JAVA_HOME must be set
		)
	)
	wmic process where ^(ParentProcessId=%ParentPID% AND Name="java.exe"^) get ProcessId /value | find "ProcessId" >%T%
	for /f "usebackq tokens=2 delims==" %%a in ("%T%") do set /a PID=%%a	
	echo !PID!>zookeeper_server.pid
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
 
:NOCOMMAND
 
endlocal
