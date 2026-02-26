@echo off
REM Seat Booking System - Build and Run Script for Windows
REM This script compiles and runs the Java 17 Seat Booking System

echo 🏗️  Building Seat Booking System...

REM Create output directory
if not exist build mkdir build

REM Compile all Java files
javac -d build src\com\seatbooking\*.java src\com\seatbooking\model\*.java src\com\seatbooking\service\*.java src\com\seatbooking\ui\*.java

REM Check compilation result
if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation successful!
    echo 🚀 Starting Seat Booking System...
    echo.
    
    REM Run the application
    cd build
    java com.seatbooking.SeatBookingApplication
) else (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)