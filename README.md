# 🎯 Seat Booking System

A comprehensive Java 17 console-based seat booking system with clean OOP design, efficient algorithms, and an interactive terminal UI.

## 📋 System Overview

### Configuration
- **Total Seats**: 50 (40 FIXED + 10 FLOATER)
- **Organization**: 8 squads in 2 batches
- **Layout**: 5 rows × 10 columns matrix
- **Rotation**: Dynamic weekly batch rotation

### Business Rules

#### General Rules
1. ✅ One user can book only 1 seat per day
2. 🚫 No double booking of same seat for same date
3. 📅 No weekend bookings allowed

#### FIXED Seats (S01-S40)
- 🕐 Can book anytime
- 📆 Can book any working day (Mon-Fri)
- ⏰ Can book up to 14 days in advance

#### FLOATER Seats (S41-S50)
- 🕒 Can only book after 3:00 PM
- 📅 Can only book for tomorrow
- 🚫 Cannot book beyond tomorrow

#### Dynamic Rotation Logic
- **Even weeks**: Mon-Wed → Batch 1, Thu-Fri → Batch 2
- **Odd weeks**: Mon-Wed → Batch 2, Thu-Fri → Batch 1
- Uses `java.time.temporal.ChronoUnit.WEEKS` for calculation

## 🏗️ Architecture

```
/home/ishouriya/Desktop/Mani-11/
├── README.md                      # Comprehensive documentation
├── run.sh                        # Linux/macOS build script
├── run.bat                       # Windows batch build script  
├── run.ps1                       # Windows PowerShell build script
└── src/com/seatbooking/
    ├── SeatBookingApplication.java    # Main entry point
    ├── SeatBookingTest.java          # Automated test suite
    ├── model/                         # Data models
    │   ├── User.java                 # User entity
    │   ├── Seat.java                 # Seat entity  
    │   ├── Booking.java              # Booking entity
    │   ├── Squad.java               # Squad enumeration
    │   ├── Batch.java               # Batch enumeration
    │   └── SeatType.java            # Seat type enumeration
    ├── service/                      # Business logic
    │   ├── BookingService.java      # Core booking operations
    │   └── BookingException.java    # Custom exception handling
    └── ui/                          # User interface
        ├── TerminalUI.java          # Main UI controller
        └── ConsoleColors.java       # ANSI colors and utilities
```

### Design Principles
- **OOP**: Clean separation of models, service, and UI layers
- **Efficiency**: O(1) booking conflict checks using HashMap + HashSet
- **Validation**: Comprehensive business rule enforcement
- **Exception Handling**: Meaningful error messages

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Terminal with ANSI color support:
  - **Linux/macOS**: Most modern terminals (Terminal, iTerm2, etc.)
  - **Windows**: Command Prompt, PowerShell, or Windows Terminal

> **Cross-Platform Support**: The system works on Linux, macOS, and Windows. Use the appropriate build script for your platform.

### Option 1: Using Build Script (Recommended)

**Linux/macOS:**
```bash
./run.sh
```

**Windows Command Prompt:**
```cmd
run.bat
```

**Windows PowerShell:**
```powershell
.\run.ps1
```

### Option 2: Manual Compilation

**Linux/macOS:**
```bash
# Create build directory
mkdir -p build

# Compile all Java files
javac -d build src/com/seatbooking/*.java \
               src/com/seatbooking/model/*.java \
               src/com/seatbooking/service/*.java \
               src/com/seatbooking/ui/*.java

# Run the application
cd build
java com.seatbooking.SeatBookingApplication
```

**Windows:**
```cmd
# Create build directory
mkdir build

# Compile all Java files
javac -d build src\com\seatbooking\*.java src\com\seatbooking\model\*.java src\com\seatbooking\service\*.java src\com\seatbooking\ui\*.java

# Run the application
cd build
java com.seatbooking.SeatBookingApplication
```

### Option 3: Run Tests (Optional)
To verify the system works correctly before using the interactive UI:

**Linux/macOS:**
```bash
./run.sh
# When the menu appears, press Ctrl+C to exit
# Then run the test suite:
cd build
java com.seatbooking.SeatBookingTest
```

**Windows:**
```cmd
run.bat
REM When the menu appears, press Ctrl+C to exit
REM Then run the test suite:
cd build
java com.seatbooking.SeatBookingTest
```

## 🎮 Usage Guide

### Main Menu Options
1. 📅 **View Available Seats** - Check seat availability for any date
2. 💺 **Book Seat** - Reserve a seat for a user
3. ❌ **Cancel Booking** - Cancel an existing reservation
4. 👤 **View My Bookings** - See all bookings for a user
5. 🚪 **Exit** - Close the application

### Sample Data
The system comes pre-loaded with sample users:

| User ID | Name   | Squad    | Batch   |
|---------|--------|----------|---------|
| U01     | User 1 | Squad A1 | Batch 1 |
| U02     | User 2 | Squad B1 | Batch 1 |
| U03     | User 3 | Squad C1 | Batch 1 |
| U04     | User 4 | Squad D1 | Batch 1 |
| U05     | User 5 | Squad A2 | Batch 2 |
| U06     | User 6 | Squad B2 | Batch 2 |
| U07     | User 7 | Squad C2 | Batch 2 |
| U08     | User 8 | Squad D2 | Batch 2 |

### Seat Matrix Legend
```
🎭 SEAT LAYOUT (F=Fixed, L=Floater, X=Booked)

     1  2  3  4  5  6  7  8  9 10
   ┌─────────────────────────────────┐
1  │ F  F  F  F  F  F  F  F  F  F  │
2  │ F  F  F  F  F  F  F  F  F  F  │
3  │ F  F  F  F  F  F  F  F  F  F  │
4  │ F  F  F  F  F  F  F  F  F  F  │
5  │ L  L  L  L  L  L  L  L  L  L  │
   └─────────────────────────────────┘
```

- `F` = Fixed seat (S01-S40)
- `L` = Floater seat (S41-S50) 
- `X` = Booked seat

## 📖 Example Usage

### Booking a Fixed Seat
1. Select option 2 (Book Seat)
2. Enter User ID: `U01`
3. Enter Seat ID: `S15`
4. Enter Date: `2026-03-03` (Monday)
5. System validates batch rotation and availability

### Booking a Floater Seat
1. Ensure current time is after 3:00 PM
2. Select option 2 (Book Seat)
3. Enter User ID: `U05`
4. Enter Seat ID: `S45`
5. Enter tomorrow's date: `2026-02-27`

### Viewing Available Seats
1. Select option 1 (View Available Seats)
2. Enter Date: `2026-03-01`
3. View colored seat matrix and booking summary

## 🔧 Key Features

### Efficiency Optimizations
- **O(1) Conflict Detection**: Uses HashMap<LocalDate, Set<String>> for instant seat/user lookup
- **Minimal Memory Usage**: In-memory storage with efficient data structures
- **Smart Validation**: Early validation prevents unnecessary processing

### User Experience
- **ANSI Colors**: Green for available, red for booked seats
- **ASCII Art**: Clean box borders and visual separators
- **Emoji Icons**: Enhanced visual feedback
- **Input Validation**: Comprehensive error handling with helpful messages

### Error Handling Examples
- ❌ "User already has a booking for 2026-03-03"
- ❌ "Seat S15 is already booked for 2026-03-03"
- ❌ "Only Batch 1 can book for 2026-03-03"
- ❌ "Floater seats can only be booked after 3:00 PM"
- ❌ "Fixed seats can only be booked up to 14 days in advance"

## 🧪 Testing Scenarios

### Test Batch Rotation
```bash
# Week of 2026-03-02 (Even week)
# Monday (3/2): Batch 1 allowed
# Thursday (3/5): Batch 2 allowed

# Week of 2026-03-09 (Odd week)  
# Monday (3/9): Batch 2 allowed
# Thursday (3/12): Batch 1 allowed
```

### Test Seat Type Rules
```bash
# FIXED seat booking (any time)
User: U01, Seat: S15, Date: 2026-03-15

# FLOATER seat booking (after 3 PM, tomorrow only)
User: U05, Seat: S45, Date: tomorrow's date
```

### Test Validation Rules
```bash
# Try double booking same seat
# Try booking multiple seats for same user on same date
# Try booking outside allowed batch rotation
# Try weekend booking
# Try booking too far in advance
```

## 🏆 Production Features

### Code Quality
- ✅ Clean OOP design with proper encapsulation
- ✅ Comprehensive JavaDoc documentation
- ✅ Consistent naming conventions
- ✅ Exception handling with meaningful messages
- ✅ Input validation and sanitization

### Performance
- ✅ O(1) booking conflict checks
- ✅ Efficient HashMap/HashSet usage
- ✅ Minimal memory footprint
- ✅ No unnecessary iterations

### Maintainability
- ✅ Modular architecture
- ✅ Separation of concerns
- ✅ Extensible design
- ✅ Well-commented code

## 📞 Support

This is a complete, production-ready seat booking system demonstrating enterprise-level Java development practices with clean code, efficient algorithms, and excellent user experience.

---
**Built with ❤️ using Java 17 and best practices**