#!/bin/bash

# Test script for OWASP Dependency Check
# This script tests the dependency check locally using Maven

set -e

echo "🧪 Testing OWASP Dependency Check locally..."

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

echo "🔍 Running OWASP Dependency Check via Maven..."

# Run dependency check using Maven plugin
./mvnw org.owasp:dependency-check-maven:9.0.10:check \
    -Dformat=HTML \
    -Dformat=JSON \
    -Dformat=SARIF \
    -DfailOnCVSS=8 \
    -DretireJsUrl=https://raw.githubusercontent.com/RetireJS/retire.js/master/repository/jsrepository.json \
    -DlogFile=target/dependency-check/dependency-check.log \
    -DnvdApiKey="$NVD_API_KEY"

echo "✅ Dependency check completed!"
echo "📁 Reports generated in target/dependency-check/"

# List generated files
echo "📋 Generated files:"
ls -la target/dependency-check/

echo "🎉 Test completed successfully!"
