@echo off
echo ==========================================
echo OWSB - Omega Wholesale Management System
echo Build Script for Windows
echo ==========================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java compiler (javac) not found
    echo Please install Java JDK (not just JRE)
    pause
    exit /b 1
)

echo Creating target directory...
if not exist "target" mkdir target
if not exist "target\classes" mkdir target\classes

echo Cleaning previous build...
if exist "target\classes\*" del /Q /S target\classes\* >nul 2>&1

echo Copying resources...
if exist "src\main\resources" (
    xcopy /E /I /Y src\main\resources\* target\classes\ >nul
    echo Resources copied successfully.
) else (
    echo No resources directory found, skipping...
)

echo Compiling Java source files...
javac -d target\classes -cp target\classes src\main\java\com\owsb\*.java src\main\java\com\owsb\model\*.java src\main\java\com\owsb\view\*.java src\main\java\com\owsb\view\panels\*.java

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!

echo Creating manifest file...
if not exist "target\META-INF" mkdir target\META-INF
echo Manifest-Version: 1.0 > target\META-INF\MANIFEST.MF
echo Main-Class: com.owsb.OWSB >> target\META-INF\MANIFEST.MF
echo. >> target\META-INF\MANIFEST.MF

echo Creating JAR file...
jar cfm target\OWSB.jar target\META-INF\MANIFEST.MF -C target\classes .

if %errorlevel% neq 0 (
    echo WARNING: JAR creation failed, but you can still run from classes
) else (
    echo JAR file created successfully: target\OWSB.jar
)

echo.
echo ==========================================
echo Build completed successfully!
echo ==========================================
echo To run the application:
echo   run-windows.bat
echo   OR
echo   java -cp target\classes com.owsb.OWSB
echo   OR
echo   java -jar target\OWSB.jar
echo ========================================== 