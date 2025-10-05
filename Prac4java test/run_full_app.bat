@echo off
echo =========================================
echo FULL BARCODE & QR CODE READER LAUNCHER
echo =========================================
echo.

REM Check if Maven is available
echo Checking for Maven...
where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ Maven found! Using Maven for full functionality...
    echo.
    
    echo Downloading dependencies and compiling...
    call mvn clean compile
    
    if %errorlevel% equ 0 (
        echo.
        echo ✅ Compilation successful!
        echo 🚀 Starting FULL VERSION with ZXing library...
        echo.
        call mvn exec:java
        goto :end
    ) else (
        echo.
        echo ❌ Maven compilation failed. Trying manual compilation...
        echo.
    )
) else (
    echo ⚠️ Maven not found. Trying manual compilation...
    echo.
)

REM Manual compilation fallback
echo Compiling with javac (demo mode)...
javac -d target\classes -encoding UTF-8 src\main\java\com\barcodereader\FullBarcodeReaderApp.java

if %errorlevel% neq 0 (
    echo.
    echo ❌ Manual compilation failed!
    echo.
    echo TROUBLESHOOTING:
    echo 1. Make sure Java JDK is installed
    echo 2. Check that JAVA_HOME is set correctly
    echo 3. Verify source files are present
    pause
    exit /b 1
)

echo.
echo ✅ Manual compilation successful!
echo 🎯 Starting DEMO VERSION (pattern analysis only)...
echo.
echo NOTE: For full barcode reading functionality:
echo • Install Maven, or
echo • Download ZXing JAR files to lib/ folder
echo.

java -cp target\classes com.barcodereader.FullBarcodeReaderApp

:end
echo.
echo Application closed.
pause