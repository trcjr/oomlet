#!/bin/bash

# OOMlet GitHub Pages Local Development with Docker
# This script helps you serve the GitHub Pages site locally using Docker

set -e

echo "🥚 OOMlet GitHub Pages Local Development (Docker)"
echo "=================================================="

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

# Create a temporary Dockerfile for Jekyll
echo "🐳 Creating temporary Dockerfile for Jekyll..."
cat > docs/Dockerfile.jekyll << 'EOF'
FROM ruby:3.2-slim

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /site

# Copy Gemfile and install dependencies
COPY Gemfile Gemfile.lock* ./
RUN bundle install

# Copy site content
COPY . .

# Expose port
EXPOSE 4000

# Start Jekyll server
CMD ["bundle", "exec", "jekyll", "serve", "--host", "0.0.0.0", "--port", "4000", "--baseurl", "/oomlet"]
EOF

# Create a temporary Gemfile.lock if it doesn't exist
if [ ! -f "docs/Gemfile.lock" ]; then
    echo "📦 Creating Gemfile.lock..."
    cd docs
    docker run --rm -v "$(pwd):/site" -w /site ruby:3.2-slim bash -c "
        apt-get update && apt-get install -y build-essential git
        bundle install
    "
    cd ..
fi

# Build the Docker image
echo "🔨 Building Jekyll Docker image..."
cd docs
docker build -f Dockerfile.jekyll -t oomlet-jekyll .

# Clean up temporary Dockerfile
rm Dockerfile.jekyll

echo "🚀 Starting Jekyll server in Docker..."
echo "📱 Site will be available at: http://localhost:4000/oomlet/"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Run the Jekyll container
docker run --rm -p 4000:4000 -v "$(pwd):/site" oomlet-jekyll
