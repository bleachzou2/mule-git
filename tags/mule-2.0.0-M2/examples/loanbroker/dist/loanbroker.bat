@echo off
setlocal
REM There is no need to call this if you set the MULE_HOME in your environment properties
if "%MULE_HOME%" == "" SET MULE_HOME=..\..
if "%MULE_BASE%" == "" SET MULE_BASE=%MULE_HOME%

REM This extends the classpath to include the configuration directory
REM Any changes to the files in .\conf will take precedence over those deployed to %MULE_HOME%\lib\user
SET MULE_LIB=.\conf

REM 3rd party libraries for LoanBroker BPM
SET BSH=bsh-1.3.0.jar
SET DERBYDB=derby-10.3.1.4.jar

:testbsh
if exist "%MULE_BASE%\lib\user\%BSH%" goto :testderbydb
if exist "%MULE_HOME%\lib\user\%BSH%" goto :testderbydb
goto :missinglibs

:testderbydb
if exist "%MULE_BASE%\lib\user\%DERBYDB%" goto :mule
if exist "%MULE_HOME%\lib\user\%DERBYDB%" goto :mule

:missinglibs
echo This example requires additional libraries which need to be downloaded by the build script.  Please follow the instructions in the README.txt file.
goto :eof

:mule
ECHO The Loan Broker example is available in two flavors:
ECHO   1. Loan Broker ESN
ECHO   2. Loan Broker BPM
SET /P Choice=Select the one you wish to execute and press Enter...

IF '%Choice%'=='1' call "%MULE_BASE%\bin\mule.bat" -main org.mule.examples.loanbroker.esn.LoanBrokerApp
IF '%Choice%'=='2' call "%MULE_BASE%\bin\mule.bat" -main org.mule.examples.loanbroker.bpm.LoanBrokerApp

