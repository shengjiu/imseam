@echo off

if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Start/Stop Script for the RAPTOR Server
rem
rem Environment Variable Prerequisites
rem
rem   Do not set the variables in this script. Instead put them into a script
rem   setenv.bat in RAPTOR_BASE/bin to keep your customizations separate.
rem
rem   RAPTOR_HOME   May point at your RAPTOR "build" directory.
rem
rem
rem   RAPTOR_OPTS   (Optional) Java runtime options used when the "start",
rem                   "run" or "debug" command is executed.
rem                   Include here and not in JAVA_OPTS all options, that should
rem                   only be used by RAPTOR itself, not by the stop process,
rem                   the version command etc.
rem                   Examples are heap size, GC logging, JMX ports etc.
rem
rem   RAPTOR_TMPDIR (Optional) Directory path location of temporary directory
rem                   the JVM should use (java.io.tmpdir).  Defaults to
rem                   %RAPTOR_BASE%\temp.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem                   Required to run the with the "debug" argument.
rem
rem   JRE_HOME        Must point at your Java Runtime installation.
rem                   Defaults to JAVA_HOME if empty. If JRE_HOME and JAVA_HOME
rem                   are both set, JRE_HOME is used.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when any command
rem                   is executed.
rem                   Include here and not in RAPTOR_OPTS all options, that
rem                   should be used by RAPTOR and also by the stop process,
rem                   the version command etc.
rem                   Most options should go into RAPTOR_OPTS.
rem
rem   JAVA_ENDORSED_DIRS (Optional) Lists of of semi-colon separated directories
rem                   containing some jars in order to allow replacement of APIs
rem                   created outside of the JCP (i.e. DOM and SAX from W3C).
rem                   It can also be used to update the XML parser implementation.
rem                   Defaults to $RAPTOR_HOME/endorsed.
rem
rem
rem   LOGGING_CONFIG  (Optional) Override RAPTOR's logging config file
rem                   Example (all one line)
rem                   set LOGGING_CONFIG="-Djava.util.logging.config.file=%RAPTOR_BASE%\conf\logging.properties"
rem
rem   LOGGING_MANAGER (Optional) Override RAPTOR's logging manager
rem                   Example (all one line)
rem                   set LOGGING_MANAGER="-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager"
rem
rem   TITLE           (Optional) Specify the title of RAPTOR window. The default
rem                   TITLE is RAPTOR if it's not specified.
rem                   Example (all one line)
rem                   set TITLE=RAPTOR.Cluster#1.Server#1 [%DATE% %TIME%]
rem
rem
rem
rem $Id: RAPTOR.bat 1344732 2012-05-31 14:08:02Z kkolinko $
rem ---------------------------------------------------------------------------

rem Suppress Terminate batch job on CTRL+C
if not ""%1"" == ""run"" goto mainEntry
if "%TEMP%" == "" goto mainEntry
if exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.run"
if not exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.Y"
call "%~f0" %* <"%TEMP%\%~nx0.Y"
rem Use provided errorlevel
set RETVAL=%ERRORLEVEL%
del /Q "%TEMP%\%~nx0.Y" >NUL 2>&1
exit /B %RETVAL%
:mainEntry
del /Q "%TEMP%\%~nx0.run" >NUL 2>&1

rem Guess RAPTOR_HOME if not defined
set "CURRENT_DIR=%cd%"
if not "%RAPTOR_HOME%" == "" goto gotHome
set "RAPTOR_HOME=%CURRENT_DIR%"
if exist "%RAPTOR_HOME%\bin\RAPTOR.bat" goto okHome
cd ..
set "RAPTOR_HOME=%cd%"
cd "%CURRENT_DIR%"
:gotHome

if exist "%RAPTOR_HOME%\bin\RAPTOR.bat" goto okHome
echo The RAPTOR_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome

rem Copy RAPTOR_BASE from RAPTOR_HOME if not defined
if not "%RAPTOR_BASE%" == "" goto gotBase
set "RAPTOR_BASE=%RAPTOR_HOME%"
:gotBase

rem Ensure that any user defined CLASSPATH variables are not used on startup,
rem but allow them to be specified in setenv.bat, in rare case when it is needed.
set CLASSPATH=

rem Get standard environment variables
if not exist "%RAPTOR_BASE%\bin\setenv.bat" goto checkSetenvHome
call "%RAPTOR_BASE%\bin\setenv.bat"
goto setenvDone
:checkSetenvHome
if exist "%RAPTOR_HOME%\bin\setenv.bat" call "%RAPTOR_HOME%\bin\setenv.bat"
:setenvDone

rem Get standard Java environment variables
if exist "%RAPTOR_HOME%\bin\setclasspath.bat" goto okSetclasspath
echo Cannot find "%RAPTOR_HOME%\bin\setclasspath.bat"
echo This file is needed to run this program
goto end
:okSetclasspath
call "%RAPTOR_HOME%\bin\setclasspath.bat" %1
if errorlevel 1 goto end

rem Add on extra jar file to CLASSPATH
rem Note that there are no quotes as we do not want to introduce random
rem quotes into the CLASSPATH
if "%CLASSPATH%" == "" goto emptyClasspath
set "CLASSPATH=%CLASSPATH%;"
:emptyClasspath
set "CLASSPATH=%CLASSPATH%%RAPTOR_HOME%\lib\raptor.jar"

if not "%RAPTOR_TMPDIR%" == "" goto gotTmpdir
set "RAPTOR_TMPDIR=%RAPTOR_BASE%\temp"
:gotTmpdir


set LOGGING_CONFIG=-Djava.util.logging.config.file="%RAPTOR_BASE%\conf\logging.properties"


rem ----- Execute The Requested Command ---------------------------------------

echo Using RAPTOR_BASE:   "%RAPTOR_BASE%"
echo Using RAPTOR_HOME:   "%RAPTOR_HOME%"
echo Using RAPTOR_TMPDIR: "%RAPTOR_TMPDIR%"
if ""%1"" == ""debug"" goto use_jdk
echo Using JRE_HOME:        "%JRE_HOME%"
goto java_dir_displayed
:use_jdk
echo Using JAVA_HOME:       "%JAVA_HOME%"
:java_dir_displayed
echo Using CLASSPATH:       "%CLASSPATH%"

set _EXECJAVA=%_RUNJAVA%
set MAINCLASS=com.imseam.raptor.startup.Raptor
set ACTION=start
set SECURITY_POLICY_FILE=
set DEBUG_OPTS=

if ""%1"" == ""run"" goto doRun
if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop

echo Usage:  RAPTOR ( commands ... )
echo commands:
echo   start             Start RAPTOR in a separate window
echo   stop              Stop RAPTOR
echo   version           What version of RAPTOR are you running?
goto end

:doStart
shift
if not "%OS%" == "Windows_NT" goto noTitle
if "%TITLE%" == "" set TITLE=RAPTOR
rem set _EXECJAVA=start "%TITLE%" %_RUNJAVA%
rem set _EXECJAVA=%_RUNJAVA%
goto gotTitle
:noTitle
set _EXECJAVA=start %_RUNJAVA%
:gotTitle
goto execCmd

:doStop
shift
set ACTION=stop
set RAPTOR_OPTS=
goto execCmd

:execCmd
rem Get remaining unshifted command line arguments and save them in the
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

rem Execute Java with the applicable properties
echo %_EXECJAVA%
echo %_EXECJAVA% %JAVA_OPTS% %RAPTOR_OPTS% -classpath "%CLASSPATH%" -DRAPTOR.base="%RAPTOR_BASE%" -DRAPTOR.home="%RAPTOR_HOME%" -Djava.io.tmpdir="%RAPTOR_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
%_EXECJAVA% %JAVA_OPTS% %RAPTOR_OPTS% -classpath "%CLASSPATH%" -DRAPTOR.base="%RAPTOR_BASE%" -DRAPTOR.home="%RAPTOR_HOME%" -Djava.io.tmpdir="%RAPTOR_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end

:end
