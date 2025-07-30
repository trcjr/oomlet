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
