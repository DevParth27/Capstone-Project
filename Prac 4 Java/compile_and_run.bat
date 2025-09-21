@echo off
echo Compiling Java files...

set CLASSPATH=.;lib\*
set SRC_DIR=src
set OUT_DIR=bin

if not exist %OUT_DIR% mkdir %OUT_DIR%

javac -d %OUT_DIR% -cp %CLASSPATH% %SRC_DIR%\com\barcodereader\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo Running application...

java -cp %OUT_DIR%;%CLASSPATH% com.barcodereader.BarcodeReaderApp

pause