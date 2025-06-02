# 🖥️ Complete Terminal Guide for OWSB

## 📋 **Table of Contents**
1. [Prerequisites](#prerequisites)
2. [Getting the Project](#getting-the-project)
3. [Terminal Setup](#terminal-setup)
4. [Running the Application](#running-the-application)
5. [Troubleshooting](#troubleshooting)
6. [Platform-Specific Instructions](#platform-specific-instructions)

---

## 🔧 **Prerequisites**

### **1. Java Development Kit (JDK)**
Your laptop needs Java JDK 8 or higher installed.

#### **Check if Java is Already Installed:**
```bash
java -version
javac -version
```

If you see version information, you're good to go! If not, install Java:

#### **Installing Java:**

**macOS:**
```bash
# Option 1: Using Homebrew (recommended)
brew install openjdk@11

# Option 2: Download from Oracle
# Visit: https://www.oracle.com/java/technologies/downloads/
```

**Windows:**
1. Download JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Run the installer
3. Add Java to PATH (installer usually does this)

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

**Linux (CentOS/RHEL):**
```bash
sudo yum install java-11-openjdk-devel
```

---

## 📁 **Getting the Project**

### **Method 1: Download ZIP File**
1. Download the OWSB project ZIP file
2. Extract it to your desired location
3. Note the extraction path (e.g., `/Users/yourname/Downloads/owsb-project`)

### **Method 2: Copy from USB/Drive**
1. Copy the project folder to your laptop
2. Note the location where you copied it

### **Method 3: Git Clone (if available)**
```bash
git clone <repository-url>
cd omega-wholesale-management
```

---

## 🖥️ **Terminal Setup**

### **Opening Terminal:**

**macOS:**
- Press `Cmd + Space`, type "Terminal", press Enter
- Or: Applications → Utilities → Terminal

**Windows:**
- Press `Win + R`, type "cmd", press Enter
- Or: Start Menu → Command Prompt
- Or: Use PowerShell (recommended)

**Linux:**
- Press `Ctrl + Alt + T`
- Or: Applications → Terminal

### **Navigate to Project Directory:**
```bash
# Replace with your actual project path
cd /path/to/your/owsb-project

# Examples:
# macOS: cd /Users/yourname/Downloads/owsb-project
# Windows: cd C:\Users\yourname\Downloads\owsb-project
# Linux: cd /home/yourname/Downloads/owsb-project
```

### **Verify You're in the Right Directory:**
```bash
# List files - you should see these files:
ls
# or on Windows:
dir

# Expected files:
# build.sh, run.sh, pom.xml, README.md, src/, target/, data/, docs/
```

---

## 🚀 **Running the Application**

### **Method 1: Using Run Script (Easiest)**

**For macOS/Linux:**
```bash
# Make script executable (first time only)
chmod +x run.sh

# Run the application
./run.sh
```

**For Windows (PowerShell):**
```powershell
# If you have bash/WSL
bash run.sh

# Or run Java directly (see Method 3)
```

### **Method 2: Build and Run**

**For macOS/Linux:**
```bash
# Make scripts executable (first time only)
chmod +x build.sh run.sh

# Build the project
./build.sh

# Run the application
./run.sh
```

### **Method 3: Direct Java Execution**

**If JAR file exists:**
```bash
java -jar target/OWSB.jar
```

**If only compiled classes exist:**
```bash
java -cp target/classes com.owsb.OWSB
```

**Manual compilation (if needed):**
```bash
# Create target directory
mkdir -p target/classes

# Copy resources
cp -r src/main/resources/* target/classes/

# Compile Java files
find src/main/java -name "*.java" > sources.txt
javac -d target/classes -cp target/classes @sources.txt

# Run the application
java -cp target/classes com.owsb.OWSB

# Clean up
rm sources.txt
```

---

## 🔐 **Login and Testing**

### **1. Application Startup**
When you run the application, you should see:
```
==========================================
OWSB - Omega Wholesale Management System
Starting Application...
==========================================
Creating LoginScreen...
LoginScreen frame setup complete
```

### **2. Login Screen**
A GUI window will appear with the OWSB login screen.

### **3. Test Login Credentials**

**Admin Access (Full Features):**
- Username: `admin`
- Password: `admin`

**Department-Specific Testing:**
- **Sales**: `sales` / `sales`
- **Purchase**: `purchase` / `purchase`
- **Finance**: `finance` / `finance`
- **Inventory**: `inventory` / `inventory`

**Staff Accounts:**
- **John Smith**: `john` / `pass123`
- **Maria Garcia**: `maria` / `pass123`
- **Sarah Johnson**: `sarah` / `pass123`
- **David Lee**: `david` / `pass123`

### **4. Successful Login**
After login, you'll see:
```
Authentication successful for user: [Username]
Login successful, opening dashboard...
Dashboard created, making it visible...
```

---

## 🛠️ **Troubleshooting**

### **Common Issues and Solutions**

#### **1. "java: command not found"**
**Problem:** Java is not installed or not in PATH
**Solution:**
```bash
# Check if Java is installed
which java
# If nothing appears, install Java (see Prerequisites section)

# On macOS, you might need to set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home)
```

#### **2. "Permission denied" on scripts**
**Problem:** Script files are not executable
**Solution:**
```bash
chmod +x build.sh run.sh
```

#### **3. "No such file or directory"**
**Problem:** You're not in the correct directory
**Solution:**
```bash
# Check current directory
pwd

# List files to verify
ls

# Navigate to correct directory
cd /path/to/your/owsb-project
```

#### **4. "ClassNotFoundException"**
**Problem:** Java classes not compiled or not in classpath
**Solution:**
```bash
# Rebuild the project
./build.sh

# Or compile manually
javac -d target/classes -cp target/classes src/main/java/com/owsb/*.java src/main/java/com/owsb/*/*.java src/main/java/com/owsb/*/*/*.java
```

#### **5. GUI doesn't appear**
**Problem:** Running in headless mode or display issues
**Solution:**
```bash
# Check if you're in a graphical environment
echo $DISPLAY

# On macOS, make sure you're not using SSH without X11 forwarding
# On Linux, install GUI packages if missing
sudo apt install default-jdk openjfx
```

#### **6. "Data files not found"**
**Problem:** Application can't find data files
**Solution:**
```bash
# Ensure you're running from project root
pwd

# Check if data directory exists
ls -la data/

# If missing, copy from resources
cp -r src/main/resources/data/* data/
```

---

## 💻 **Platform-Specific Instructions**

### **macOS Terminal**

**Complete Setup:**
```bash
# 1. Open Terminal (Cmd + Space, type "Terminal")

# 2. Navigate to project
cd /Users/$(whoami)/Downloads/owsb-project

# 3. Make scripts executable
chmod +x build.sh run.sh

# 4. Run application
./run.sh
```

**If Homebrew Java:**
```bash
# Add to ~/.zshrc or ~/.bash_profile
export JAVA_HOME=$(/usr/libexec/java_home)
export PATH=$JAVA_HOME/bin:$PATH
```

### **Windows Command Prompt/PowerShell**

**Using Command Prompt:**
```cmd
REM 1. Open Command Prompt (Win + R, type "cmd")

REM 2. Navigate to project
cd C:\Users\%USERNAME%\Downloads\owsb-project

REM 3. Run application
java -jar target\OWSB.jar

REM Or if JAR doesn't exist:
java -cp target\classes com.owsb.OWSB
```

**Using PowerShell:**
```powershell
# 1. Open PowerShell (Win + X, select PowerShell)

# 2. Navigate to project
cd C:\Users\$env:USERNAME\Downloads\owsb-project

# 3. Run application
java -jar target/OWSB.jar
```

**Using WSL (Windows Subsystem for Linux):**
```bash
# 1. Open WSL terminal

# 2. Navigate to Windows directory
cd /mnt/c/Users/$(whoami)/Downloads/owsb-project

# 3. Follow Linux instructions
chmod +x build.sh run.sh
./run.sh
```

### **Linux Terminal**

**Ubuntu/Debian:**
```bash
# 1. Open Terminal (Ctrl + Alt + T)

# 2. Install Java if needed
sudo apt update
sudo apt install openjdk-11-jdk

# 3. Navigate to project
cd ~/Downloads/owsb-project

# 4. Make scripts executable
chmod +x build.sh run.sh

# 5. Run application
./run.sh
```

**CentOS/RHEL:**
```bash
# 1. Install Java if needed
sudo yum install java-11-openjdk-devel

# 2. Follow same steps as Ubuntu
cd ~/Downloads/owsb-project
chmod +x build.sh run.sh
./run.sh
```

---

## 📝 **Quick Reference Commands**

### **Essential Commands:**
```bash
# Navigate to project
cd /path/to/owsb-project

# Check Java version
java -version

# Make scripts executable (Unix/Linux/macOS)
chmod +x build.sh run.sh

# Build project
./build.sh

# Run application
./run.sh

# Direct Java execution
java -jar target/OWSB.jar
java -cp target/classes com.owsb.OWSB

# Check current directory
pwd

# List files
ls        # Unix/Linux/macOS
dir       # Windows
```

### **File Structure Check:**
```bash
# You should see these files/directories:
ls -la

# Expected output:
# build.sh
# run.sh
# pom.xml
# README.md
# src/
# target/
# data/
# docs/
```

---

## 🎯 **Success Indicators**

### **Build Success:**
```
==========================================
Build completed successfully!
==========================================
JAR file created successfully: target/OWSB.jar
```

### **Application Start Success:**
```
==========================================
OWSB - Omega Wholesale Management System
Starting Application...
==========================================
Running from JAR file...
Creating LoginScreen...
```

### **Login Success:**
```
Authentication successful for user: admin
Login successful, opening dashboard...
Dashboard created, making it visible...
```

---

## 🆘 **Getting Help**

If you encounter issues:

1. **Check Prerequisites**: Ensure Java is properly installed
2. **Verify File Structure**: Make sure all project files are present
3. **Check Permissions**: Ensure scripts are executable
4. **Review Error Messages**: Read terminal output carefully
5. **Try Manual Compilation**: Use the manual compilation steps
6. **Check Documentation**: Review README.md and other docs

**Emergency Manual Run:**
```bash
# If all else fails, try this step-by-step:
cd /path/to/owsb-project
mkdir -p target/classes
cp -r src/main/resources/* target/classes/
find src/main/java -name "*.java" -exec javac -d target/classes -cp target/classes {} +
java -cp target/classes com.owsb.OWSB
```

---

**🎉 You're now ready to run OWSB on any laptop terminal!**

*For additional help, check the other documentation files in the `docs/` directory.* 