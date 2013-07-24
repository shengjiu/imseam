@echo off

if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Start script for the Raptor Server
rem
rem $Id: startup.bat
rem ---------------------------------------------------------------------------

rem Guess RAPTOR_HOME if not defined
set "CURRENT_DIR=%cd%"
if not "%RAPTOR_HOME%" == "" goto gotHome
set "RAPTOR_HOME=%CURRENT_DIR%"
if exist "%RAPTOR_HOME%\bin\raptor.bat" goto okHome
cd ..
set "RAPTOR_HOME=%cd%"
cd "%CURRENT_DIR%"
:gotHome
if exist "%RAPTOR_HOME%\bin\raptor.bat" goto okHome
echo The RAPTOR_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome

set "EXECUTABLE=%RAPTOR_HOME%\bin\raptor.bat"

rem Check that target executable exists
if exist "%EXECUTABLE%" goto okExec
echo Cannot find "%EXECUTABLE%"
echo This file is needed to run this program
goto end
:okExec

rem Get remaining unshifted command line arguments and save them in the
set CMD_LINE_ARGS=-config ../config/engine.config 
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1

:doneSetArgs

echo "%EXECUTABLE%" start %CMD_LINE_ARGS%

call "%EXECUTABLE%" start %CMD_LINE_ARGS%

:end
