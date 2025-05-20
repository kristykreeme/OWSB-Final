#!/bin/bash

# Create data directory if it doesn't exist
mkdir -p data

# Compile all Java files
javac -d . *.java model/*.java view/*.java view/panels/*.java

echo "Compilation complete. Run with: java OWSB" 