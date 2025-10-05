@echo off
echo Starting Enhanced Barcode & QR Code Reader Application...
echo.

REM Compile the application
echo Compiling enhanced version with QR code functionality...
javac -d target\classes -encoding UTF-8 src\main\java\com\barcodereader\WorkingBarcodeReaderApp.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed! Please check the error messages above.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Starting enhanced application with QR code reader...
java -cp target\classes com.barcodereader.WorkingBarcodeReaderApp

pause