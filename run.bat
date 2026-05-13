@echo off
REM ============================================================
REM  run.bat — Compile and run Library Management System
REM  Usage: Double-click or run from project root in CMD
REM  Requirement: Java 17+ installed, MySQL running
REM ============================================================

set JAR=lib\mysql-connector-j-8.0.33.jar

echo [*] Cleaning previous build...
if exist out rmdir /s /q out
mkdir out

echo [*] Compiling Java sources...
javac -cp "%JAR%" -d out ^
  src\database\*.java ^
  src\exception\*.java ^
  src\model\*.java ^
  src\util\*.java ^
  src\service\*.java ^
  src\main\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Check errors above.
    pause
    exit /b 1
)

echo [*] Compilation successful. Starting application...
echo.
java -cp "out;%JAR%" main.Main

pause
