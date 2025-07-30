# Workflow Validation Tools Setup

This document describes the validation tools we've configured to ensure GitHub Actions workflows are correct before they're committed.

## 🛠 **Tools Installed**

### **1. actionlint**
- **Purpose**: Validates GitHub Actions workflow files
- **Installation**: `brew install actionlint`
- **Usage**: `actionlint .github/workflows/`
- **What it checks**:
  - YAML syntax
  - Action references
  - Shell script issues
  - Workflow structure

### **2. yamllint**
- **Purpose**: Validates YAML syntax
- **Installation**: `brew install yamllint`
- **Usage**: `yamllint .github/workflows/`
- **Configuration**: `.yamllint`
- **What it checks**:
  - YAML syntax errors
  - Indentation
  - Line length
  - Trailing spaces

### **3. shellcheck**
- **Purpose**: Validates shell scripts in workflows
- **Installation**: `brew install shellcheck`
- **Usage**: `shellcheck scripts/*.sh`
- **What it checks**:
  - Shell script syntax
  - Security issues
  - Best practices

### **4. pre-commit**
- **Purpose**: Runs validation hooks before commits
- **Installation**: `brew install pre-commit`
- **Configuration**: `.pre-commit-config.yaml`
- **What it does**:
  - Automatically runs all validators
  - Prevents commits with errors
  - Fixes minor issues automatically

## 📁 **Configuration Files**

### **`.yamllint`**
```yaml
extends: default

rules:
  line-length: disable
  document-start: disable
  trailing-spaces: disable
  comments-indentation: disable
  empty-lines: disable
  indentation:
    spaces: 2
    indent-sequences: true
```

### **`.pre-commit-config.yaml`**
```yaml
repos:
  - repo: https://github.com/rhysd/actionlint
    rev: v1.6.25
    hooks:
      - id: actionlint
        args: [--verbose]
        files: \.github/workflows/.*\.ya?ml$

  - repo: https://github.com/adrienverge/yamllint
    rev: v1.33.0
    hooks:
      - id: yamllint
        args: [--config-file=.yamllint]
        files: \.ya?ml$
        exclude: ^helm/

  - repo: https://github.com/shellcheck-py/shellcheck-py
    rev: v0.9.0.5
    hooks:
      - id: shellcheck
        args: [--severity=warning]
        files: \.(sh|bash)$

  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.5.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
        exclude: ^helm/
      - id: check-added-large-files
        args: ['--maxkb=1000']
      - id: check-merge-conflict
```

## 🚀 **Usage**

### **Manual Validation**
```bash
# Validate all workflows
actionlint .github/workflows/
yamllint .github/workflows/
shellcheck scripts/*.sh

# Validate specific files
actionlint .github/workflows/security-scan.yml
yamllint .github/workflows/security-scan.yml
```

### **Automatic Validation (pre-commit)**
```bash
# Install hooks (already done)
pre-commit install

# Run on all files
pre-commit run --all-files

# Run on specific files
pre-commit run --files .github/workflows/security-scan.yml

# Run specific hooks
pre-commit run actionlint
pre-commit run yamllint
```

## ✅ **What Gets Validated**

### **Before Every Commit**
- ✅ GitHub Actions workflow syntax
- ✅ YAML syntax (excluding Helm files)
- ✅ Shell script syntax and security
- ✅ Trailing whitespace
- ✅ File endings
- ✅ Merge conflicts
- ✅ Large files (>1MB)

### **Common Issues Fixed**
- Shell variable quoting: `$VAR` → `"$VAR"`
- Trailing whitespace removal
- Missing newlines at end of files
- YAML indentation issues

## 🔧 **Troubleshooting**

### **If pre-commit fails**
```bash
# Skip hooks for this commit (not recommended)
git commit --no-verify

# Run hooks manually to see errors
pre-commit run --all-files

# Update hooks to latest versions
pre-commit autoupdate
```

### **If actionlint fails**
```bash
# Check specific workflow
actionlint .github/workflows/security-scan.yml

# Check all workflows with verbose output
actionlint --verbose .github/workflows/
```

### **If yamllint fails**
```bash
# Check specific file
yamllint .github/workflows/security-scan.yml

# Check with custom config
yamllint --config-file=.yamllint .github/workflows/
```

## 📋 **Best Practices**

1. **Always run validation before pushing**
   ```bash
   pre-commit run --all-files
   ```

2. **Fix issues locally before committing**
   - Don't rely on CI to catch workflow errors
   - Use the tools to validate locally first

3. **Keep tools updated**
   ```bash
   pre-commit autoupdate
   brew upgrade actionlint yamllint shellcheck
   ```

4. **Exclude Helm files**
   - Helm uses different YAML syntax
   - Excluded from yamllint and check-yaml hooks

## 🎯 **Benefits**

- **Faster feedback**: Catch errors before pushing
- **Consistent quality**: All workflows validated
- **Security**: Shell script security checks
- **Automation**: No manual validation needed
- **Team consistency**: Same validation for everyone
