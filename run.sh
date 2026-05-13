#!/bin/bash
# ============================================================
#  run.sh — Compile and run Library Management System
#  Usage  : chmod +x run.sh && ./run.sh
#  Requires: Java 17+, MySQL running
# ============================================================

JAR="lib/mysql-connector-j-8.0.33.jar"

echo "[*] Cleaning previous build..."
rm -rf out
mkdir -p out

echo "[*] Compiling Java sources..."
javac -cp "$JAR" -d out \
  src/database/*.java \
  src/exception/*.java \
  src/model/*.java \
  src/util/*.java \
  src/service/*.java \
  src/main/*.java

if [ $? -ne 0 ]; then
  echo "[ERROR] Compilation failed. Check errors above."
  exit 1
fi

echo "[*] Compilation successful. Starting application..."
echo ""
java -cp "out:$JAR" main.Main
