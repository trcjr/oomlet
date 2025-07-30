#!/bin/bash

# Test script for OWASP Dependency Check (Legacy Version)
# This script tests the dependency check locally using an older version

set -e

echo "🧪 Testing OWASP Dependency Check locally (Legacy Version)..."

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found. Please run this script from the project root."
    exit 1
fi

# Check Java version
echo "📋 Java version:"
java -version

# Check Maven version
echo "📋 Maven version:"
./mvnw -version

# Create output directory
mkdir -p target/dependency-check

echo "🔍 Running OWASP Dependency Check via Maven (Legacy Version)..."

# Run dependency check using Maven plugin with older version
./mvnw org.owasp:dependency-check-maven:8.4.0:check \
    -Dformat=HTML \
    -Dformat=JSON \
    -Dformat=SARIF \
    -DfailOnCVSS=8 \
    -DretireJsUrl=https://raw.githubusercontent.com/RetireJS/retire.js/master/repository/jsrepository.json \
    -DlogFile=target/dependency-check/dependency-check.log

echo "✅ Dependency check completed!"
echo "📁 Reports generated in target/dependency-check/"

# List generated files
echo "📋 Generated files:"
ls -la target/dependency-check/

echo "🎉 Test completed successfully!"
