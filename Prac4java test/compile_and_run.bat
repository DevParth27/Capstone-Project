@echo off
echo Compiling and Running Barcode Reader Application...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven is not found in PATH. Please install Maven or use the manual compilation method.
    echo.
    echo Manual compilation:
    echo 1. Download required JAR files to lib/ folder
    echo 2. Run compile_manual.bat
    pause
    exit /b 1
)

echo Cleaning previous builds...
call mvn clean

echo Compiling the project...
call mvn compile

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed! Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Creating executable JAR with dependencies...
call mvn package

if %errorlevel% neq 0 (
    echo.
    echo Packaging failed! Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Running the application...
java -jar target\barcode-qr-reader-1.0.0.jar

pause