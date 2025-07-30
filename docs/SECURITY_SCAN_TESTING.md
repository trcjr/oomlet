# Security Scan Testing Guide

This document explains how to test the security scan workflow before contributing fixes.

## Prerequisites

1. **NVD API Key** (Optional for v8.4.0, Required for v9.0.10+):
   - For the current workflow (v8.4.0): No API key required
   - For newer versions (v9.0.10+): Get a free API key from: https://nvd.nist.gov/developers/request-an-api-key
   - Add it to your GitHub repository secrets as `NVD_API_KEY`

## Testing Approaches

### 1. Local Testing (Quick Feedback)

Run the local test script to validate the dependency check works:

```bash
./scripts/test-dependency-check.sh
```

**Note**: This will work with the legacy version (8.4.0) without an NVD API key. For newer versions, an API key is required.

### 2. GitHub Actions Test Workflows

We've created two test workflows that can be run manually:

#### Option A: Standalone Tool Test
- **File**: `.github/workflows/test-security-scan.yml`
- **How to run**: Go to Actions tab → "Test Security Scan" → "Run workflow"
- **Pros**: Tests the exact same approach as the main workflow
- **Cons**: Requires NVD API key for newer versions

#### Option B: Maven Plugin Test
- **File**: `.github/workflows/test-security-scan-maven.yml`
- **How to run**: Go to Actions tab → "Test Security Scan (Maven Plugin)" → "Run workflow"
- **Pros**: Often more reliable, uses Maven ecosystem
- **Cons**: Different approach from main workflow

### 3. Manual GitHub Actions Run

You can also manually trigger the main security scan workflow:
- **File**: `.github/workflows/security-scan.yml`
- **How to run**: Go to Actions tab → "Security Scan" → "Run workflow"

## What to Look For

### ✅ Success Indicators
- Workflow completes without errors
- Reports are generated and uploaded as artifacts
- SARIF report is uploaded to GitHub Security tab
- No null pointer exceptions or template errors
- NVD data downloads successfully (for v8.4.0)

### ❌ Failure Indicators
- `Cannot invoke "String.toUpperCase()" because the return value is null`
- NVD API errors (403/404) - indicates API key issue with newer versions
- Missing dependencies or configuration issues

## Troubleshooting

### NVD API Key Issues
If you get 403/404 errors:
1. **For v8.4.0**: This shouldn't happen - the version works without an API key
2. **For v9.0.10+**:
   - Verify your NVD API key is valid
   - Check that the secret is properly set in GitHub
   - Ensure the key has the correct permissions
   - Consider using v8.4.0 as a fallback

### Version Compatibility
If you encounter template errors:
1. Try updating to a newer version of OWASP Dependency Check
2. Check if there are known issues with the current version
3. Consider using the Maven plugin approach instead

### Local vs CI Differences
- Local environment may have different Java versions
- CI environment has different network access
- Always test in CI environment for final validation

## Contributing the Fix

Once you've validated the fix works:

1. **Test locally** with the script
2. **Test in CI** using one of the test workflows
3. **Update the main workflow** with your changes
4. **Create a pull request** with:
   - Description of the issue
   - Explanation of the fix
   - Test results showing it works
   - Any new dependencies or requirements

## Files Modified

- `.github/workflows/security-scan.yml` - Main workflow (updated)
- `.github/workflows/test-security-scan.yml` - Test workflow (new)
- `.github/workflows/test-security-scan-maven.yml` - Alternative test (new)
- `scripts/test-dependency-check.sh` - Local test script (new)
- `docs/SECURITY_SCAN_TESTING.md` - This documentation (new)
