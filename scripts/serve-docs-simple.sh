#!/bin/bash

# OOMlet GitHub Pages Local Development with Docker Compose
# Simple script to serve the GitHub Pages site locally using Docker

set -e

echo "🥚 OOMlet GitHub Pages Local Development (Docker Compose)"
echo "========================================================="

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

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker is not installed or not available"
    echo "Please install Docker first: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Error: Docker Compose is not installed or not available"
    echo "Please install Docker Compose first: https://docs.docker.com/compose/install/"
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

# Install Jekyll dependencies in the container
echo "📦 Installing Jekyll dependencies..."
cd docs
docker run --rm -v "$(pwd):/srv/jekyll" -w /srv/jekyll jekyll/jekyll:4.2.2 bundle install
cd ..

echo "🚀 Starting Jekyll server with Docker Compose..."
echo "📱 Site will be available at: http://localhost:4000/oomlet/"
echo "🔄 Live reload enabled - changes will auto-refresh"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Start the Jekyll server using Docker Compose
if command -v docker-compose &> /dev/null; then
    docker-compose -f docker-compose.docs.yml up
else
    docker compose -f docker-compose.docs.yml up
fi
