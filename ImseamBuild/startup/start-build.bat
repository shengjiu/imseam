@echo off
REM Build, Test, and Deploy Imseam
:Init
SETLOCAL ENABLEEXTENSIONS
set BuildRoot=c:\working\build
echo %BuildRoot%
pushd %BuildRoot%

REM Create a variable to store the current date. Make sure the WinNT short date
REM  format is yyyy-mm-dd (all numeric; the '-' could be something else like
REM  '/'). This way, the directories that are created using the variable can be
REM  properly sorted.
FOR /F "Tokens=2" %%d IN ('DATE /T') DO SET CDate=%%d
set Label=%2
if "%Label%"=="" (set Label=%CDate%a)

set CurrentBuildDir=%BuildRoot%\%Label%

echo %CurrentBuildDir%
set BuildLog=%CurrentBuildDir%\Build.log
echo %BuildLog%

REM Create the archive folder
rd /S /Q %CurrentBuildDir%
mkdir %CurrentBuildDir%
rem cd %CurrentBuildDir%
set ImseamBuildRoot=%CurrentBuildDir%
echo.
echo Build, Test, and Deploy Imseam.
echo Build, Test, and Deploy Imseam. > %BuildLog%

REM Delete the local source
:Clean
echo.
echo Delete the local source...
echo. >> %BuildLog%
echo Delete the local source... >> %BuildLog%
rd /S /Q Imseam >> %BuildLog%

REM Get the source from subversion
:Checkout
echo.
echo Get the source from subversion...
echo. >> %BuildLog%
echo Get the source from subversion... >> %BuildLog%
call ant -logger org.apache.tools.ant.listener.MailLogger -buildfile ../ant-subversion.xml >> %BuildLog%


REM Insert build, version, and classpath strings
:Version
REM echo.
REM echo Insert the version info (0.9.0, %Label%)...
REM echo. >> %BuildLog%
REM echo Insert the version info (0.9.0, %Label%)... >> %BuildLog%
REM CScript Imseam\build\SetVersionInfo.js %Label% >> %BuildLog%
REM CScript Imseam\build\SetClassPath.js %Label% >> %BuildLog%

REM Build the Java classes and jars using Ant
:Ant
echo.
echo Build and test the Java classes and jars using Ant...
echo. >> %BuildLog%
echo Build and test the Java classes and jars using Ant... >> %BuildLog%
cd %ImseamBuildRoot%
call ant -logger org.apache.tools.ant.listener.MailLogger -buildfile ImseamBuild\ImseamBuild.xml build >> %BuildLog%
rem cd ..
rem copy /v /y Imseam\Target\TEST-*.txt %CurrentBuildDir% >> %BuildLog%

REM Finished
:Finish
echo.
echo Build, Test, and Deployment complete.
echo. >> %BuildLog%
echo Build, Test, and Deployment complete. >> %BuildLog%
popd
endlocal
