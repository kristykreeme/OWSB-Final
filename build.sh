#!/bin/bash

# OWSB Build Script
# Compiles the Omega Wholesale Management System

echo "=========================================="
echo "OWSB - Omega Wholesale Management System"
echo "Build Script"
echo "=========================================="

# Set variables
SRC_DIR="src/main/java"
RESOURCE_DIR="src/main/resources"
TARGET_DIR="target/classes"
MAIN_CLASS="com.owsb.OWSB"

# Create target directory if it doesn't exist
echo "Creating target directory..."
mkdir -p "$TARGET_DIR"

# Clean previous build
echo "Cleaning previous build..."
rm -rf "$TARGET_DIR"/*

# Copy resources to target directory
echo "Copying resources..."
if [ -d "$RESOURCE_DIR" ]; then
    cp -r "$RESOURCE_DIR"/* "$TARGET_DIR/"
    echo "Resources copied successfully."
else
    echo "Warning: Resources directory not found."
fi

# Compile Java source files
echo "Compiling Java source files..."
find "$SRC_DIR" -name "*.java" > sources.txt

if [ -s sources.txt ]; then
    javac -d "$TARGET_DIR" -cp "$TARGET_DIR" @sources.txt
    
    if [ $? -eq 0 ]; then
        echo "Compilation successful!"
        
        # Create manifest file
        echo "Creating manifest file..."
        mkdir -p "$TARGET_DIR/META-INF"
        cat > "$TARGET_DIR/META-INF/MANIFEST.MF" << EOF
Manifest-Version: 1.0
Main-Class: $MAIN_CLASS
Created-By: OWSB Build System

EOF
        
        # Create JAR file
        echo "Creating JAR file..."
        cd "$TARGET_DIR"
        jar cfm ../OWSB.jar META-INF/MANIFEST.MF .
        cd - > /dev/null
        
        if [ -f "target/OWSB.jar" ]; then
            echo "JAR file created successfully: target/OWSB.jar"
        fi
        
        echo ""
        echo "=========================================="
        echo "Build completed successfully!"
        echo "=========================================="
        echo "To run the application:"
        echo "  ./run.sh"
        echo "  OR"
        echo "  java -cp target/classes $MAIN_CLASS"
        echo "  OR"
        echo "  java -jar target/OWSB.jar"
        echo "=========================================="
        
    else
        echo "Compilation failed!"
        echo "Please check the error messages above."
        exit 1
    fi
else
    echo "No Java source files found!"
    exit 1
fi

# Clean up
rm -f sources.txt

echo "Build process completed." 