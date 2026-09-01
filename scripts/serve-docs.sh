#!/bin/bash

# OOMlet GitHub Pages Local Development Script
# This script helps you serve the GitHub Pages site locally for development

set -e

echo "🥚 OOMlet GitHub Pages Local Development"
echo "========================================"

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Check if docs directory exists
if [ ! -d "docs" ]; then
    echo "❌ Error: docs directory not found"
    exit 1
fi

# Check if Jekyll is installed
if ! command -v jekyll &> /dev/null; then
    echo "⚠️  Jekyll not found. Installing..."
    echo "Please install Jekyll first:"
    echo "  gem install jekyll bundler"
    echo ""
    echo "Or on macOS with Homebrew:"
    echo "  brew install ruby"
    echo "  gem install jekyll bundler"
    exit 1
fi

# Build the project to generate coverage reports
echo "🔨 Building project to generate coverage reports..."
./mvnw clean verify

# Check if coverage reports were generated
if [ ! -d "target/site/jacoco" ]; then
    echo "❌ Error: Coverage reports not generated. Check the build output."
    exit 1
fi

# Copy coverage reports to docs directory
echo "📊 Copying coverage reports to docs directory..."
rm -rf docs/jacoco
cp -r target/site/jacoco docs/

# Navigate to docs directory
cd docs

echo "🚀 Starting Jekyll server..."
echo "📱 Site will be available at: http://localhost:4000/oomlet/"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Start Jekyll server
jekyll serve --host 0.0.0.0 --port 4000 --baseurl /oomlet
