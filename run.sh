#!/bin/bash

# Create data directory if it doesn't exist
mkdir -p data

# Compile all Java files
javac -d . *.java model/*.java view/*.java view/panels/*.java

# Force regenerate sample data for testing
echo "Generating sample data..."
java GenerateSampleData

# Run the application
java OWSB

echo "Application closed." 