@echo off
setlocal
set JAVA_HOME=C:\Users\andre\.jdks\corretto-17.0.11
set CLASSPATH=C:\Users\andre\IdeaProjects\EcosystemSimulatorProject\target\classes
%JAVA_HOME%\bin\java -classpath %CLASSPATH% ecosystem.App
endlocal
pause
