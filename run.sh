#!/bin/bash

# OWSB Run Script
# Runs the Omega Wholesale Management System

echo "=========================================="
echo "OWSB - Omega Wholesale Management System"
echo "Starting Application..."
echo "=========================================="

# Set variables
MAIN_CLASS="com.owsb.OWSB"
JAR_FILE="target/OWSB.jar"
CLASS_PATH="target/classes"

# Check if JAR file exists
if [ -f "$JAR_FILE" ]; then
    echo "Running from JAR file..."
    java -jar "$JAR_FILE"
elif [ -d "$CLASS_PATH" ]; then
    echo "Running from compiled classes..."
    java -cp "$CLASS_PATH" "$MAIN_CLASS"
else
    echo "Error: Application not built!"
    echo "Please run the build script first:"
    echo "  ./build.sh"
    echo ""
    echo "If build script doesn't exist, compile manually:"
    echo "  javac -d target/classes -cp target/classes src/main/java/com/owsb/*.java src/main/java/com/owsb/*/*.java src/main/java/com/owsb/*/*/*.java"
    echo "  java -cp target/classes $MAIN_CLASS"
    exit 1
fi

echo "Application terminated." 