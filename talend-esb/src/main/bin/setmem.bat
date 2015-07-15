@echo off
rem
rem
rem    Licensed to the Apache Software Foundation (ASF) under one or more
rem    contributor license agreements.  See the NOTICE file distributed with
rem    this work for additional information regarding copyright ownership.
rem    The ASF licenses this file to You under the Apache License, Version 2.0
rem    (the "License"); you may not use this file except in compliance with
rem    the License.  You may obtain a copy of the License at
rem
rem       http://www.apache.org/licenses/LICENSE-2.0
rem
rem    Unless required by applicable law or agreed to in writing, software
rem    distributed under the License is distributed on an "AS IS" BASIS,
rem    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem    See the License for the specific language governing permissions and
rem    limitations under the License.
rem

rem
rem handle specific scripts; the SCRIPT_NAME is exactly the name of the Karaf
rem script; for example karaf.bat, start.bat, stop.bat, admin.bat, client.bat, ...
rem
rem if "%KARAF_SCRIPT%" == "SCRIPT_NAME" (
rem   Actions go here...
rem )

rem Find java version we use

set java_version_file=%time::=%
set /a java_version_file=java_version_file
set java_version_file=__JVER%java_version_file%%random%.tmp
"%JAVA%" -version 2> %java_version_file%
rem Determine whether we use a 64-bit java version
for /f %%G IN ('findstr "64-Bit" %java_version_file%') DO set sixtyfour=true
rem Check minor java version number in string like 'java version "1.7.0_80"'
for /f "tokens=3" %%g in ('findstr /i /c:"java version" %java_version_file%') do (
    set JAVAVER=%%g
)
set JAVAVER=%JAVAVER:"=%
set JAVAVER_MINOR=7
for /f "delims=. tokens=2" %%v in ("%JAVAVER%") do (
    set JAVAVER_MINOR=%%v
)
del %java_version_file%

rem Check/Set up some easily accessible MIN/MAX params for JVM mem usage

if "%JAVA_MIN_MEM%" == "" (
	if "%sixtyfour%" == "" (
		set JAVA_MIN_MEM=128M 
	) else (
		set JAVA_MIN_MEM=256M
	)
)

if "%JAVA_MAX_MEM%" == "" (
	if "%sixtyfour%" == "" (
		set JAVA_MAX_MEM=512M 
	) else ( 
		set JAVA_MAX_MEM=1024M
	)
)

if %JAVAVER_MINOR% LSS 8 (
    if "%JAVA_PERM_MEM%" == "" (
    	if "%sixtyfour%" == "" (
    		set JAVA_PERM_MEM=64M 
    	) else (
    		set JAVA_PERM_MEM=128M
    	)
    )
    
    if "%JAVA_MAX_PERM_MEM%" == "" (
    	if "%sixtyfour%" == "" (
    		set JAVA_MAX_PERM_MEM=384M
    	) else (
    		set JAVA_MAX_PERM_MEM=640M
    	)
    )
)
