# Seat Booking System - PowerShell Build and Run Script
# This script compiles and runs the Java 17 Seat Booking System

Write-Host "🏗️  Building Seat Booking System..." -ForegroundColor Cyan

# Create output directory
if (!(Test-Path "build")) {
    New-Item -ItemType Directory -Path "build" | Out-Null
}

# Compile all Java files
Write-Host "Compiling Java files..." -ForegroundColor Yellow
javac -d build src\com\seatbooking\*.java src\com\seatbooking\model\*.java src\com\seatbooking\service\*.java src\com\seatbooking\ui\*.java

# Check compilation result
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation successful!" -ForegroundColor Green
    Write-Host "🚀 Starting Seat Booking System..." -ForegroundColor Cyan
    Write-Host ""
    
    # Run the application
    Push-Location build
    java com.seatbooking.SeatBookingApplication
    Pop-Location
} else {
    Write-Host "❌ Compilation failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}