#!/bin/bash

# OOMlet Kind Cluster Setup Script
# This script creates a Kind cluster with ingress support and deploys OOMlet

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

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."

    local missing_tools=()

    if ! command_exists kind; then
        missing_tools+=("kind")
    fi

    if ! command_exists kubectl; then
        missing_tools+=("kubectl")
    fi

    if ! command_exists helm; then
        missing_tools+=("helm")
    fi

    if [ ${#missing_tools[@]} -ne 0 ]; then
        print_error "Missing required tools: ${missing_tools[*]}"
        echo "Please install the missing tools:"
        echo "  kind: https://kind.sigs.k8s.io/docs/user/quick-start/#installation"
        echo "  kubectl: https://kubernetes.io/docs/tasks/tools/"
        echo "  helm: https://helm.sh/docs/intro/install/"
        exit 1
    fi

    print_success "All prerequisites are installed"
}

# Function to cleanup existing cluster
cleanup_existing() {
    if kind get clusters | grep -q "kind"; then
        print_warning "Existing Kind cluster 'kind' found"
        read -p "Do you want to delete the existing cluster? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            print_status "Deleting existing Kind cluster..."
            kind delete cluster --name kind
            print_success "Existing cluster deleted"
        else
            print_error "Please delete the existing cluster manually or choose a different name"
            exit 1
        fi
    fi
}

# Function to create Kind cluster
create_cluster() {
    print_status "Creating Kind cluster with ingress support..."

    if [ ! -f "kind-config.yaml" ]; then
        print_error "kind-config.yaml not found in current directory"
        exit 1
    fi

    kind create cluster --name kind --config kind-config.yaml

    print_success "Kind cluster created successfully"
}

# Function to install NGINX ingress controller
install_ingress() {
    print_status "Installing NGINX Ingress Controller..."

    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

    print_status "Waiting for ingress controller to be ready..."
    kubectl wait --namespace ingress-nginx \
        --for=condition=ready pod \
        --selector=app.kubernetes.io/component=controller \
        --timeout=120s

    print_success "NGINX Ingress Controller installed and ready"
}

# Function to deploy OOMlet
deploy_oomlet() {
    print_status "Deploying OOMlet with Helm..."

    if [ ! -f "helm/values-kind.yaml" ]; then
        print_error "helm/values-kind.yaml not found"
        exit 1
    fi

    helm install oomlet ./helm -f helm/values-kind.yaml

    print_status "Waiting for OOMlet pods to be ready..."
    kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=oomlet --timeout=120s

    print_success "OOMlet deployed successfully"
}

# Function to setup local DNS
setup_dns() {
    print_status "Setting up local DNS entry..."

    if ! grep -q "oomlet.local" /etc/hosts; then
        echo "127.0.0.1 oomlet.local" | sudo tee -a /etc/hosts
        print_success "Added oomlet.local to /etc/hosts"
    else
        print_warning "oomlet.local already exists in /etc/hosts"
    fi
}

# Function to test deployment
test_deployment() {
    print_status "Testing deployment..."

    # Wait a moment for ingress to be ready
    sleep 5

    if curl -s http://oomlet.local/actuator/health > /dev/null; then
        print_success "OOMlet is accessible at http://oomlet.local"
    else
        print_warning "OOMlet might not be ready yet. Please wait a moment and try:"
        echo "  curl http://oomlet.local/actuator/health"
    fi
}

# Function to show next steps
show_next_steps() {
    echo
    print_success "🎉 Kind cluster setup complete!"
    echo
    echo "📋 Next steps:"
    echo "  1. Test the deployment:"
    echo "     curl http://oomlet.local/actuator/health"
    echo
    echo "  2. Try some stress tests:"
    echo "     # Memory allocation (causes OutOfMemoryError)"
    echo "     curl 'http://oomlet.local/api/allocate-memory?bytes=2147483648'"
    echo
    echo "     # Application crash (simulates pod failure)"
    echo "     curl -X POST 'http://oomlet.local/api/crash?code=137'"
    echo
    echo "     # CPU stress"
    echo "     curl 'http://oomlet.local/api/burn-cpu?millis=5000&threads=4'"
    echo
    echo "  3. View cluster status:"
    echo "     kubectl get pods"
    echo "     kubectl get ingress"
    echo
    echo "  4. Clean up when done:"
    echo "     kind delete cluster --name kind"
    echo
    echo "📚 For more information, see the README.md file"
}

# Main execution
main() {
    echo "🚀 OOMlet Kind Cluster Setup"
    echo "=============================="
    echo

    check_prerequisites
    cleanup_existing
    create_cluster
    install_ingress
    deploy_oomlet
    setup_dns
    test_deployment
    show_next_steps
}

# Run main function
main "$@"
