@echo off
echo ==========================================
echo OWSB - Omega Wholesale Management System
echo Starting Application...
echo ==========================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    echo Download from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Check if we're in the right directory
if not exist "src\main\java\com\owsb\OWSB.java" (
    echo ERROR: OWSB.java not found
    echo Please make sure you're running this from the OWSB project directory
    pause
    exit /b 1
)

REM Try to run from JAR first
if exist "target\OWSB.jar" (
    echo Running from JAR file...
    java -jar target\OWSB.jar
) else (
    echo JAR file not found, running from compiled classes...
    
    REM Check if classes exist
    if not exist "target\classes\com\owsb\OWSB.class" (
        echo Compiled classes not found, building project...
        call build-windows.bat
    )
    
    REM Run from classes
    java -cp target\classes com.owsb.OWSB
)

if %errorlevel% neq 0 (
    echo.
    echo Application encountered an error.
    pause
) 