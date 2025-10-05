@echo off
echo ===============================================
echo FULL BARCODE READER WITH ZXING LIBRARIES
echo ===============================================
echo.

REM Check if lib directory exists
if not exist "lib" (
    echo ❌ lib directory not found!
    echo.
    echo To enable full functionality, run:
    echo   powershell -ExecutionPolicy Bypass .\download_libs.ps1
    echo.
    echo This will download the required ZXing JAR files.
    pause
    exit /b 1
)

REM Check for key JAR files
set MISSING_JARS=0

if not exist "lib\core-3.5.1.jar" (
    echo ❌ Missing: core-3.5.1.jar
    set MISSING_JARS=1
)

if not exist "lib\javase-3.5.1.jar" (
    echo ❌ Missing: javase-3.5.1.jar
    set MISSING_JARS=1
)

if %MISSING_JARS%==1 (
    echo.
    echo ⚠️  Required JAR files are missing!
    echo To download them automatically, run:
    echo   powershell -ExecutionPolicy Bypass .\download_libs.ps1
    echo.
    pause
    exit /b 1
)

echo ✅ ZXing libraries found!
echo.

echo Compiling with ZXing support...
javac -cp "lib\*" -d target\classes -encoding UTF-8 src\main\java\com\barcodereader\FullBarcodeReaderApp.java

if %errorlevel% neq 0 (
    echo.
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.
echo 🚀 Starting FULL VERSION with actual barcode reading...
echo   ✅ QR Code generation and reading
echo   ✅ Multiple barcode format support  
echo   ✅ Real content decoding
echo.

java -cp "target\classes;lib\*" com.barcodereader.FullBarcodeReaderApp

echo.
echo Application closed.
pause