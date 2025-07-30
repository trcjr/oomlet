#!/bin/bash

# OOMlet Kind Cluster Cleanup Script
# This script removes the Kind cluster and cleans up local DNS entries

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to cleanup Kind cluster
cleanup_cluster() {
    print_status "Checking for existing Kind cluster..."

    if kind get clusters | grep -q "kind"; then
        print_status "Deleting Kind cluster 'kind'..."
        kind delete cluster --name kind
        print_success "Kind cluster deleted"
    else
        print_warning "No Kind cluster 'kind' found"
    fi
}

# Function to cleanup DNS entries
cleanup_dns() {
    print_status "Cleaning up DNS entries..."

    if grep -q "oomlet.local" /etc/hosts; then
        print_status "Removing oomlet.local from /etc/hosts..."
        sudo sed -i '' '/oomlet.local/d' /etc/hosts
        print_success "DNS entries cleaned up"
    else
        print_warning "No oomlet.local entries found in /etc/hosts"
    fi
}

# Function to show cleanup summary
show_summary() {
    echo
    print_success "🧹 Cleanup complete!"
    echo
    echo "📋 What was cleaned up:"
    echo "  ✅ Kind cluster 'kind' (if it existed)"
    echo "  ✅ DNS entries for oomlet.local"
    echo
    echo "💡 To set up again, run:"
    echo "  ./scripts/setup-kind.sh"
}

# Main execution
main() {
    echo "🧹 OOMlet Kind Cluster Cleanup"
    echo "==============================="
    echo

    cleanup_cluster
    cleanup_dns
    show_summary
}

# Run main function
main "$@"
