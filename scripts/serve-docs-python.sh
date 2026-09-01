#!/bin/bash

# OOMlet GitHub Pages Local Development with Python HTTP Server
# Simple script to serve the GitHub Pages site locally using Python

set -e

echo "🥚 OOMlet GitHub Pages Local Development (Python HTTP Server)"
echo "============================================================="

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

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo "❌ Error: Python 3 is not installed or not available"
    echo "Please install Python 3 first: https://www.python.org/downloads/"
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

# Create a simple Python HTTP server script
echo "🐍 Creating Python HTTP server..."
cat > docs/server.py << 'EOF'
#!/usr/bin/env python3
import http.server
import socketserver
import os
import sys

class CustomHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        # Add CORS headers for local development
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def do_GET(self):
        # Handle the /oomlet/ base path
        if self.path.startswith('/oomlet/'):
            self.path = self.path[8:]  # Remove /oomlet/ prefix
        elif self.path == '/oomlet':
            self.path = '/'

        # Serve index.html for directory requests
        if self.path.endswith('/') or self.path == '':
            self.path = '/index.html'

        return super().do_GET()

if __name__ == "__main__":
    PORT = 4000

    # Change to the directory containing this script
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    with socketserver.TCPServer(("", PORT), CustomHTTPRequestHandler) as httpd:
        print(f"🚀 Server started at http://localhost:{PORT}/")
        print(f"📱 Site will be available at http://localhost:{PORT}/")
        print("🛑 Press Ctrl+C to stop the server")
        print("")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\n👋 Server stopped.")
EOF

echo "🚀 Starting Python HTTP server..."
echo "📱 Site will be available at: http://localhost:4000/"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Start the Python HTTP server
cd docs
python3 server.py
