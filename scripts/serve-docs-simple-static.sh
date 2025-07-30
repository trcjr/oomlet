#!/bin/bash

# OOMlet GitHub Pages Local Development with Static File Server
# Simple script to serve the GitHub Pages site locally using nginx

set -e

echo "🥚 OOMlet GitHub Pages Local Development (Static Server)"
echo "========================================================"

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

# Create a simple nginx configuration
echo "🐳 Creating nginx configuration..."
cat > docs/nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       4000;
        server_name  localhost;
        root         /usr/share/nginx/html;
        index        index.html;

        location / {
            try_files $uri $uri/ /index.html;
        }

        location /oomlet/ {
            alias /usr/share/nginx/html/;
            try_files $uri $uri/ /index.html;
        }

        # Enable gzip compression
        gzip on;
        gzip_vary on;
        gzip_min_length 1024;
        gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
    }
}
EOF

echo "🚀 Starting nginx server..."
echo "📱 Site will be available at: http://localhost:4000/"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Run nginx container
docker run --rm -p 4000:4000 -v "$(pwd)/docs:/usr/share/nginx/html" -v "$(pwd)/docs/nginx.conf:/etc/nginx/nginx.conf" nginx:alpine
