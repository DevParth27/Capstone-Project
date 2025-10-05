@echo off
echo Manual Compilation (without Maven)
echo ==================================
echo.

REM Check if lib folder exists
if not exist "lib" (
    echo Creating lib folder...
    mkdir lib
)

REM Check for required JAR files
set MISSING_JARS=0

if not exist "lib\core-3.5.1.jar" (
    echo Missing: core-3.5.1.jar
    set MISSING_JARS=1
)

if not exist "lib\javase-3.5.1.jar" (
    echo Missing: javase-3.5.1.jar
    set MISSING_JARS=1
)

if not exist "lib\webcam-capture-0.3.12.jar" (
    echo Missing: webcam-capture-0.3.12.jar
    set MISSING_JARS=1
)

if not exist "lib\slf4j-api-1.7.36.jar" (
    echo Missing: slf4j-api-1.7.36.jar
    set MISSING_JARS=1
)

if not exist "lib\slf4j-simple-1.7.36.jar" (
    echo Missing: slf4j-simple-1.7.36.jar
    set MISSING_JARS=1
)

if not exist "lib\bridj-0.7.0.jar" (
    echo Missing: bridj-0.7.0.jar (required for webcam-capture)
    set MISSING_JARS=1
)

if %MISSING_JARS%==1 (
    echo.
    echo Please download the missing JAR files and place them in the lib/ folder.
    echo You can download them from:
    echo - ZXing: https://github.com/zxing/zxing/releases
    echo - Webcam Capture: https://github.com/sarxos/webcam-capture/releases
    echo - SLF4J: https://www.slf4j.org/download.html
    echo.
    pause
    exit /b 1
)

echo All required JAR files found!
echo.

REM Create target directories
if not exist "target\classes" mkdir target\classes

echo Compiling Java files...
javac -cp "lib\*" -d target\classes src\main\java\com\barcodereader\*.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed! Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo Running the application...
java -cp "target\classes;lib\*" com.barcodereader.BarcodeReaderApp

pause