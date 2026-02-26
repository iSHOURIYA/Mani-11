#!/bin/bash

# Seat Booking System - Build and Run Script
# This script compiles and runs the Java 17 Seat Booking System

echo "🏗️  Building Seat Booking System..."

# Create output directory
mkdir -p build

# Compile all Java files
javac -d build src/com/seatbooking/*.java src/com/seatbooking/model/*.java src/com/seatbooking/service/*.java src/com/seatbooking/ui/*.java

# Check compilation result
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🚀 Starting Seat Booking System..."
    echo ""
    
    # Run the application
    cd build
    java com.seatbooking.SeatBookingApplication
else
    echo "❌ Compilation failed!"
    exit 1
fi